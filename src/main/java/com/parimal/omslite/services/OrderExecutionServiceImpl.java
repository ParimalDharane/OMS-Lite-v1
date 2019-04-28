package com.parimal.omslite.services;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.OBStatus;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import com.parimal.omslite.repo.ExecutionRepository;
import com.parimal.omslite.repo.OrderRepository;
import com.parimal.omslite.repo.OrderbookRepository;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.parimal.omslite.utils.OMSLiteConstants.*;

/**
 * Default implementation of OrderExecutionService
 * @see OrderExecutionService
 */
@NoArgsConstructor
@Service
public class OrderExecutionServiceImpl implements OrderExecutionService {

    private Logger logger = LoggerFactory.getLogger(OrderExecutionServiceImpl.class);

    @Autowired
    private OrderbookRepository orderbookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Override
    public Orderbook addOrderbook(String instrumentName) throws OMSException {
        if(instrumentName == null || orderbookRepository.findByInstrumentName(instrumentName) != null) {
            throw OMSException.createUnprocessableEntity(DATA_ALREADY_PRESENT + " Instrument=" + instrumentName);
        }
        return orderbookRepository.save(new Orderbook(instrumentName));
    }

    @Override
    public Orderbook closeOrderbook(Long id) throws OMSException {
        Orderbook orderbook = orderbookRepository.findById(id).orElse(null);
        if(orderbook == null) {
            throw OMSException.createNotFound(DATA_NOT_FOUND + " Book Id=" + id);
        }
        if(!orderbook.getStatus().equals(OBStatus.Open)) {
            throw OMSException.createUnprocessableEntity(ORDERBOOK_NOT_OPEN + " Book Id=" + id);
        }
        orderbook.setStatus(OBStatus.Close);
        return orderbookRepository.save(orderbook);
    }

    @Override
    public Order addOrder(Order order) throws OMSException {
        Orderbook orderbook = orderbookRepository.findById(order.getBookId()).orElse(null);
        if(orderbook == null || !orderbook.getStatus().equals(OBStatus.Open)) {
            throw OMSException.createUnprocessableEntity(ORDERBOOK_NOT_OPEN + " Book Id=" + order.getBookId());
        }
        if(order.getQuantity() <= 0) {
            throw OMSException.createUnprocessableEntity(ORDER_NOT_VALID_QUANTITY + " Book Id=" + order.getBookId() + ", Quantity=" + order.getQuantity());
        }
        if(order.getPrice() < 0) {
            throw OMSException.createUnprocessableEntity(ORDER_NOT_VALID_PRICE + " Book Id=" + order.getBookId() + ", Price=" + order.getPrice());
        }
        if(order.getMarketOrder() && order.getPrice() > 0) {
            order.setPrice(0.0); //market order
        } else if(order.getPrice() == 0) {
            order.setMarketOrder(true); //convert to market order
        }
        return orderRepository.save(order);
    }

    @Override
    public Execution addExecution(Execution execution) throws OMSException {
        Orderbook orderbook = orderbookRepository.findById(execution.getBookId()).orElse(null);
        if(orderbook == null || !orderbook.getStatus().equals(OBStatus.Close)) {
            throw OMSException.createUnprocessableEntity(ORDERBOOK_NOT_CLOSE + " Book Id=" + execution.getBookId());
        }
        if(execution.getQuantity() <= 0) {
            throw OMSException.createUnprocessableEntity(EXECUTION_NOT_VALID_QUANTITY + " Book Id=" + execution.getBookId() + ", Quantity=" + execution.getQuantity());
        }
        if(execution.getPrice() < 0) {
            throw OMSException.createUnprocessableEntity(EXECUTION_NOT_VALID_PRICE + " Book Id=" + execution.getBookId() + ", Price=" + execution.getPrice());
        }

        execution = executeOrders(orderbook, execution);
        return executionRepository.save(execution);
    }

    @Override
    public Order getOrderDetails(Long bookId, Long orderId) throws OMSException {
        Order order = orderRepository.findByBookIdAndId(bookId, orderId);
        if(order == null) {
            throw OMSException.createNotFound(DATA_NOT_FOUND + " Book Id=" + bookId + ", Order Id=" + orderId);
        }
        return order;
    }

    private Execution executeOrders(Orderbook orderbook, Execution execution)  {
        //Check if this is a first execution for this orderbook
        final Execution firstExecution = executionRepository.getFirstExecution(orderbook.getId());
        if(firstExecution == null) {
            //mark other orders invalid
            List<Order> nonEligibleOrderList = orderRepository.findAllNonEligible(orderbook.getId(), execution.getPrice());
            nonEligibleOrderList
                    .stream()
                    .parallel()
                    .forEach(order -> {
                        order.setValid(false);
                        order.setExecutionPrice(0.0);
                        orderRepository.save(order);
                    });
        } else {
            //check for price and make the correction accordingly
            if(!firstExecution.getPrice().equals(execution.getPrice())) {
                logger.warn("Price of consecutive execution was not matching with the first one. Overriding. Instrument={}, Curr Invalid Price={}, Expected Price={}",
                        execution.getInstrumentName(), execution.getPrice(), firstExecution.getPrice());
                execution.setPrice(firstExecution.getPrice()); //override the price
            }
        }
        //find orders which are valid and matching price criteria
        List<Order> eligibleOrderList = orderRepository.findAllEligible(orderbook.getId(), execution.getPrice());
        //calculate open demand - for linear distribution
        final Long openDemand = eligibleOrderList
                                    .stream()
                                    .parallel()
                                    .mapToLong(Order::getOpenQuantity).sum();
        final Long availableQuantity = execution.getQuantity() - execution.getExecutedQuantity();
        eligibleOrderList.forEach(order ->
                executeOrderLinear(order, execution, openDemand, availableQuantity)
        );
        //check if there is some un-executed quantity due to fraction
        long remainingQuantity = execution.getQuantity() - execution.getExecutedQuantity();
        if(remainingQuantity < openDemand && remainingQuantity > 0L) {
                //distribute the remaining quantity again for first n orders
                Long targetExecQty = 1L;
                eligibleOrderList
                        .stream()
                        .parallel()
                        .limit(remainingQuantity)
                        .forEach(order -> {
                            executeOrder(order, execution, targetExecQty);
                        });
        }
        //save orders
        boolean anyOpenQty = false;
        for(Order o : eligibleOrderList) {
            orderRepository.save(o);
            if(!anyOpenQty && o.getOpenQuantity() > 0) {
                anyOpenQty = true;
            }
        }
        if(!anyOpenQty) {
            orderbook.setStatus(OBStatus.Executed);
            orderbookRepository.save(orderbook);
        }
        return execution;
    }

    private void executeOrderLinear(Order order, Execution execution, Long currOpenDemand, Long availableQuanty) {
        Long orderOpenQty = order.getOpenQuantity();
        Long targetExecQty = (long)Math.floor(((double)orderOpenQty / currOpenDemand) * availableQuanty);

        //if there is not enough open quantity on order is available then use order's open quantity
        targetExecQty = (targetExecQty > orderOpenQty) ? orderOpenQty : targetExecQty;
        execution.setExecutedQuantity(execution.getExecutedQuantity() + targetExecQty);
        order.setOpenQuantity(order.getOpenQuantity() - targetExecQty);
        order.setExecutionPrice(execution.getPrice());
    }
    private void executeOrder(Order order, Execution execution, Long targetExecQty) {
        if(order.getOpenQuantity() <= 0L) {
            return;
        }
        execution.setExecutedQuantity(execution.getExecutedQuantity() + targetExecQty);
        order.setOpenQuantity(order.getOpenQuantity() - targetExecQty);
    }
}


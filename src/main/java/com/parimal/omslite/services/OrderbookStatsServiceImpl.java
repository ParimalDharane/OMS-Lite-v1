package com.parimal.omslite.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import com.parimal.omslite.entities.OrderbookStats;
import com.parimal.omslite.repo.ExecutionRepository;
import com.parimal.omslite.repo.OrderRepository;
import com.parimal.omslite.repo.OrderbookRepository;

import lombok.NoArgsConstructor;

import static com.parimal.omslite.utils.OMSLiteConstants.DATA_NOT_FOUND;

/**
 * Default implementation of OrderbookStatsService
 * @see OrderbookStatsService
 */
@NoArgsConstructor
@Service
public class OrderbookStatsServiceImpl implements OrderbookStatsService {

    @Autowired
    private OrderbookRepository orderbookRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ExecutionRepository executionRepository;

    @Override
    public OrderbookStats calculateStats(Long bookId) throws OMSException {
        Orderbook orderbook = orderbookRepository.findById(bookId).orElseThrow(() ->
                OMSException.createNotFound(DATA_NOT_FOUND + " Book Id=" + bookId));


        final OrderbookStats orderbookStats = new OrderbookStats(orderbook);
        List<Order> orderList = orderRepository.findAllByBookId(orderbook.getId());
        if(orderList.size()<= 0) {
            return orderbookStats;
        }
        Order firstOrder = orderList.get(0);

        orderbookStats.setBiggestOrder(firstOrder);
        orderbookStats.setSmallestOrder(firstOrder);
        orderbookStats.setFirstOrder(firstOrder);
        orderbookStats.setLastOrder(firstOrder);

        orderList.forEach(order -> {
            //Original Demand
            orderbookStats.setOrgDemand(orderbookStats.getOrgDemand() + order.getQuantity());

            //Open Demand
            orderbookStats.setOpenDemand(orderbookStats.getOpenDemand() + order.getOpenQuantity());

            //limit breakdown for All
            Long demand = orderbookStats.getLimitBreakdown().get(order.getPrice());
            demand = (demand == null) ? 0L : demand;
            orderbookStats.getLimitBreakdown().put(order.getPrice(), demand + order.getQuantity());

            if(order.getValid()) {
                //Original Demand for Valid
                orderbookStats.setOrgDemandValid(orderbookStats.getOrgDemandValid() + order.getQuantity());

                //limit breakdown for Valid
                demand = orderbookStats.getLimitBreakdownValid().get(order.getPrice());
                demand = (demand == null) ? 0L : demand;
                orderbookStats.getLimitBreakdownValid().put(order.getPrice(), demand + order.getQuantity());
            } else {
                //Original Demand for Invalid
                orderbookStats.setOrgDemandInvalid(orderbookStats.getOrgDemandInvalid() + order.getQuantity());

                //limit breakdown for Invalid
                demand = orderbookStats.getLimitBreakdownInvalid().get(order.getPrice());
                demand = (demand == null) ? 0L : demand;
                orderbookStats.getLimitBreakdownInvalid().put(order.getPrice(), demand + order.getQuantity());
            }

            //Biggest Order
            if(order.getQuantity() > orderbookStats.getBiggestOrder().getQuantity()) {
                orderbookStats.setBiggestOrder(order); //got a new biggest
            }

            //Smallest Order
            if(order.getQuantity() < orderbookStats.getSmallestOrder().getQuantity()) {
                orderbookStats.setSmallestOrder(order); //got a new biggest
            }

            //first order
            if(order.getId() < orderbookStats.getFirstOrder().getId()) {
                orderbookStats.setFirstOrder(order);
            }

            //last order
            if(order.getId() > orderbookStats.getLastOrder().getId()) {
                orderbookStats.setLastOrder(order);
            }
        });

        //Cal for execution numbers
        List<Execution> executionList = executionRepository.findAllByBookId(orderbook.getId());
        if(executionList.size()<= 0) {
            return orderbookStats;
        }
        Execution firstExecution = executionList.get(0);
        orderbookStats.setExecutionPrice(firstExecution.getPrice());

        executionList.forEach(execution -> {
            //Original Demand
            orderbookStats.setExecutedQuantity(orderbookStats.getExecutedQuantity() + execution.getExecutedQuantity());
        });

        return orderbookStats;
    }

}


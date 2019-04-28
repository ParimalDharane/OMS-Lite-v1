package com.parimal.omslite.controllers;

import com.parimal.omslite.dto.*;
import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import com.parimal.omslite.services.OrderExecutionService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * REST API for resources like Orderbook, Order and Execution
 */
@Api(value = "OrderbookController", description = "REST APIs to manage and operate entities - Orderbook, Order and Execution")
@RestController
@RequestMapping("/api/orderbooks")
public class OrderbookController {

    @Autowired
    private OrderExecutionService orderExecutionService;

    @PostMapping(consumes = { "application/json"}
        ,produces = { "application/json"} )
    public Resource<OrderbookDTO> newOrderBook(@RequestBody OrderbookReq orderbookReq) {
        Orderbook orderbook = orderExecutionService.addOrderbook(orderbookReq.getInstrumentName());
        return new Resource<>(DTOUtils.toOrderbookDTO(orderbook));
    }

    @PostMapping(value = "/{bookId}/orders"
        ,consumes = { "application/json"}
        ,produces = { "application/json"} )
    public Resource<OrderDTO> newOrder(@Valid @PathVariable Long bookId, @RequestBody OrderReq orderReq) {
        Order order = orderExecutionService.addOrder(DTOUtils.toOrder(orderReq, bookId));
        return new Resource<>(DTOUtils.toOrderDTO(order));
    }

    @PutMapping(value = "/{id}"
        ,produces = { "application/json"} )
    public Resource<OrderbookDTO> updateOrderbook(@PathVariable Long id) {
        Orderbook orderbook = orderExecutionService.closeOrderbook(id);
        return new Resource<>(DTOUtils.toOrderbookDTO(orderbook));
    }

    @PostMapping(value = "/{bookId}/executions"
            ,consumes = { "application/json"}
            ,produces = { "application/json"} )
    public Resource<ExecutionDTO> newExecution(@Valid @PathVariable Long bookId, @RequestBody ExecutionReq executionReq) {
        Execution execution = orderExecutionService.addExecution(DTOUtils.toExecution(executionReq, bookId));
        return new Resource<>(DTOUtils.toExecutionDTO(execution));
    }

    @GetMapping(value = "/{bookId}/orders/{id}"
            ,produces = { "application/json"} )
    public Resource<OrderDTO> getOrderDetails(@PathVariable Long bookId, @PathVariable Long id) {
        return  new Resource<>(DTOUtils.toOrderDTO(orderExecutionService.getOrderDetails(bookId, id)));
    }
}

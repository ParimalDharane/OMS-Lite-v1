package com.parimal.omslite.services;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import org.springframework.stereotype.Service;

/**
 * Business service interface for managing various operations on Orderbook, Order and Execution
 */
@Service
public interface OrderExecutionService {

    Orderbook addOrderbook(String instrumentName) throws OMSException;

    Orderbook closeOrderbook(Long id) throws OMSException;

    Order addOrder(Order order) throws OMSException;

    Execution addExecution(Execution execution) throws OMSException;

    Order getOrderDetails(Long bookId, Long orderId) throws OMSException;
}

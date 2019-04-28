package com.parimal.omslite.services;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.OBStatus;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;

import java.util.ArrayList;
import java.util.List;

public class TestDataUtils {

    static final Long bookId = 1L;
    static final String instrumentName = "INS1";
    static final Orderbook defOrderbook = new Orderbook(bookId, instrumentName, OBStatus.Open);

    static final Double priceDelta = 0.0001;

    static final List<Order> defOrderList = new ArrayList<>();
    static final List<Execution> defExecutionList = new ArrayList<>();

    static {
        defOrderList.add(new Order(2L, bookId, instrumentName, 100L, 10.0, false));
        defOrderList.add(new Order(3L, bookId, instrumentName, 100L, 10.0, false));

        defExecutionList.add(new Execution(5L, bookId, instrumentName, 100L, 10.0));
    }
}

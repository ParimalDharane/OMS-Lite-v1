package com.parimal.omslite.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OrderbookStats entity - a data model consists of various attributes order book statistics
 */
@Data
@Getter
@Setter
@ToString
public class OrderbookStats {
    private Orderbook orderbook;

    private Long orgDemand;
    private Long openDemand;

    private Long orgDemandValid;
    private Long orgDemandInvalid;

    private Order biggestOrder;
    private Order smallestOrder;
    private Order firstOrder;
    private Order lastOrder;

    private Map<Double, Long> limitBreakdown; //Key=Price, Value=Demand (accumulated quantity)
    private Map<Double, Long> limitBreakdownValid; //Key=Price, Value=Demand (accumulated quantity)
    private Map<Double, Long> limitBreakdownInvalid; //Key=Price, Value=Demand (accumulated quantity)

    private Double executionPrice;

    private Long executedQuantity;

    public OrderbookStats(Orderbook orderbook) {
        init();
        this.orderbook = orderbook;
    }
    private void init() {
        this.orderbook = null;

        this.orgDemand = 0L;
        this.openDemand = 0L;

        this.orgDemandValid = 0L;
        this.orgDemandInvalid = 0L;

        this.biggestOrder = null;
        this.smallestOrder = null;
        this.firstOrder = null;
        this.lastOrder = null;

        limitBreakdown = new ConcurrentHashMap<>();
        limitBreakdownValid = new ConcurrentHashMap<>();
        limitBreakdownInvalid = new ConcurrentHashMap<>();

        this.executionPrice = 0.0;
        this.executedQuantity = 0L;
    }
}

package com.parimal.omslite.dto;

import com.parimal.omslite.entities.OBStatus;
import lombok.*;

import java.util.Hashtable;
import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderbookStatsDTO {
    private Long orderbookId;
    private String instrumentName;
    private OBStatus orderbookStatus;

    private Long orgDemand;
    private Long openDemand;

    private Long orgDemandValid;
    private Long orgDemandInvalid;

    private OrderDTO biggestOrder;
    private OrderDTO smallestOrder;
    private OrderDTO firstOrder;
    private OrderDTO lastOrder;

    private Map<Double, Long> limitBreakdown = new Hashtable<>(); //Key=Price, Value=Demand (accumulated quantity)
    private Map<Double, Long> limitBreakdownValid = new Hashtable<>(); //Key=Price, Value=Demand (accumulated quantity)
    private Map<Double, Long> limitBreakdownInvalid = new Hashtable<>(); //Key=Price, Value=Demand (accumulated quantity)

    private Double executionPrice;

    private Long executedQuantity;
}

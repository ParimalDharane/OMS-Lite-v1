package com.parimal.omslite.controllers;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import com.parimal.omslite.dto.*;
import com.parimal.omslite.entities.OrderbookStats;
import com.parimal.omslite.services.OMSException;

/**
 * Utility for convert business objects to/from DTO objects
 */
public class DTOUtils {

    public static OrderbookDTO toOrderbookDTO(Orderbook in) {
        if(in == null) {
            return null;
        }
        OrderbookDTO out = new OrderbookDTO();
        out.setId(in.getId());
        out.setInstrumentName(in.getInstrumentName());
        out.setStatus(in.getStatus());
        return out;
    }
    public static Order toOrder(OrderReq req, Long bookId) {
        if(req == null) {
            return null;
        }
        if(req.getPrice() == null || req.getPrice() <= 0.0) {
            req.setMarketOrder(true);
        }
        if(req.getMarketOrder()) {
            req.setPrice(0.0);
        }
        return new Order(bookId, req.getInstrumentName(), req.getQuantity(), req.getPrice(), req.getMarketOrder());
    }
    public static OrderDTO toOrderDTO(Order in) {
        if(in == null) {
            return null;
        }
        OrderDTO out = new OrderDTO();
        out.setId(in.getId());
        out.setBookId(in.getBookId());
        out.setInstrumentName(in.getInstrumentName());
        out.setOrderQuantity(in.getQuantity());
        out.setOrderPrice(in.getPrice());
        out.setMarketOrder(in.getMarketOrder());
        out.setValid(in.getValid());
        out.setExecutionPrice(in.getExecutionPrice());
        out.setExecutionQuantity(in.getQuantity() - in.getOpenQuantity());
        return out;
    }
    public static Execution toExecution(ExecutionReq req, Long bookId) {
        if(req == null) {
            return null;
        }
        Execution out = new Execution(bookId, req.getInstrumentName(), req.getQuantity(), req.getPrice());
        out.setExecutedQuantity(0L);
        return out;
    }
    public static ExecutionDTO toExecutionDTO(Execution in) {
        if(in == null) {
            return null;
        }
        ExecutionDTO out = new ExecutionDTO();
        out.setId(in.getId());
        out.setBookId(in.getBookId());
        out.setInstrumentName(in.getInstrumentName());
        out.setPrice(in.getPrice());
        out.setQuantity(in.getQuantity());
        out.setExecutedQuantity(in.getExecutedQuantity());
        return out;
    }
    public static OrderbookStatsDTO toOrderbookStatsDTO(OrderbookStats in) {
        if(in == null) {
            return null;
        }
        OrderbookStatsDTO out = new OrderbookStatsDTO();
        out.setOrderbookId(in.getOrderbook().getId());
        out.setInstrumentName(in.getOrderbook().getInstrumentName());
        out.setOrderbookStatus(in.getOrderbook().getStatus());

        out.setOrgDemand(in.getOrgDemand());
        out.setOpenDemand(in.getOpenDemand());
        out.setOrgDemandValid(in.getOrgDemandValid());
        out.setOrgDemandInvalid(in.getOrgDemandInvalid());

        out.setBiggestOrder(toOrderDTO(in.getBiggestOrder()));
        out.setSmallestOrder(toOrderDTO(in.getSmallestOrder()));
        out.setFirstOrder(toOrderDTO(in.getFirstOrder()));
        out.setLastOrder(toOrderDTO(in.getLastOrder()));

        out.getLimitBreakdown().putAll(in.getLimitBreakdown());
        out.getLimitBreakdownValid().putAll(in.getLimitBreakdownValid());
        out.getLimitBreakdownInvalid().putAll(in.getLimitBreakdownInvalid());

        out.setExecutionPrice(in.getExecutionPrice());
        out.setExecutedQuantity(in.getExecutedQuantity());

        return out;
    }
    public static OMSErrorDTO toOMSErrorDTO(OMSException in) {
        OMSErrorDTO out = new OMSErrorDTO();
        out.setErrorCode(in.getErrorCode());
        out.setMessage(in.getMessage());
        return out;
    }
}

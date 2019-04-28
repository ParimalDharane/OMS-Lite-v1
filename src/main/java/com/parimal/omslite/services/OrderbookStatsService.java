package com.parimal.omslite.services;

import com.parimal.omslite.entities.OrderbookStats;
import org.springframework.stereotype.Service;

/**
 * Business service interface for calculating order book statistics
 */
@Service
public interface OrderbookStatsService {

    OrderbookStats calculateStats(Long bookId) throws OMSException;

}

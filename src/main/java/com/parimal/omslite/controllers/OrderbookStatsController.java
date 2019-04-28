package com.parimal.omslite.controllers;

import com.parimal.omslite.dto.OrderbookStatsDTO;
import com.parimal.omslite.services.OrderbookStatsService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API for resources like Order book statistics
 */
@Api(value = "OrderbookStatsController", description = "REST APIs to calculate and get Orderbook statistics")
@RestController
@RequestMapping("/api/stats")
public class OrderbookStatsController {

    @Autowired
    private OrderbookStatsService service;

    @GetMapping( value = "/{bookId}"
            ,produces = { "application/json"} )
    public Resource<OrderbookStatsDTO> findStatsById(@PathVariable Long bookId) {
        return new Resource<>(DTOUtils.toOrderbookStatsDTO(service.calculateStats(bookId)));
    }
}

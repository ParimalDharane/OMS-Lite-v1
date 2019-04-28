package com.parimal.omslite.services;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.OrderbookStats;
import com.parimal.omslite.repo.ExecutionRepository;
import com.parimal.omslite.repo.OrderRepository;
import com.parimal.omslite.repo.OrderbookRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.parimal.omslite.services.TestDataUtils.*;


@SpringBootTest
public class OrderbookStatsServiceTest {

    @Mock
    private OrderbookRepository orderbookRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ExecutionRepository executionRepository;

    @InjectMocks
    private OrderbookStatsServiceImpl statsService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void whenDataIsMissing_thenNoErrors() {
        Mockito.when(orderbookRepository.findById(bookId)).
                thenReturn(Optional.of(defOrderbook));

        OrderbookStats stats = statsService.calculateStats(bookId);
        Assert.assertNotNull(stats);
        Assert.assertEquals(instrumentName, stats.getOrderbook().getInstrumentName());
    }

    @Test
    public void whenExecutionIsMissing_thenNoErrors() {
        Mockito.when(orderbookRepository.findById(bookId)).
                thenReturn(Optional.of(defOrderbook));

        Mockito.when(orderRepository.findAllByBookId(bookId)).
                thenReturn(defOrderList);

        OrderbookStats stats = statsService.calculateStats(bookId);
        Assert.assertNotNull(stats);
        Assert.assertEquals(instrumentName, stats.getOrderbook().getInstrumentName());
        Assert.assertEquals(200L, stats.getOrgDemand().longValue());
    }

    @Test
    public void whenPartiallyExecuted_thenResultMatches() {
        Mockito.when(orderbookRepository.findById(bookId)).
                thenReturn(Optional.of(defOrderbook));

        Mockito.when(orderRepository.findAllByBookId(bookId)).
                thenReturn(defOrderList);

        Mockito.when(executionRepository.findAllByBookId(bookId)).
                thenReturn(defExecutionList);

        OrderbookStats stats = statsService.calculateStats(bookId);
        Assert.assertNotNull(stats);
        Assert.assertEquals(instrumentName, stats.getOrderbook().getInstrumentName());
        Assert.assertEquals(200L, stats.getOrgDemand().longValue());
        Assert.assertEquals(10.0, stats.getExecutionPrice(), priceDelta);
    }

    @Test
    public void whenFullyExecuted_thenVerifyExecutedQuantity() {
        List<Order> orderList = new ArrayList<>();
        List<Execution> executionList = new ArrayList<>();

        Order o1 = new Order(2L, bookId, instrumentName, 600L, 10.0, false);
        o1.setOpenQuantity(0L);
        orderList.add(o1);

        Execution e1 = new Execution(5L, bookId, instrumentName, 100L, 10.0);
        e1.setExecutedQuantity(100L);
        Execution e2 = new Execution(5L, bookId, instrumentName, 200L, 10.0);
        e2.setExecutedQuantity(200L);
        Execution e3 = new Execution(5L, bookId, instrumentName, 300L, 10.0);
        e3.setExecutedQuantity(300L);
        executionList.add(e1);
        executionList.add(e2);
        executionList.add(e3);

        Mockito.when(orderbookRepository.findById(bookId)).thenReturn(Optional.of(defOrderbook));
        Mockito.when(orderRepository.findAllByBookId(bookId)).thenReturn(orderList);
        Mockito.when(executionRepository.findAllByBookId(bookId)).thenReturn(executionList);

        OrderbookStats stats = statsService.calculateStats(bookId);
        Assert.assertNotNull(stats);
        Assert.assertEquals(instrumentName, stats.getOrderbook().getInstrumentName());
        Assert.assertEquals(0L, stats.getOpenDemand().longValue());
        Assert.assertEquals(600L, stats.getExecutedQuantity().longValue());
    }

}

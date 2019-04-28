package com.parimal.omslite.services;

import com.parimal.omslite.entities.Execution;
import com.parimal.omslite.entities.OBStatus;
import com.parimal.omslite.entities.Order;
import com.parimal.omslite.entities.Orderbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static com.parimal.omslite.services.TestDataUtils.instrumentName;


@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-daotest.properties")
public class OrderExecutionServiceTest {
    private MockMvc mockMvc;

    @Autowired
    private OrderExecutionServiceImpl orderExecutionService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Transactional
    public void whenAddOrderbook_thenOrderbookIsAdded() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);
    }

    @Test
    @Transactional
    public void whenCloseOrderbook_thenOrderbookIsClosed() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());
    }

    @Test
    @Transactional
    public void whenAddOrder_thenOrderIsAdded() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order expected = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        Order actual = orderExecutionService.addOrder(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
        Assert.assertEquals(expected.getMarketOrder(), actual.getMarketOrder());
    }

    @Test
    @Transactional
    public void whenAddOrderAsMarketOrder_thenOrderIsAdded() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Double price = 0.0;
        Boolean marketOrder = true;
        Order expected = new Order(orderbook.getId(), instrumentName, 110L, price, marketOrder);
        Order actual = orderExecutionService.addOrder(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(price, actual.getPrice());
        Assert.assertEquals(marketOrder, actual.getMarketOrder());
    }

    @Test
    @Transactional
    public void whenAddExecution_thenExecutionIsAdded() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order order = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        orderExecutionService.addOrder(order);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution expected = new Execution(orderbook.getId(), instrumentName, 220L, 11.0);
        Execution actual = orderExecutionService.addExecution(expected);

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
    }

    @Test
    @Transactional
    public void whenGetOrderDetails_thenOrderDetailsAreReturned() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order expected = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        expected = orderExecutionService.addOrder(expected);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution execution = new Execution(orderbook.getId(), instrumentName, 220L, 11.0);
        execution = orderExecutionService.addExecution(execution);
        Assert.assertNotNull(execution);

        Order actual = orderExecutionService.getOrderDetails(orderbook.getId(), expected.getId());

        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(true, actual.getValid());
        Assert.assertEquals(execution.getExecutedQuantity().longValue(), (actual.getQuantity() - actual.getOpenQuantity()));
        Assert.assertEquals(expected.getPrice(), actual.getPrice());
        Assert.assertEquals(execution.getPrice(), actual.getExecutionPrice());
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddOrderbookDuplicate_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        orderExecutionService.addOrderbook(instrumentName); //should throw exception
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenCloseOrderbookNotPresent_thenThrowsException() {
        Long bookId = 99L; //id not exists
        Orderbook orderbook = orderExecutionService.closeOrderbook(bookId); //should throw exception
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenCloseOrderbookAlreadyClosed_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        //Try to close again
        orderExecutionService.closeOrderbook(orderbook.getId()); //should throw exception
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddOrderAndBookIsClose_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Order expected = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        orderExecutionService.addOrder(expected);
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddOrderWithNegativeQuantity_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order expected = new Order(orderbook.getId(), instrumentName, -250L, 11.0, false);
        orderExecutionService.addOrder(expected);
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddOrderWithNegativePrice_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order expected = new Order(orderbook.getId(), instrumentName, 100L, -5.0, false);
        orderExecutionService.addOrder(expected);
    }

    @Test
    @Transactional
    public void whenAddOrderAsMarketWithPositivePrice_thenOrderIsAddedWithPriceAsZero() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Double givenPrice = 11.0;
        Double expectedPrice = 0.0;
        Boolean marketOrder = true;
        Order expected = new Order(orderbook.getId(), instrumentName, 110L, givenPrice, marketOrder);
        Order actual = orderExecutionService.addOrder(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(expectedPrice, actual.getPrice());
        Assert.assertEquals(marketOrder, actual.getMarketOrder());
    }

    @Test
    @Transactional
    public void whenAddOrderWithZeroPrice_thenOrderIsAddedAsMarketOrder() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Double givenPrice = 0.0;
        Boolean givenMarketOrder = false;
        Boolean expectedMarketOrder = true;
        Order expected = new Order(orderbook.getId(), instrumentName, 110L, givenPrice, givenMarketOrder);
        Order actual = orderExecutionService.addOrder(expected);
        Assert.assertNotNull(actual);
        Assert.assertEquals(expected.getQuantity(), actual.getQuantity());
        Assert.assertEquals(givenPrice, actual.getPrice());
        Assert.assertEquals(expectedMarketOrder, actual.getMarketOrder());
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddExecutionAndBookIsOpen_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Execution expected = new Execution(orderbook.getId(), instrumentName, 220L, 11.0);
        orderExecutionService.addExecution(expected);
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddExecutionAndBookIsExecuted_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order order = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        orderExecutionService.addOrder(order);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution expected = new Execution(orderbook.getId(), instrumentName, order.getQuantity(), order.getPrice());
        orderExecutionService.addExecution(expected);

        //orderbook must be "Executed" and adding another execution should throw exception
        orderExecutionService.addExecution(expected);
    }

    @Test
    @Transactional
    public void whenTotalQuantityMatchesExecution_thenNoOpenQuantity() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order o1 = new Order(orderbook.getId(), instrumentName, 110L, 11.0, false);
        o1 = orderExecutionService.addOrder(o1);

        Order o2 = new Order(orderbook.getId(), instrumentName, 190L, 11.0, false);
        o2 = orderExecutionService.addOrder(o2);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution execution = new Execution(orderbook.getId(), instrumentName, 300L, 11.0);
        execution = orderExecutionService.addExecution(execution);
        Assert.assertNotNull(execution);

        Order actual = orderExecutionService.getOrderDetails(orderbook.getId(), o1.getId());
        Assert.assertNotNull(actual);
        Assert.assertEquals(o1.getId(), actual.getId());
        Assert.assertEquals(0L, actual.getOpenQuantity().longValue());

        Order actual2 = orderExecutionService.getOrderDetails(orderbook.getId(), o2.getId());
        Assert.assertNotNull(actual2);
        Assert.assertEquals(o2.getId(), actual2.getId());
        Assert.assertEquals(0L, actual2.getOpenQuantity().longValue());
    }

    @Test
    @Transactional
    public void whenPriceIsLess_thenOrderBecomesInvalid() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order expectedInvalid = new Order(orderbook.getId(), instrumentName, 100L, 2.0, false);
        expectedInvalid = orderExecutionService.addOrder(expectedInvalid);

        Order o2 = new Order(orderbook.getId(), instrumentName, 100L, 10.0, false);
        o2 = orderExecutionService.addOrder(o2);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution execution = new Execution(orderbook.getId(), instrumentName, 200L, 10.0);
        execution = orderExecutionService.addExecution(execution);
        Assert.assertNotNull(execution);

        Order actual = orderExecutionService.getOrderDetails(orderbook.getId(), expectedInvalid.getId());
        Assert.assertNotNull(actual);
        Assert.assertEquals(expectedInvalid.getId(), actual.getId());
        Assert.assertFalse(actual.getValid());
    }

    @Test
    @Transactional
    public void whenAddExecutionWithDifferentPrice_thenPriceGetsMatchedWithFirst() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order o1 = new Order(orderbook.getId(), instrumentName, 200L, 10.0, false);
        o1 = orderExecutionService.addOrder(o1);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Execution eFirst10 = new Execution(orderbook.getId(), instrumentName, 100L, 10.0);
        eFirst10 = orderExecutionService.addExecution(eFirst10);
        Assert.assertNotNull(eFirst10);

        Execution eSecond12 = new Execution(orderbook.getId(), instrumentName, 100L, 12.0);
        eSecond12 = orderExecutionService.addExecution(eSecond12);
        Assert.assertNotNull(eSecond12);

        Order actual = orderExecutionService.getOrderDetails(orderbook.getId(), o1.getId());
        Assert.assertNotNull(actual);
        Assert.assertEquals(eFirst10.getPrice(), actual.getExecutionPrice());
        Assert.assertEquals(0L, actual.getOpenQuantity().longValue());
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddExecutionWithNegativeQuantity_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order o1 = new Order(orderbook.getId(), instrumentName, 200L, 10.0, false);
        o1 = orderExecutionService.addOrder(o1);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Long givenQuantity = -5L;
        Double givenPrice = 15.0;
        Execution execution = new Execution(orderbook.getId(), instrumentName, givenQuantity, givenPrice);
        orderExecutionService.addExecution(execution);
    }

    @Test (expected = OMSException.class)
    @Transactional
    public void whenAddExecutionWithNegativePrice_thenThrowsException() {
        Orderbook orderbook = orderExecutionService.addOrderbook(instrumentName);
        Assert.assertNotNull(orderbook);
        Assert.assertTrue(orderbook.getId() > 0);

        Order o1 = new Order(orderbook.getId(), instrumentName, 200L, 10.0, false);
        o1 = orderExecutionService.addOrder(o1);

        orderbook = orderExecutionService.closeOrderbook(orderbook.getId());
        Assert.assertEquals(OBStatus.Close, orderbook.getStatus());

        Double givenPrice = -15.0;
        Execution execution = new Execution(orderbook.getId(), instrumentName, 100L, givenPrice);
        orderExecutionService.addExecution(execution);
    }

}

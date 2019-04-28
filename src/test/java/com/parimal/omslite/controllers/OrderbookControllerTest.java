package com.parimal.omslite.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.parimal.omslite.dto.OrderDTO;
import com.parimal.omslite.dto.OrderbookDTO;

import static com.parimal.omslite.controllers.TestUtils.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-daotest.properties")
public class OrderbookControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    @Transactional
    public void verifyAddOrderbook() throws Exception {
        addOrderbook(mockMvc);
    }

    @Test
    @Transactional
    public void verifyAddOrder() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());
    }

    @Test
    @Transactional
    public void verifyCloseOrderbook() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        closeOrderbook(mockMvc, savedBook.getId());
    }

    @Test
    @Transactional
    public void verifyAddExecution() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        closeOrderbook(mockMvc, savedBook.getId());
        addExecution(mockMvc, savedBook.getId());
    }

    @Test
    @Transactional
    public void whenOrderbookIsOpen_thenAddExecutionFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addExecution(mockMvc, savedBook.getId(), jsonDefaultAddExecution, status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    public void whenOrderbookIsExecuted_thenAddExecutionFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        closeOrderbook(mockMvc, savedBook.getId());
        addExecution(mockMvc, savedBook.getId());
        addExecution(mockMvc, savedBook.getId(), jsonDefaultAddExecution, status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    public void whenOrderbookIsClose_thenAddOrderFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        closeOrderbook(mockMvc, savedBook.getId());
        addOrder(mockMvc, savedBook.getId(), jsonDefaultAddOrder, status().isUnprocessableEntity());
    }
    @Test
    @Transactional
    public void verifyGetOrder() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        OrderDTO savedOrder = addOrder(mockMvc, savedBook.getId());
        getOneAndCheckProperty(mockMvc, uriGetOrder.replace("{bookId}", "" + savedBook.getId() ) + savedOrder.getId(), "$.id", savedOrder.getId().intValue() );
    }

    @Test
    @Transactional
    public void whenDuplicate_thenAddOrderbookFails() throws Exception {
        addOrderbook(mockMvc);
        //add another orderbook for the same instrument
        addOrderbook(mockMvc, status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    public void whenBookNotValid_thenAddOrderFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        //add an order with orderbook id which is not present
        addOrder(mockMvc, savedBook.getId() + 100, jsonDefaultAddOrder, status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    public void whenBookNotValid_thenCloseOrderbookFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        String uriOrderbooksById2 = "/api/orderbooks/" + savedBook.getId() + 100;
        putAndCheckStatus(mockMvc, uriOrderbooksById2, jsonUpdateOrderbookForClose, status().isNotFound());
    }

    @Test
    @Transactional
    public void whenBookNotValid_thenAddExecutionFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        closeOrderbook(mockMvc, savedBook.getId());
        //add an execution with orderbook id which is not present
        addExecution(mockMvc, savedBook.getId() + 100, jsonDefaultAddExecution, status().isUnprocessableEntity());
    }

    @Test
    @Transactional
    public void whenBookNotValid_thenGetOrderFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        OrderDTO savedOrder = addOrder(mockMvc, savedBook.getId());
        getOneAndCheckProperty(mockMvc, uriGetOrder.replace("{bookId}", "" + savedBook.getId() ) + savedOrder.getId(), "$.id", savedOrder.getId().intValue() );

        String uriGetOrder100 = "/api/orderbooks/" + savedBook.getId() + "/orders/" + (savedOrder.getId() + 100); //Order id which is not present
        gettAndCheckStatus(mockMvc, uriGetOrder100, status().isNotFound());
    }
}

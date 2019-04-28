package com.parimal.omslite.controllers;

import com.parimal.omslite.dto.OrderDTO;
import com.parimal.omslite.dto.OrderbookDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static com.parimal.omslite.controllers.TestUtils.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-daotest.properties")
public class OrderbookStatsControllerTest {
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void init() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context).build();
    }

    @Test
    @Transactional
    public void whenBookNotValid_thenGetStatsFails() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        OrderDTO orderDTO = addOrder(mockMvc, savedBook.getId());

        String uriGetStats99 = "/api/stats/" + (orderDTO.getId() + 100); //invalid book id
        gettAndCheckStatus(mockMvc, uriGetStats99, status().isNotFound());
    }

    @Test
    @Transactional
    public void verifyStatsDetailsForOneOrder() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());
        closeOrderbook(mockMvc, savedBook.getId());
        addExecution(mockMvc, savedBook.getId());
        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(100)))
                .andExpect(jsonPath("$.openDemand", is(0)))
                .andExpect(jsonPath("$.orgDemandValid", is(100)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(100)));
    }

    @Test
    @Transactional
    public void verifyStatsDetailsForTwoOrders() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 12.0, \"quantity\": 200}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        closeOrderbook(mockMvc, savedBook.getId());
        addExecution(mockMvc, savedBook.getId());
        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(300)))
                .andExpect(jsonPath("$.openDemand", is(0)))
                .andExpect(jsonPath("$.orgDemandValid", is(300)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(300)));
    }

    @Test
    @Transactional
    public void verifyStatsDetailsForTwoExecutions() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 12.0, \"quantity\": 600}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        String jsonAddOrder3 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 12.0, \"quantity\": 300}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder3);

        closeOrderbook(mockMvc, savedBook.getId());

        addExecution(mockMvc, savedBook.getId());
        String jsonAddExecution2 = "{ \"instrumentName\": \"INS1\", \"price\": 11.0, \"quantity\": 200}";
        addExecution(mockMvc, savedBook.getId(), jsonAddExecution2, status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(1000)))
                .andExpect(jsonPath("$.openDemand", is(0)))
                .andExpect(jsonPath("$.orgDemandValid", is(1000)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(1000)));

    }

    @Test
    @Transactional
    public void verifyStatsDetailsForInvalidOrders() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 8.0, \"quantity\": 600}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        String jsonAddOrder3 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 12.0, \"quantity\": 1200}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder3);

        closeOrderbook(mockMvc, savedBook.getId());

        addExecution(mockMvc, savedBook.getId());
        String jsonAddExecution2 = "{ \"instrumentName\": \"INS1\", \"price\": 11.0, \"quantity\": 200}";
        addExecution(mockMvc, savedBook.getId(), jsonAddExecution2, status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(1900)))
                .andExpect(jsonPath("$.openDemand", is(900)))
                .andExpect(jsonPath("$.orgDemandValid", is(1300)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(600)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(1000)));
    }


    @Test
    @Transactional
    public void verifyStatsDetailsForShorterExecution() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 200}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        String jsonAddOrder3 = "{ \"bookId\": 1, \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 300}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder3);

        closeOrderbook(mockMvc, savedBook.getId());

        String jsonAddExecution1 = "{ \"instrumentName\": \"INS1\", \"price\": 10.0, \"quantity\": 405}";
        addExecution(mockMvc, savedBook.getId(), jsonAddExecution1, status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(600)))
                .andExpect(jsonPath("$.openDemand", is(195)))
                .andExpect(jsonPath("$.orgDemandValid", is(600)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(405)));
    }

    @Test
    @Transactional
    public void verifyStatsDetailsForEqualExecution() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 100}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        String jsonAddOrder3 = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 100}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder3);

        closeOrderbook(mockMvc, savedBook.getId());

        String jsonAddExecution1 = "{ \"instrumentName\": \"INS1\", \"price\": 10.0, \"quantity\": 300}";
        addExecution(mockMvc, savedBook.getId(), jsonAddExecution1, status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(300)))
                .andExpect(jsonPath("$.openDemand", is(0)))
                .andExpect(jsonPath("$.orgDemandValid", is(300)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(300)));
    }

    @Test
    @Transactional
    public void verifyStatsDetailsForGreaterExecution() throws Exception {
        OrderbookDTO savedBook = addOrderbook(mockMvc);
        addOrder(mockMvc, savedBook.getId());

        String jsonAddOrder2 = "{ \"bookId\": 1, \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 100}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder2);

        String jsonAddOrder3 = "{ \"bookId\": 1, \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 100}";
        addOrder(mockMvc, savedBook.getId(), jsonAddOrder3);

        closeOrderbook(mockMvc, savedBook.getId());

        String jsonAddExecution1 = "{ \"instrumentName\": \"INS1\", \"price\": 10.0, \"quantity\": 303}";
        addExecution(mockMvc, savedBook.getId(), jsonAddExecution1, status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get(uriGetStats + savedBook.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orgDemand", is(300)))
                .andExpect(jsonPath("$.openDemand", is(0)))
                .andExpect(jsonPath("$.orgDemandValid", is(300)))
                .andExpect(jsonPath("$.orgDemandInvalid", is(0)))
                .andExpect(jsonPath("$.executionPrice", is(10.0)))
                .andExpect(jsonPath("$.executedQuantity", is(300)));
    }
}

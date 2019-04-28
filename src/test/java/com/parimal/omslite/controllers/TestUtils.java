package com.parimal.omslite.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parimal.omslite.dto.ExecutionDTO;
import com.parimal.omslite.dto.OrderDTO;
import com.parimal.omslite.dto.OrderbookDTO;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
public class TestUtils {

    public static final String uriOrderbooks = "/api/orderbooks";
    public static final String uriOrderbooksById = "/api/orderbooks/{bookId}";
    public static final String uriOrders = "/api/orderbooks/{bookId}/orders";
    public static final String uriExecutions = "/api/orderbooks/{bookId}/executions";
    public static final String uriGetOrder = "/api/orderbooks/{bookId}/orders/";
    public static final String uriGetStats = "/api/stats/";

    public static final String jsonAddOrderbooks = "{ \"instrumentName\": \"INS1\"}";
    public static final String jsonDefaultAddOrder = "{ \"instrumentName\": \"INS1\", \"marketOrder\": false, \"price\": 10.0, \"quantity\": 100}";
    public static final String jsonUpdateOrderbookForClose = "";

    public static final String jsonDefaultAddExecution = "{ \"instrumentName\": \"INS1\", \"price\": 10.0, \"quantity\": 800}";

    public static String statsResult = "{\"orderbook\":{\"id\":1,\"instrumentName\":\"INS1\",\"status\":\"Executed\"},\"orgDemand\":100,\"openDemand\":0,\"orgDemandValid\":100,\"orgDemandInvalid\":0,\"biggestOrder\":{\"id\":2,\"bookId\":1,\"instrumentName\":\"INS1\",\"quantity\":100,\"price\":10.0,\"marketOrder\":false,\"entryDate\":\"2019-04-19T17:38:30.976\",\"valid\":true,\"openQuantity\":0},\"smallestOrder\":{\"id\":2,\"bookId\":1,\"instrumentName\":\"INS1\",\"quantity\":100,\"price\":10.0,\"marketOrder\":false,\"entryDate\":\"2019-04-19T17:38:30.976\",\"valid\":true,\"openQuantity\":0},\"firstOrder\":{\"id\":2,\"bookId\":1,\"instrumentName\":\"INS1\",\"quantity\":100,\"price\":10.0,\"marketOrder\":false,\"entryDate\":\"2019-04-19T17:38:30.976\",\"valid\":true,\"openQuantity\":0},\"lastOrder\":{\"id\":2,\"bookId\":1,\"instrumentName\":\"INS1\",\"quantity\":100,\"price\":10.0,\"marketOrder\":false,\"entryDate\":\"2019-04-19T17:38:30.976\",\"valid\":true,\"openQuantity\":0},\"limitBreakdown\":{\"10.0\":100},\"limitBreakdownValid\":{\"10.0\":100},\"limitBreakdownInvalid\":{},\"executionPrice\":10.0,\"executedQuantity\":100}";

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static OrderbookDTO addOrderbook(MockMvc mockMvc) throws Exception {
        return addOrderbook(mockMvc, status().isOk());
    }

    public static OrderbookDTO addOrderbook(MockMvc mockMvc, ResultMatcher expectedStatus) throws Exception {
        return postAndCheckStatus(mockMvc, uriOrderbooks, jsonAddOrderbooks, expectedStatus, OrderbookDTO.class);
    }

    public static void closeOrderbook(MockMvc mockMvc, long bookId) throws Exception {
        putAndCheckStatus(mockMvc, uriOrderbooksById.replace("{bookId}", "" + bookId), jsonUpdateOrderbookForClose, status().isOk());
    }

    public static OrderDTO addOrder(MockMvc mockMvc, long bookId) throws Exception {
        return addOrder(mockMvc, bookId, jsonDefaultAddOrder, status().isOk());
    }

    public static OrderDTO addOrder(MockMvc mockMvc, long bookId, String jsonAddOrder) throws Exception {
        return addOrder(mockMvc, bookId, jsonAddOrder, status().isOk());
    }

    public static OrderDTO addOrder(MockMvc mockMvc, long bookId, String jsonAddOrder, ResultMatcher expectedStatus) throws Exception {
        return postAndCheckStatus(mockMvc, uriOrders.replace("{bookId}", "" + bookId ), jsonAddOrder, expectedStatus, OrderDTO.class);
    }

    public static void addExecution(MockMvc mockMvc, long bookId) throws Exception {
        addExecution(mockMvc, bookId, jsonDefaultAddExecution, status().isOk());
    }

    public static ExecutionDTO addExecution(MockMvc mockMvc, long bookId, String jsonAddExecution, ResultMatcher expectedStatus) throws Exception {
        return postAndCheckStatus(mockMvc, uriExecutions.replace("{bookId}", "" + bookId ), jsonAddExecution, expectedStatus, ExecutionDTO.class);
    }

    public static <T> T postAndCheckStatus(MockMvc mockMvc, String uri, String jsonData, ResultMatcher expectedStatus, Class<T> valueType ) throws Exception {
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(expectedStatus);

        MvcResult result = resultActions.andReturn();
        if(result.getResponse().getStatus() == 200) {
            return objectMapper.readValue(result.getResponse().getContentAsString(), valueType);
        }
        return null;
    }

    public static void putAndCheckStatus(MockMvc mockMvc, String uri, String jsonData, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put(uri)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonData))
                .andExpect(expectedStatus);
    }

    public static void gettAndCheckStatus(MockMvc mockMvc, String uri, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(expectedStatus);
    }

    public static void getOneAndCheckProperty(MockMvc mockMvc, String uri, String property, int value) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath(property, is(value)));
    }

    public static void getOneAndCheckProperty(MockMvc mockMvc, String uri, ResultHandler resultHandler) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(uri)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(resultHandler);
    }

}

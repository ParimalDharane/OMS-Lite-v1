package com.parimal.omslite.entities;

/**
 * On creation of an Orderbook the status will be "Open" by default
 * User is allowed to change the status to "Close" (executions will be accepted only if it is close)
 * When all orders are fully executed then the status will become "Executed"
 */
public enum OBStatus {
    Open
    ,Close
    ,Executed
}

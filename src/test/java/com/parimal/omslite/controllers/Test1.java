package com.parimal.omslite.controllers;

public class Test1 {

    public static void main(String[] args) {
        System.out.println("inside Test1"); //TODO remove this class

        String uriOrders = "/api/orderbooks/{bookid}/orders";

        System.out.println("Before=" + uriOrders + ", After=" + uriOrders.replace("{bookid}", ""+1));

    }
}

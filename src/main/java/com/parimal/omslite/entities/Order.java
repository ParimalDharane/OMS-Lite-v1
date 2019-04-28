package com.parimal.omslite.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Order entity - a data model consists of various attributes of an "Order"
 */
@Entity
@Table(name = "order_table")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long bookId;
    private String instrumentName;
    private Long quantity = 0L;
    private Double price = 0.0;
    private Boolean marketOrder = false;
    private LocalDateTime entryDate = LocalDateTime.now();
    private Boolean valid = true;

    private Long openQuantity;
    private Double executionPrice;

    public Order() {
    }

    public Order(Long bookId, String instrumentName, Long quantity, Double price, Boolean marketOrder) {
        this.bookId = bookId;
        this.instrumentName = instrumentName;
        this.quantity = quantity;
        this.price = price;
        this.marketOrder = marketOrder;
        this.openQuantity = quantity;
        this.executionPrice = 0.0;
    }

    public Order(Long id, Long bookId, String instrumentName, Long quantity, Double price, Boolean marketOrder) {
        this(bookId, instrumentName, quantity, price, marketOrder);
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getInstrumentName() {
        return instrumentName;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getMarketOrder() {
        return marketOrder;
    }
    public void setMarketOrder(Boolean marketOrder) {
        this.marketOrder = marketOrder;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Long getOpenQuantity() {
        return openQuantity;
    }

    public void setOpenQuantity(Long openQuantity) {
        this.openQuantity = openQuantity;
    }

    public Double getExecutionPrice() {
        return executionPrice;
    }

    public void setExecutionPrice(Double executionPrice) {
        this.executionPrice = executionPrice;
    }
}

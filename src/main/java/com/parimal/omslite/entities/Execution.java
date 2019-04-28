package com.parimal.omslite.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Execution entity - a data model consists of various attributes of an "Execution"
 */
@Entity
@Table(name = "execution_table")
public class Execution {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Long bookId;
    private String instrumentName;
    private Long quantity = 0L;
    private Double price = 0.0;

    private Long executedQuantity = 0L;

    public Execution() {

    }
    public Execution(Long bookId, String instrumentName, Long quantity, Double price) {
        this.bookId = bookId;
        this.instrumentName = instrumentName;
        this.quantity = quantity;
        this.price = price;
    }

    public Execution(Long id, Long bookId, String instrumentName, Long quantity, Double price) {
        this(bookId, instrumentName, quantity, price);
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

    public Long getExecutedQuantity() {
        return executedQuantity;
    }

    public void setExecutedQuantity(Long executedQuantity) {
        this.executedQuantity = executedQuantity;
    }
}

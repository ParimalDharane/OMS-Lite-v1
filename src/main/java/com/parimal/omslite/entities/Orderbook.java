package com.parimal.omslite.entities;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

/**
 * Orderbook entity - data model consists of various attributes of an "Orderbook".
 */
@Entity
@Table(name = "orderbook_table")
public class Orderbook {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @NotBlank(message = "Instrument Name is mandatory")
    private String instrumentName;
    private OBStatus status = OBStatus.Open;

    public Orderbook() {
    }
    public Orderbook(String instrumentName) {
        this.instrumentName = instrumentName;
    }

    public Orderbook(Long id, @NotBlank(message = "Instrument Name is mandatory") String instrumentName, OBStatus status) {
        this.id = id;
        this.instrumentName = instrumentName;
        this.status = status;
    }

    public Long getId() {
        return id;
    }
    public String getInstrumentName() {
        return instrumentName;
    }
    public void setInstrumentName(String instrumentName) {
        this.instrumentName = instrumentName;
    }
    public OBStatus getStatus() {
        return status;
    }
    public void setStatus(OBStatus status) {
        this.status = status;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Orderbook orderbook = (Orderbook) o;
        return Objects.equals(id, orderbook.id) && Objects.equals(instrumentName, orderbook.instrumentName) && status == orderbook.status;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, instrumentName, status);
    }
    @Override
    public String toString() {
        return "Orderbook{" + "id=" + id + ", instrumentName='" + instrumentName + '\'' + ", status=" + status + '}';
    }
}

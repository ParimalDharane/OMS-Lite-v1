package com.parimal.omslite.dto;

import com.parimal.omslite.entities.OBStatus;

public class OrderbookDTO {

    private Long id;
    private String instrumentName;
    private OBStatus status;

    public OrderbookDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
    public String toString() {
        return "OrderbookDTO{" +
                "id=" + id +
                ", instrumentName='" + instrumentName + '\'' +
                ", status=" + status +
                '}';
    }
}

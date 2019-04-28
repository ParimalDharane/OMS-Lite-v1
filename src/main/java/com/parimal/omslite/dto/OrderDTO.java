package com.parimal.omslite.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long bookId;
    private String instrumentName;
    private Long orderQuantity = 0L;
    private Boolean valid = true;
    private Double orderPrice = 0.0;
    private Boolean marketOrder = false;
    private Long executionQuantity;
    private Double executionPrice;
}

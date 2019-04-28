package com.parimal.omslite.dto;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionReq {
    private String instrumentName;
    private Long quantity = 0L;
    private Double price = 0.0;
}

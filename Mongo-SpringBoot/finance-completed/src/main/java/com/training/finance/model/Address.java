package com.training.finance.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String city;
    private String state;
    private String country;
}
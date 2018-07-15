package com.zopa.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class Offer implements Comparable {
    private String lender;
    private int amount;
    private BigDecimal rate;

    @Override
    public int compareTo(Object offer) {
        return rate.compareTo(((Offer) offer).rate);
    }
}

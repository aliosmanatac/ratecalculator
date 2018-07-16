package com.zopa.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@EqualsAndHashCode
public class Offer implements Comparable {
    private String lender;
    private int amount;
    private BigDecimal rate;

    @Override
    public int compareTo(Object offer) {
        int result = rate.compareTo(((Offer) offer).rate);
        // avoid equals
        return result == 0 ? 1 : result;
    }
}

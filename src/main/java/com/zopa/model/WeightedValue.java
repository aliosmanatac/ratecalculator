package com.zopa.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class WeightedValue {
    private BigDecimal weight;
    private BigDecimal value;
}

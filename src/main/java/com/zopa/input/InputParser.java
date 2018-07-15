package com.zopa.input;

import com.zopa.model.Offer;
import lombok.NonNull;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static com.zopa.config.Config.LOAN_AMOUNT_MULTIPLIER;
import static com.zopa.config.Config.MAX_LOAN_AMOUNT;
import static com.zopa.config.Config.MIN_LOAN_AMOUNT;


public class InputParser {
    public static final String NAME_REGEX = "^[a-zA-Z0-9]+$";
    public static Offer parseOffer(@NonNull final CSVRecord csvRecord) {
        return Offer.builder()
                .lender(parseName(csvRecord.get("lender")))
                .rate(parseRate(csvRecord.get("rate")))
                .amount(parseAmount(csvRecord.get("available")))
                .build();
    }

    private static String parseName(final String name) {
        if (!Pattern.matches(NAME_REGEX, name)) {
            throw new IllegalArgumentException("Lender name may only contain alpha-numerical characters");
        }
        return name;
    }

    private static BigDecimal parseRate(final String rate) {
        try {
            return new BigDecimal(rate);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid rate [%s]. Rate should be a decimal number", rate));
        }
    }

    public static int parseRequestedAmount(final String requestedAmount) {
        int value = parseAmount(requestedAmount);

        if (value < MIN_LOAN_AMOUNT || value > MAX_LOAN_AMOUNT || value % LOAN_AMOUNT_MULTIPLIER != 0) {
            throw new IllegalArgumentException(String.format("Invalid requested amount [%s]. Amount should be a "
                            + "multiple of %d between %d and %d", requestedAmount, LOAN_AMOUNT_MULTIPLIER, MIN_LOAN_AMOUNT,
                    MAX_LOAN_AMOUNT));
        }
        return value;
    }

    private static int parseAmount(final String amount) {
        int value;
        try {
            value = Integer.parseInt(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid amount [%s]. Amount should be numerical", amount));
        }

        if (value <= 0) {
            throw new IllegalArgumentException(
                    String.format("Invalid amount [%s]. Amount should be positive", amount));
        }
        return value;
    }

}

package com.zopa.input;

import com.zopa.model.Offer;
import org.apache.commons.csv.CSVRecord;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static com.zopa.config.Config.*;

public class InputParser {
    public static Offer parseOffer(final CSVRecord csvRecord) {
        return Offer.builder()
                .lender(parseName(csvRecord.get("lender")))
                .rate(parseRate(csvRecord.get("rate")))
                .amount(parseAmount(csvRecord.get("available")))
                .build();
    }

    private static String parseName(final String name) {
        String nameRegex = "^[a-zA-Z0-9]+$";
        if (!Pattern.matches(nameRegex, name)) {
            throw new IllegalArgumentException("Lender name may only contain alpha-numerical characters");
        }
        return name;
    }

    private static BigDecimal parseRate(final String rate) {
        try {
            return new BigDecimal(rate);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid rate [%s]. Rate should be a decimal number" ,rate));
        }
    }

    public static int parseRequestedAmount(final String requestedAmount) {
        int value = parseAmount(requestedAmount);

        if (value < MIN_LOAN_AMOUNT || value > MAX_LOAN_AMOUNT || value % LOAN_AMOUNT_MULTIPLIER != 0)
            throw new IllegalArgumentException(String.format("Invalid requested amount [%s]. Amount should be a " +
                            "multiple of %d between %d and %d" ,requestedAmount, LOAN_AMOUNT_MULTIPLIER, MIN_LOAN_AMOUNT,
                    MAX_LOAN_AMOUNT));
        return value;
    }

    private static int parseAmount(final String amount) {
        int value;
        try {
            value = Integer.valueOf(amount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid amount [%s]. Amount should be numerical" ,amount));
        }

        if (value <= 0)
            throw new IllegalArgumentException(
                    String.format("Invalid amount [%s]. Amount should be positive" ,amount));

        return value;
    }

}

package com.zopa.app;

import com.zopa.calculator.QuoteCalculator;
import com.zopa.input.CsvParser;
import com.zopa.model.Offer;
import com.zopa.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Optional;
import java.util.TreeSet;

import static com.zopa.config.Config.LOAN_LENGTH_IN_MONTHS;
import static com.zopa.input.InputParser.parseRequestedAmount;
import static org.springframework.util.Assert.notNull;

@ComponentScan(basePackages = "com.zopa")
@Configuration
public class QuoteCalculationApp {

    private QuoteCalculator quoteCalculator;
    private CsvParser csvParser;

    @Autowired
    public QuoteCalculationApp(final QuoteCalculator quoteCalculator, final CsvParser csvParser) {
        notNull(quoteCalculator, "quoteCalculator cannot be null");
        notNull(csvParser, "csvParser cannot be null");
        this.quoteCalculator = quoteCalculator;
        this.csvParser = csvParser;
    }

    public String start(final String[] args) {
        if (args.length != 2)
            return "Usage: quote [market_file] [requested_amount]";

        try {
            // Read market data from file
            TreeSet<Offer> offerSet = csvParser.getOffersFromFile(args[0]);
            int requestedAmount = parseRequestedAmount(args[1]);
            // Calculate quote
            Optional<Quote> quoteOptional = quoteCalculator.calculateQuote(offerSet, requestedAmount, LOAN_LENGTH_IN_MONTHS);
            // return quote as string
            return quoteOptional
                    .map(q -> q.toString())
                    .orElse("No available loans for the current amount");
        } catch (IllegalArgumentException e) {
            return "Error occurred while processing input parameters: " + e.getMessage();
        } catch (IOException e) {
            return "Error occurred while reading the market file: " + e.getMessage();
        }
    }

}

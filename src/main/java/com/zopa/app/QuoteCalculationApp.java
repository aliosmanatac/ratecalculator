package com.zopa.app;

import com.zopa.calculator.QuoteCalculator;
import com.zopa.input.MarketDataFileParser;
import com.zopa.model.Offer;
import com.zopa.model.Quote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Optional;
import java.util.SortedSet;

import static com.zopa.config.Config.LOAN_LENGTH_IN_MONTHS;
import static com.zopa.input.InputParser.parseRequestedAmount;

@ComponentScan(basePackages = "com.zopa")
@Configuration
public class QuoteCalculationApp {

    private QuoteCalculator quoteCalculator;
    private MarketDataFileParser marketDataFileParser;

    @Autowired
    public QuoteCalculationApp(final QuoteCalculator quoteCalculator, final MarketDataFileParser marketDataFileParser) {
        this.quoteCalculator = quoteCalculator;
        this.marketDataFileParser = marketDataFileParser;
    }

    /**
     * Calculates and returns the result of the quote as String. If any problem occurs related to input data,
     * it returns the related error message to the client
     * @param args app arguments to start. It expects an array with marketDataFilePath and requestedAmount
     * @return result of the calculation
     */
    public String calculate(final String[] args) {
        if (args.length != 2)
            return "Usage: quote [market_file] [requested_amount]";
        String marketDataFile = args[0];
        String amount = args[1];
        try {
            // Read market data from file
            SortedSet<Offer> offerSet = marketDataFileParser.getOffersFromFile(marketDataFile);
            int requestedAmount = parseRequestedAmount(amount);
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

package com.zopa.app;

import com.zopa.calculator.QuoteCalculator;
import com.zopa.input.MarketDataFileParser;
import com.zopa.model.Quote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static com.zopa.config.Config.LOAN_LENGTH_IN_MONTHS;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuoteCalculationAppTest {

    private static final String USAGE_MSG_PREFIX = "Usage: ";
    private static final String IO_EXCEPTION_MSG_PREFIX = "Error occurred while reading the market file";
    private static final String ILLEGAL_ARG_EXCEPTION_MSG_PREFIX = "Error occurred while processing input parameters";
    private static final String QUOTE_PREFIX = "Requested amount";
    private static final String INSUFFICIENT_AVAILABLE_PREFIX = "No available loans for the current amount";
    
    @Mock
    MarketDataFileParser marketDataFileParserMock;
    @Mock
    QuoteCalculator quoteCalculatorMock;

    @InjectMocks
    QuoteCalculationApp quoteCalculationApp;

    @Test
    public void calculate_withLessThanTwoArgs_returnsErrorMessage() {
        String response = quoteCalculationApp.calculate(new String[]{"arg1"});
        assertTrue(response.startsWith(USAGE_MSG_PREFIX));
    }

    @Test
    public void calculate_withMoreThanTwoArgs_returnsErrorMessage() {
        String response = quoteCalculationApp.calculate(new String[]{"arg1", "arg2", "arg3"});
        assertTrue(response.startsWith(USAGE_MSG_PREFIX));
    }

    @Test
    public void calculate_marketDataFileParserThrowsIOException_returnsErrorMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenThrow(new IOException("Some message"));
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "1000"});
        assertTrue(response.startsWith(IO_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void calculate_marketDataFileParserThrowsIllegalArgumentException_returnsErrorMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenThrow(new IllegalArgumentException("Some message"));
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "1000"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void calculate_smallRequestedAmount_returnsErrorMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "900"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void calculate_largeRequestedAmount_returnsErrorMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "15100"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void calculate_nonMultiplierRequestedAmount_returnsErrorMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "1099"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void calculate_insufficientAvailable_returnsInsufficientMessage() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        when(quoteCalculatorMock.calculateQuote(any(SortedSet.class), eq(15000), eq(LOAN_LENGTH_IN_MONTHS)))
                .thenReturn(Optional.empty());
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "15000"});
        assertTrue(response.startsWith(INSUFFICIENT_AVAILABLE_PREFIX));
    }

    @Test
    public void calculate_validArgs_callsQuoteCalculator() throws IOException {
        when(marketDataFileParserMock.getOffersFromFile("filePath")).thenReturn(new TreeSet<>());
        when(quoteCalculatorMock.calculateQuote(any(SortedSet.class), eq(15000), eq(LOAN_LENGTH_IN_MONTHS)))
                .thenReturn(Optional.of(Quote.builder()
                        .requestedAmount(15000)
                        .monthlyRepayment(BigDecimal.ONE)
                        .rate(BigDecimal.ONE)
                        .totalRepayment(BigDecimal.ONE)
                        .build()));
        String response = quoteCalculationApp.calculate(new String[]{"filePath", "15000"});
        assertTrue(response.startsWith(QUOTE_PREFIX));
    }
}
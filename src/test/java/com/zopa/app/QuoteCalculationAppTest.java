package com.zopa.app;

import com.zopa.calculator.QuoteCalculator;
import com.zopa.input.CsvParser;
import com.zopa.model.Quote;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
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
    CsvParser csvParser;
    @Mock
    QuoteCalculator quoteCalculator;

    @InjectMocks
    QuoteCalculationApp quoteCalculationApp;

    @Test
    public void start_withLessThanTwoArgs_returnsErrorMessage() {
        String response = quoteCalculationApp.start(new String[]{"arg1"});
        assertTrue(response.startsWith(USAGE_MSG_PREFIX));
    }

    @Test
    public void start_withMoreThanTwoArgs_returnsErrorMessage() {
        String response = quoteCalculationApp.start(new String[]{"arg1", "arg2", "arg3"});
        assertTrue(response.startsWith(USAGE_MSG_PREFIX));
    }

    @Test
    public void start_csvParserThrowsIOException_returnsErrorMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenThrow(new IOException("Some message"));
        String response = quoteCalculationApp.start(new String[]{"filePath", "1000"});
        assertTrue(response.startsWith(IO_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void start_csvParserThrowsIllegalArgumentException_returnsErrorMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenThrow(new IllegalArgumentException("Some message"));
        String response = quoteCalculationApp.start(new String[]{"filePath", "1000"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void start_smallRequestedAmount_returnsErrorMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.start(new String[]{"filePath", "900"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void start_largeRequestedAmount_returnsErrorMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.start(new String[]{"filePath", "15100"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void start_nonMultiplierRequestedAmount_returnsErrorMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        String response = quoteCalculationApp.start(new String[]{"filePath", "1099"});
        assertTrue(response.startsWith(ILLEGAL_ARG_EXCEPTION_MSG_PREFIX));
    }

    @Test
    public void start_insufficientAvailable_returnsInsufficientMessage() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        when(quoteCalculator.calculateQuote(any(TreeSet.class), eq(15000), eq(LOAN_LENGTH_IN_MONTHS)))
                .thenReturn(Optional.empty());
        String response = quoteCalculationApp.start(new String[]{"filePath", "15000"});
        assertTrue(response.startsWith(INSUFFICIENT_AVAILABLE_PREFIX));
    }

    @Test
    public void start_validArgs_callsQuoteCalculator() throws IOException {
        when(csvParser.getOffersFromFile(anyString())).thenReturn(new TreeSet<>());
        when(quoteCalculator.calculateQuote(any(TreeSet.class), eq(15000), eq(LOAN_LENGTH_IN_MONTHS)))
                .thenReturn(Optional.of(Quote.builder()
                        .requestedAmount(15000)
                        .monthlyRepayment(BigDecimal.ONE)
                        .rate(BigDecimal.ONE)
                        .totalRepayment(BigDecimal.ONE)
                        .build()));
        String response = quoteCalculationApp.start(new String[]{"filePath", "15000"});
        assertTrue(response.startsWith(QUOTE_PREFIX));
    }
}
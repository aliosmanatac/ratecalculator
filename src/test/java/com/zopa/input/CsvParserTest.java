package com.zopa.input;

import com.zopa.model.Offer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class CsvParserTest {

    @InjectMocks
    CsvParser csvParser;

    public static final String FILE_PATH = "test.csv";

    @Test
    public void getOffersFromFile_validInput_returnsTreeSetOfOffers() throws IOException {
        writeToFile(Arrays.asList(
                "Lender,Rate,Available",
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        TreeSet<Offer> offerSet = csvParser.getOffersFromFile(FILE_PATH);
        assertEquals(2, offerSet.size());
        assertEquals(BigDecimal.valueOf(0.07), offerSet.first().getRate());
        assertEquals(BigDecimal.valueOf(0.56), offerSet.last().getRate());
        assertEquals(15000, offerSet.first().getAmount());
        assertEquals(1000, offerSet.last().getAmount());
    }

    @Test
    public void getOffersFromFile_csvNoOffers_returnsEmptyTreeSet() throws IOException {
        writeToFile(Arrays.asList(
                "Lender,Rate,Available"
        ));
        TreeSet<Offer> offerSet = csvParser.getOffersFromFile(FILE_PATH);
        assertTrue(offerSet.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvWithoutHeader_throwsException() throws IOException {
        writeToFile(Arrays.asList(
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        csvParser.getOffersFromFile(FILE_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvUnmatchingHeaderName_throwsException() throws IOException {
        writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        csvParser.getOffersFromFile(FILE_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvInvalidRate_throwsException() throws IOException {
        writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.5.6,1000",
                "User2,0.07,15000"
        ));
        csvParser.getOffersFromFile(FILE_PATH);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvInvalidAmount_throwsException() throws IOException {
        writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.5.6,1000",
                "User2,0.07,invalid"
        ));
        csvParser.getOffersFromFile(FILE_PATH);
    }

    private void writeToFile(List<String> lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH));

        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
    }
}
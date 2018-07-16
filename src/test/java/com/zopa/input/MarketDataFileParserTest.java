package com.zopa.input;

import com.zopa.model.Offer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class MarketDataFileParserTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @InjectMocks
    private MarketDataFileParser marketDataFileParser;

    private static final String FILE_PATH = "test.csv";

    @Test
    public void getOffersFromFile_validInput_returnsSortedSetOfOffers() throws IOException {
        File input = writeToFile(Arrays.asList(
                "Lender,Rate,Available",
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        SortedSet<Offer> offerSet = marketDataFileParser.getOffersFromFile(input.getPath());
        assertEquals(2, offerSet.size());
        assertEquals(BigDecimal.valueOf(0.07), offerSet.first().getRate());
        assertEquals(BigDecimal.valueOf(0.56), offerSet.last().getRate());
        assertEquals(15000, offerSet.first().getAmount());
        assertEquals(1000, offerSet.last().getAmount());
    }

    @Test
    public void getOffersFromFile_validInput7Offers_returnsSortedSetOfOffers() throws IOException {
        File input = writeToFile(Arrays.asList(
                "Lender,Rate,Available",
                "Bob,0.075,640",
                "Jane,0.099,480",
                "Fred,0.071,520",
                "Mary,0.104,170",
                "John,0.081,320",
                "Dave,0.074,140",
                "Angela,0.071,960"
        ));
        SortedSet<Offer> offerSet = marketDataFileParser.getOffersFromFile(input.getPath());
        assertEquals(7, offerSet.size());
    }

    @Test
    public void getOffersFromFile_csvNoOffers_returnsEmptySortedSet() throws IOException {
        File input = writeToFile(Collections.singletonList(
                "Lender,Rate,Available"
        ));
        SortedSet<Offer> offerSet = marketDataFileParser.getOffersFromFile(input.getPath());
        assertTrue(offerSet.isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvWithoutHeader_throwsException() throws IOException {
        File input = writeToFile(Arrays.asList(
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        marketDataFileParser.getOffersFromFile(input.getPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvUnmatchingHeaderName_throwsException() throws IOException {
        File input = writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.56,1000",
                "User2,0.07,15000"
        ));
        marketDataFileParser.getOffersFromFile(input.getPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvInvalidRate_throwsException() throws IOException {
        File input = writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.5.6,1000",
                "User2,0.07,15000"
        ));
        marketDataFileParser.getOffersFromFile(input.getPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getOffersFromFile_csvInvalidAmount_throwsException() throws IOException {
        File input = writeToFile(Arrays.asList(
                "Lender,Invalid,Available",
                "User1,0.5.6,1000",
                "User2,0.07,invalid"
        ));
        marketDataFileParser.getOffersFromFile(input.getPath());
    }

    private File writeToFile(List<String> lines) throws IOException {
        File createdFile = folder.newFile(FILE_PATH);
        BufferedWriter writer = new BufferedWriter(new FileWriter(createdFile));

        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
        writer.close();
        return createdFile;
    }
}
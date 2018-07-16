package com.zopa.input;

import com.zopa.model.Offer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class MarketDataFileParser {
    public SortedSet<Offer> getOffersFromFile(final String filePath) throws IOException {
        File csvData = new File(filePath);
        CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"),
                CSVFormat.DEFAULT.withFirstRecordAsHeader()
                        .withAllowMissingColumnNames(false)
                        .withIgnoreHeaderCase());

        return parser.getRecords().stream()
                .map(InputParser::parseOffer)
                .collect(Collectors.toCollection(TreeSet::new));
    }
}

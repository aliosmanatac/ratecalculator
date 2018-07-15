package com.zopa.input;

import com.zopa.model.Offer;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.springframework.util.Assert.notNull;

@Component
public class CsvParser {
    public TreeSet<Offer> getOffersFromFile(final String filePath) throws IOException {
        File csvData = new File(filePath);
        CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"),
                CSVFormat.DEFAULT.withFirstRecordAsHeader()
                        .withAllowMissingColumnNames(false)
                        .withIgnoreHeaderCase());

        return parser.getRecords().stream()
                .map(InputParser::parseOffer)
                .collect(Collectors.toCollection(() -> new TreeSet<>()));
    }
}

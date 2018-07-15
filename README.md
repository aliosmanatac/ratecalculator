# Transactions API

## Install & Run
```
mvn clean install
java -jar target/quote.jar input.csv 1000
```

## Implementation Details
This application aims to calculate a quote based on the given market data (lenders with rates and available amount)
Market data is a CSV file including 3 data in each row: Lender, Rate and Available. 

The application returns a message indicating no sufficient amount for a requested amount if requested amount is larger 
than available. Otherwise it calculates a weighted average with the least rated offers.

The locale, currency, minimum, maximum amounts, number of months of repayment are configured in a Config file which 
gives the opportunity to customize this values easily

The application uses BigDecimal type in internal implementations, which gives a high accuracy in calculations

The formula is taken from https://www.thebalance.com/loan-payment-calculations-315564 in the section 
"Formula for Amortizing Loan Payment":
```$xslt
Loan Payment (L) = Amount(A) / Discount Factor(D)
Discount Factor (D) = {[(1 + i) ^n] - 1} / [i(1 + i)^n]
where:
* Number of Periodic Payments (n) = Payments per year times number of years
* Periodic Interest Rate (i) = Annual rate divided by number of payment periods
```

Spring context is used to inject dependencies and make testing easier. 




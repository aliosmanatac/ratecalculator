package com.zopa.calculator;

import com.zopa.model.Offer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Optional;
import java.util.TreeSet;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

@Component
public class QuoteCalculatorHelperImpl implements QuoteCalculatorHelper {
    /**
     * This method calculates the weighted rate from the given set of offers
     * Works as follows:
     * - Add offer amount to the total amount until requested amount is reached
     * - In each iteration, calculate the weighted average of rate
     * - return optional of weighted rate if amount is reached, otherwise return empty
     * @param offerSet Set of available offers
     * @param amount Total amount to loan
     * @return calculated rate as optional if offers satisfy amount.
     *      * otherwise return empty
     */
    public Optional<BigDecimal> calculateRate(final TreeSet<Offer> offerSet, final int amount) {
        notNull(offerSet, "offerSet cannot be null");
        isTrue(amount > 0, "amount should be positive");

        int currAmount = 0;
        BigDecimal currRate = new BigDecimal(0);
        for (Offer offer: offerSet) {
            if (currAmount + offer.getAmount() < amount) {
                currRate = calculateWeightedAverage(currRate, new BigDecimal(currAmount),
                        offer.getRate(), new BigDecimal(offer.getAmount()));
                currAmount = currAmount + offer.getAmount();
            } else {
                currRate = calculateWeightedAverage(currRate, new BigDecimal(currAmount),
                        offer.getRate(), new BigDecimal(amount - currAmount));
                currAmount = amount;
                break;
            }
        }

        return currAmount == amount ? Optional.of(currRate) : Optional.empty();
    }

    /**
     * Discount Factor = {[(1 + mRate) ^numberOfMonths] - 1} / [mRate(1 + mRate)^numberOfMonths]
     * where
     *  (mRate) = Annual rate divided by number of payment periods
     * @param rate Yearly rate for the loan
     * @param numberOfMonths Total number of months of repayment
     * @return Discount factor
     */
    public BigDecimal calculateDiscountFactor(final BigDecimal rate, final int numberOfMonths) {
        notNull(rate, "rate cannot be null");
        isTrue(numberOfMonths > 0, "numberOfMonths should be positive");

        final int yearlyPayments = 12;
        final BigDecimal mRate = rate.divide(new BigDecimal(yearlyPayments), MathContext.DECIMAL128);

        final BigDecimal numerator = mRate.add(BigDecimal.ONE).pow(numberOfMonths).subtract(BigDecimal.ONE);
        final BigDecimal denominator = mRate.multiply(mRate.add(BigDecimal.ONE).pow(numberOfMonths));

        return numerator.divide(denominator, MathContext.DECIMAL128);
    }

    /**
     * Calculation of monthly payments done by division of amount by discountFactor
     * @param discountFactor Discount factor
     * @param amount Total amount of loan
     * @return Amount to pay each month
     */
    public BigDecimal calculateMonthlyPayments(final BigDecimal discountFactor, final int amount) {
        notNull(discountFactor, "discountFactor cannot be null");
        isTrue(amount > 0, "amount should be positive");

        return new BigDecimal(amount).divide(discountFactor, MathContext.DECIMAL128);
    }

    /**
     * Total amount calculated by multiplication of monthly payments and numberOf months
     * @param monthlyPayments
     * @param numberOfMonths
     * @return
     */
    public BigDecimal calculateTotalPayments(final BigDecimal monthlyPayments, final int numberOfMonths) {
        notNull(monthlyPayments, "monthlyPayments cannot be null");
        isTrue(numberOfMonths > 0, "numberOfMonths should be positive");

        return monthlyPayments.multiply(new BigDecimal(numberOfMonths));
    }

    private BigDecimal calculateWeightedAverage(final BigDecimal value1, final BigDecimal weight1,
                                                final BigDecimal value2, final BigDecimal weight2) {
        return value1.multiply(weight1)
                .add(value2.multiply(weight2))
                .divide(weight1.add(weight2), MathContext.DECIMAL128);
    }
}

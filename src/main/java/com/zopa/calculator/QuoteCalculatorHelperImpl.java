package com.zopa.calculator;

import com.zopa.model.Offer;
import com.zopa.model.WeightedValue;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;

import static org.springframework.util.Assert.isTrue;

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
    public Optional<BigDecimal> calculateRate(@NonNull final SortedSet<Offer> offerSet, final int amount) {
        isTrue(amount > 0, "amount should be positive");

        List<WeightedValue> offersToLoan = new LinkedList<>();
        int currTotal = 0;
        for (Offer offer: offerSet) {
            if (currTotal + offer.getAmount() < amount) {
                offersToLoan.add(WeightedValue.builder()
                        .weight(BigDecimal.valueOf(offer.getAmount()))
                        .value(offer.getRate())
                        .build());
                currTotal = currTotal + offer.getAmount();
            } else {
                offersToLoan.add(WeightedValue.builder()
                        .weight(BigDecimal.valueOf(amount - currTotal))
                        .value(offer.getRate())
                        .build());
                currTotal = amount;
                break;
            }
        }

        return currTotal == amount ? Optional.of(offersToLoan.stream()
                .reduce(QuoteCalculatorHelperImpl::calculateWeightedAverage)
                .get()
                .getValue()) : Optional.empty();
    }

    /**
     * Discount Factor = {[(1 + mRate) ^numberOfMonths] - 1} / [mRate(1 + mRate)^numberOfMonths]
     * where
     *  (mRate) = Annual rate divided by number of payment periods
     * @param rate Yearly rate for the loan
     * @param numberOfMonths Total number of months of repayment
     * @return Discount factor
     */
    public BigDecimal calculateDiscountFactor(@NonNull final BigDecimal rate, final int numberOfMonths) {
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
    public BigDecimal calculateMonthlyPayments(@NonNull final BigDecimal discountFactor, final int amount) {
        isTrue(amount > 0, "amount should be positive");

        return new BigDecimal(amount).divide(discountFactor, MathContext.DECIMAL128);
    }

    /**
     * Total amount calculated by multiplication of monthly payments and numberOf months
     * @param monthlyPayments
     * @param numberOfMonths
     * @return Total repayment amount
     */
    public BigDecimal calculateTotalPayments(@NonNull final BigDecimal monthlyPayments, final int numberOfMonths) {
        isTrue(numberOfMonths > 0, "numberOfMonths should be positive");

        return monthlyPayments.multiply(new BigDecimal(numberOfMonths));
    }

    private static WeightedValue calculateWeightedAverage(final WeightedValue wv1, final WeightedValue wv2) {
        return WeightedValue.builder().value(
                wv1.getValue().multiply(wv1.getWeight())
                .add(wv2.getValue().multiply(wv2.getWeight()))
                .divide(wv1.getWeight().add(wv2.getWeight()), MathContext.DECIMAL128))
                .weight(wv1.getWeight().add(wv2.getWeight()))
                .build();
    }
}

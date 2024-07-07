package org.example.xchange.finance;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.exception.MathIllegalArgumentException;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

public class GaussianNaiveBayes {

	private List<RealDistribution> distributions;

	public GaussianNaiveBayes(List<List<BigDecimal>> data) {
		distributions = new ArrayList<>();

		for (int i = 0; i < data.get(0).size(); i++) {
			List<BigDecimal> column = new ArrayList<>();
			for (List<BigDecimal> row : data) {
				column.add(row.get(i));
			}

			BigDecimal mean = calculateMean(column);
			BigDecimal stdDev = calculateStdDev(column, mean);

			double meanDouble = mean.doubleValue();
			double stdDevDouble = stdDev.doubleValue();

			distributions.add(new NormalDistribution(meanDouble, stdDevDouble));
		}
	}

	public double predict(List<BigDecimal> features) {
		double[] featuresData = new double[features.size()];
		for (int i = 0; i < features.size(); i++) {
			featuresData[i] = features.get(i).doubleValue();
		}

		double result = 1.0;

		for (int i = 0; i < features.size(); i++) {
			try {
				result *= distributions.get(i).density(featuresData[i]);
			} catch (MathIllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	private BigDecimal calculateMean(List<BigDecimal> data) {
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal value : data) {
			sum = sum.add(value);
		}
		return sum.divide(BigDecimal.valueOf(data.size()), MathContext.DECIMAL128);
	}

	private BigDecimal calculateStdDev(List<BigDecimal> data, BigDecimal mean) {
		BigDecimal sum = BigDecimal.ZERO;
		for (BigDecimal value : data) {
			BigDecimal diff = value.subtract(mean);
			sum = sum.add(diff.pow(2));
		}
		BigDecimal variance = sum.divide(BigDecimal.valueOf(data.size()), MathContext.DECIMAL128);
		return new BigDecimal(Math.sqrt(variance.doubleValue()), MathContext.DECIMAL128);
	}

	public static void main(String[] args) {
		List<List<BigDecimal>> trainingData = new ArrayList<>();
		trainingData.add(List.of(BigDecimal.valueOf(1.0), BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0)));
		trainingData.add(List.of(BigDecimal.valueOf(2.0), BigDecimal.valueOf(3.0), BigDecimal.valueOf(4.0)));
		trainingData.add(List.of(BigDecimal.valueOf(3.0), BigDecimal.valueOf(4.0), BigDecimal.valueOf(5.0)));
		GaussianNaiveBayes model = new GaussianNaiveBayes(trainingData);

		double[] unknownFeatures = {4.0, 5.0, 6.0};

		//double prediction = model.predict(unknownFeatures);
		//System.out.println("Predicted probability: " + prediction);
	}
}

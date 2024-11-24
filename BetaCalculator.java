import java.io.*;
import java.util.*;

public class BetaCalculator {

    public static void main(String[] args) throws IOException {
        String stockFile = "stock.csv"; // Path to stock CSV
        String benchmarkFile = "benchmark.csv"; // Path to benchmark CSV

        List<Double> stockPrices = readPricesFromCsv(stockFile);
        List<Double> marketPrices = readPricesFromCsv(benchmarkFile);

        double beta = calculateBeta(stockPrices, marketPrices);
        System.out.println("Beta: " + beta);
    }

    // Reads closing prices from a CSV file
    private static List<Double> readPricesFromCsv(String filePath) throws IOException {
        List<Double> prices = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean skipHeader = true; // Skip the header row
            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                String[] fields = line.split(",");
                double closingPrice = Double.parseDouble(fields[1]); // Assuming closing price is in column 2
                prices.add(closingPrice);
            }
        }
        return prices;
    }

    // Calculates beta given stock and market prices
    private static double calculateBeta(List<Double> stockPrices, List<Double> marketPrices) {
        if (stockPrices.size() != marketPrices.size() || stockPrices.size() < 2) {
            throw new IllegalArgumentException("Stock and market prices must have the same size and at least 2 data points.");
        }

        // Calculate returns for stock and market
        List<Double> stockReturns = calculateReturns(stockPrices);
        List<Double> marketReturns = calculateReturns(marketPrices);

        // Calculate covariance and variance
        double covariance = calculateCovariance(stockReturns, marketReturns);
        double variance = calculateVariance(marketReturns);

        // Beta = Covariance(stock, market) / Variance(market)
        return covariance / variance;
    }

    // Calculates daily returns
    private static List<Double> calculateReturns(List<Double> prices) {
        List<Double> returns = new ArrayList<>();
        for (int i = 1; i < prices.size(); i++) {
            double dailyReturn = (prices.get(i) - prices.get(i - 1)) / prices.get(i - 1);
            returns.add(dailyReturn);
        }
        return returns;
    }

    // Calculates covariance between two lists of returns
    private static double calculateCovariance(List<Double> x, List<Double> y) {
        double meanX = calculateMean(x);
        double meanY = calculateMean(y);

        double covariance = 0.0;
        for (int i = 0; i < x.size(); i++) {
            covariance += (x.get(i) - meanX) * (y.get(i) - meanY);
        }
        return covariance / (x.size() - 1);
    }

    // Calculates variance of a list of returns
    private static double calculateVariance(List<Double> values) {
        double mean = calculateMean(values);

        double variance = 0.0;
        for (double value : values) {
            variance += Math.pow(value - mean, 2);
        }
        return variance / (values.size() - 1);
    }

    // Calculates the mean of a list of values
    private static double calculateMean(List<Double> values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.size();
    }
}

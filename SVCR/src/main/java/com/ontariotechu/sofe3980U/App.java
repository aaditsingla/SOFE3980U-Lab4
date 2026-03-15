package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {

    static class Metrics {
        double mse;
        double mae;
        double mare;

        Metrics(double mse, double mae, double mare) {
            this.mse = mse;
            this.mae = mae;
            this.mare = mare;
        }
    }

    public static Metrics evaluateModel(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            double sumSquaredError = 0.0;
            double sumAbsoluteError = 0.0;
            double sumAbsoluteRelativeError = 0.0;
            double epsilon = 1e-10;

            int n = allData.size();

            for (String[] row : allData) {
                double yTrue = Double.parseDouble(row[0]);
                double yPredicted = Double.parseDouble(row[1]);

                double error = yTrue - yPredicted;

                sumSquaredError += error * error;
                sumAbsoluteError += Math.abs(error);
                sumAbsoluteRelativeError += Math.abs(error) / (Math.abs(yTrue) + epsilon);
            }

            double mse = sumSquaredError / n;
            double mae = sumAbsoluteError / n;
            double mare = sumAbsoluteRelativeError / n;

            return new Metrics(mse, mae, mare);

        } catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
            return null;
        }
    }

    public static void printMetrics(String fileName, Metrics m) {
        System.out.println("for " + fileName);
        System.out.println("\tMSE =" + m.mse);
        System.out.println("\tMAE =" + m.mae);
        System.out.println("\tMARE =" + m.mare);
    }

    public static void main(String[] args) {
        String[] files = {"model_1.csv", "model_2.csv", "model_3.csv"};
        Metrics[] results = new Metrics[files.length];

        for (int i = 0; i < files.length; i++) {
            results[i] = evaluateModel(files[i]);
            if (results[i] != null) {
                printMetrics(files[i], results[i]);
            }
        }

        int bestMSE = 0;
        int bestMAE = 0;
        int bestMARE = 0;

        for (int i = 1; i < results.length; i++) {
            if (results[i].mse < results[bestMSE].mse) {
                bestMSE = i;
            }
            if (results[i].mae < results[bestMAE].mae) {
                bestMAE = i;
            }
            if (results[i].mare < results[bestMARE].mare) {
                bestMARE = i;
            }
        }

        System.out.println("According to MSE, The best model is " + files[bestMSE]);
        System.out.println("According to MAE, The best model is " + files[bestMAE]);
        System.out.println("According to MARE, The best model is " + files[bestMARE]);
    }
}
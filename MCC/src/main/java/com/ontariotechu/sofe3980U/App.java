package com.ontariotechu.sofe3980U;

import java.io.FileReader;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

public class App {

    static class Metrics {
        double ce;
        int[][] confusionMatrix;

        Metrics(double ce, int[][] confusionMatrix) {
            this.ce = ce;
            this.confusionMatrix = confusionMatrix;
        }
    }

    public static Metrics evaluateModel(String filePath) {
        try {
            FileReader filereader = new FileReader(filePath);
            CSVReader csvReader = new CSVReaderBuilder(filereader).withSkipLines(1).build();
            List<String[]> allData = csvReader.readAll();
            csvReader.close();

            int n = allData.size();
            double epsilon = 1e-15;
            double ceSum = 0.0;

            int numClasses = 5;
            int[][] confusionMatrix = new int[numClasses][numClasses];

            for (String[] row : allData) {
                int yTrue = Integer.parseInt(row[0]);

                double[] probs = new double[numClasses];
                int predictedClass = 1;
                double maxProb = -1.0;

                for (int i = 0; i < numClasses; i++) {
                    probs[i] = Double.parseDouble(row[i + 1]);

                    if (probs[i] < epsilon) {
                        probs[i] = epsilon;
                    }

                    if (probs[i] > maxProb) {
                        maxProb = probs[i];
                        predictedClass = i + 1;
                    }
                }

                ceSum += Math.log(probs[yTrue - 1]);

                confusionMatrix[predictedClass - 1][yTrue - 1]++;
            }

            double ce = -ceSum / n;

            return new Metrics(ce, confusionMatrix);

        } catch (Exception e) {
            System.out.println("Error reading file: " + filePath);
            return null;
        }
    }

    public static void printMetrics(Metrics m) {
        System.out.println("CE =" + m.ce);
        System.out.println("Confusion matrix");
        System.out.println("\t\ty=1\ty=2\ty=3\ty=4\ty=5");

        for (int i = 0; i < 5; i++) {
            System.out.print("\ty^=" + (i + 1));
            for (int j = 0; j < 5; j++) {
                System.out.print("\t" + m.confusionMatrix[i][j]);
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Metrics result = evaluateModel("model.csv");
        if (result != null) {
            printMetrics(result);
        }
    }
}
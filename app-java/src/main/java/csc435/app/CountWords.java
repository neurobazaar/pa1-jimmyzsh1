package csc435.app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class CountWords {

    private static long bytesRead = 0; // static variable
    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please provide input and output directories.");
            return;
        }

        String inputDirBase = args[0];
        String outputDirBase = args[1];

        for (int i = 1; i <= 5; i++) {
            String inputDir = inputDirBase + "CleanedDataset" + i;
            String outputDir = outputDirBase + "CountedDataset" + i;
            bytesRead = 0; // reset to 0
            long startTime = System.currentTimeMillis();

            try {
                Files.walk(Paths.get(inputDir))
                        .filter(Files::isRegularFile)
                        .forEach(file -> processFile(file, outputDir)); // remove redundant things
            } catch (IOException e) {
                System.out.println("Error processing files: " + e.getMessage());
                return;
            }

            long timeTaken = System.currentTimeMillis() - startTime;
            displayMetrics(bytesRead, timeTaken, i);
        }
    }

    private static void processFile(Path filePath, String outputDir) {
        File file = filePath.toFile();
        Map<String, Integer> wordCounts = countWordsInFile(file);
        bytesRead += file.length();

        try {
            Path outputFile = Paths.get(outputDir, filePath.getFileName().toString());
            Files.createDirectories(outputFile.getParent());
            writeWordCountsToFile(wordCounts, outputFile.toFile());
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void displayMetrics(long bytesRead, long timeTaken, int datasetNumber) {
        double datasetSizeMiB = bytesRead / (1024.0 * 1024.0);
        double timeTakenSeconds = timeTaken / 1000.0;
        double throughput = datasetSizeMiB / timeTakenSeconds;
        System.out.printf("Dataset %d = %.2f MB, takes %.3f seconds, Bandwidth %.3f MB/s%n",
                datasetNumber, datasetSizeMiB, timeTakenSeconds, throughput);
    }

    //  methods
    private static Map<String, Integer> countWordsInFile(File file) {
        Map<String, Integer> wordCounts = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(file.toPath())) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.toLowerCase().split("\\W+");
                for (String word : words) {
                    if (!word.isEmpty()) {
                        wordCounts.put(word, wordCounts.getOrDefault(word, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordCounts;
    }

    private static void writeWordCountsToFile(Map<String, Integer> wordCounts, File outputFile) {
        // 使用Files.newBufferedWriter简化文件写入
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile.toPath())) {
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
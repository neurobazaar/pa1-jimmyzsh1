package csc435.app;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
public class SortWords {

    private static long processFile(Path file, Path inputDirPath, Path outputDirPath) throws IOException {
        Map<String, Integer> wordCounts = readWordCountsFromFile(file);
        List<Map.Entry<String, Integer>> sortedWordCounts = sortWordCounts(wordCounts);

        Path relativePath = inputDirPath.relativize(file);
        Path outputFile = outputDirPath.resolve(relativePath);
        Files.createDirectories(outputFile.getParent());

        writeSortedWordCountsToFile(sortedWordCounts, outputFile);

        return wordCounts.size(); // 返回单个文件中的单词计数
    }


    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please provide input and output directories.");
            return;
        }


        String inputDirBase = args[0];
        String outputDirBase = args[1];

        for (int i = 1; i <= 5; i++) {
            String inputDir = inputDirBase + "CountedDataset" + i;
            String outputDir = outputDirBase + "SortedDataset" + i;
            Path inputDirPath = Paths.get(inputDir);
            Path outputDirPath = Paths.get(outputDir);
            long totalWords = 0;
            long startTime = System.currentTimeMillis();

            try (Stream<Path> paths = Files.walk(inputDirPath)) {
                long wordsInDataset = paths.filter(Files::isRegularFile)
                        .parallel() // 使用并行流加速处理
                        .mapToLong(file -> {
                            try {
                                return processFile(file, inputDirPath, outputDirPath);
                            } catch (IOException e) {
                                System.out.println("Error processing file " + file + ": " + e.getMessage());
                                return 0L;
                            }
                        }).sum();

                totalWords += wordsInDataset; // 累加每个数据集的单词计数
            } catch (IOException e) {
                System.out.println("Error walking the file tree: " + e.getMessage());
            }

            long endTime = System.currentTimeMillis();
            double timeTakenSeconds = (endTime - startTime) / 1000.0;
            double throughput = totalWords / timeTakenSeconds;

            System.out.printf("Dataset %d: words_count = %d, takes = %.3f seconds, Bandwidth = %.3f words/s%n",
                    i, totalWords, timeTakenSeconds, throughput);
        }
    }

    // methods
    private static Map<String, Integer> readWordCountsFromFile(Path filePath) throws IOException {
        Map<String, Integer> wordCounts = new HashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2) {
                    wordCounts.put(parts[0], Integer.parseInt(parts[1]));
                }
            }
        }
        return wordCounts;
    }
    private static List<Map.Entry<String, Integer>> sortWordCounts(Map<String, Integer> wordCounts) {
        return wordCounts.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .collect(Collectors.toList());
    }

    private static void writeSortedWordCountsToFile(List<Map.Entry<String, Integer>> sortedWordCounts, Path outputFile) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(outputFile)) {
            for (Map.Entry<String, Integer> entry : sortedWordCounts) {
                writer.write(entry.getKey() + " " + entry.getValue());
                writer.newLine();
            }
        }
    }

}

package csc435.app;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.File;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.List;

public class CleanDataset {

    public static void main(String[] args) {

        if (args.length < 2) {
            System.out.println("Please provide input directory and output directory as arguments.");
            return;
        }

        String inputDirBase = args[0];
        String outputDirBase = args[1];

        for (int i = 1; i <= 5; i++) {
            String inputDir = inputDirBase + "/Dataset" + i;
            String outputDir = outputDirBase + "/CleanedDataset" + i;

            long startTime = System.currentTimeMillis();

            try {
                List<File> files = Files.walk(Paths.get(inputDir))
                        .filter(Files::isRegularFile)
                        .map(Path::toFile)
                        .collect(Collectors.toList());

                long bytesReadForDataset = files.parallelStream()
                        .peek(file -> cleanFile(file, inputDir, outputDir))
                        .mapToLong(File::length)
                        .sum();

                long endTime = System.currentTimeMillis();
                displayMetrics(bytesReadForDataset, startTime, endTime, i);

            } catch (IOException e) {
                System.out.println("Error processing files: " + e.getMessage());
            }
        }
    }

    private static void displayMetrics(long bytesRead, long startTime, long endTime, int datasetNumber) {
        double datasetSizeMiB = bytesRead / 1024.0 / 1024.0;
        double timeTakenSeconds = (endTime - startTime) / 1000.0;
        double throughput = datasetSizeMiB / timeTakenSeconds;
        System.out.println(String.format("Dataset %d: Size %.2f MB, Time Taken %.2f seconds, Throughput %.2f MB/s",
                datasetNumber, datasetSizeMiB, timeTakenSeconds, throughput));
    }

    private static void cleanFile(File file, String inputDir, String outputDir) {
        Path outputPath = Paths.get(outputDir, file.getPath().substring(inputDir.length()));
        try {
            String content = Files.readString(file.toPath());
            String cleanedContent = cleanContent(content);
            Files.createDirectories(outputPath.getParent());
            Files.writeString(outputPath, cleanedContent);
        } catch (IOException e) {
            System.out.println("Error cleaning file: " + file.getName() + " - " + e.getMessage());
        }
    }

    private static String cleanContent(String content) {
        // Remove '\r' characters and any non-word separators
        String cleaned = content.replaceAll("\\r", "").replaceAll("[^\\w\\s]+", "");

        // Replace multiple consecutive whitespace characters with a single space
        return cleaned.replaceAll("\\s+", " ");
    }
}

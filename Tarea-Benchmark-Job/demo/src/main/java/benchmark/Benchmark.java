package benchmark;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import structures.DoublyLinkedList;
import structures.DynamicArray;
import structures.IntStructure;
import structures.SinglyLinkedList;

public class Benchmark {
    private static final int[] SIZES = {10, 100, 1000, 10000};
    private static final int RUNS = 5;

    private static final String[] TIMED_OPERATIONS = {
            "insertFirst",
            "insertLast",
            "insertMiddle",
            "delete",
            "search",
            "get",
            "replace",
            "size",
            "clear"
    };

    private static volatile long blackHole;

    public static void runAll() {
        new Benchmark().run();
    }

    public void run() {
        Path resultsDir = Path.of("results");
        try {
            Files.createDirectories(resultsDir);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta results", e);
        }

        runExperimentsForStructure("array", resultsDir);
        runExperimentsForStructure("singly", resultsDir);
        runExperimentsForStructure("doubly", resultsDir);
    }

    private void runExperimentsForStructure(String structureName, Path resultsDir) {
        List<ExperimentResult> experiments = new ArrayList<>();

        for (int size : SIZES) {
            experiments.add(runExperimentForStructureSize(structureName, size));
        }

        writeJsonResults(structureName, experiments.toArray(new ExperimentResult[0]), resultsDir);
    }

    private ExperimentResult runExperimentForStructureSize(String structureName, int n) {
        System.out.println("\n============================================================");
        System.out.println("Estructura: " + structureName + " | n=" + n + " | corridas=" + RUNS);
        System.out.println("============================================================");
        System.out.printf("%-14s %-18s %-18s %-8s%n", "Operacion", "Promedio", "Desv. Estandar", "Unidad");

        OperationResult[] operationResults = new OperationResult[TIMED_OPERATIONS.length + 1];
        int opIndex = 0;

        for (String operation : TIMED_OPERATIONS) {
            double[] raw = new double[RUNS];
            for (int run = 0; run < RUNS; run++) {
                IntStructure structure = createStructure(structureName);
                raw[run] = measureTimedOperation(structure, operation, n);
                structure.clear();
            }

            OperationResult result = new OperationResult(operation, raw, "ns");
            operationResults[opIndex++] = result;
            printSummaryRow(result);
        }

        double[] memoryRaw = new double[RUNS];
        for (int run = 0; run < RUNS; run++) {
            IntStructure structure = createStructure(structureName);
            memoryRaw[run] = measureMemoryOperation(structure, n);
            structure.clear();
        }

        OperationResult memoryResult = new OperationResult("memoryBytes", memoryRaw, "bytes");
        operationResults[opIndex] = memoryResult;
        printSummaryRow(memoryResult);

        ExperimentResult experimentResult = new ExperimentResult(
                structureName,
                n,
                RUNS,
                Instant.now().toString(),
                operationResults
        );

        return experimentResult;
    }

    private void printSummaryRow(OperationResult result) {
        String avgText = "bytes".equals(result.unit)
                ? String.format(Locale.US, "%.0f", result.avg)
                : String.format(Locale.US, "%.2f", result.avg);
        String stdText = "bytes".equals(result.unit)
                ? String.format(Locale.US, "%.0f", result.stddev)
                : String.format(Locale.US, "%.2f", result.stddev);

        System.out.printf(Locale.US, "%-14s %-18s %-18s %-8s%n",
                result.name,
                avgText,
                stdText,
                result.unit);
    }

    private IntStructure createStructure(String structureName) {
        return switch (structureName) {
            case "array" -> new DynamicArray();
            case "singly" -> new SinglyLinkedList();
            case "doubly" -> new DoublyLinkedList();
            default -> throw new IllegalArgumentException("Estructura desconocida: " + structureName);
        };
    }

    private double measureTimedOperation(IntStructure structure, String operation, int n) {
        long checksum = 0;
        long start;
        long end;

        switch (operation) {
            case "insertFirst":
                structure.clear();
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    structure.insertFirst(i);
                }
                end = System.nanoTime();
                return end - start;

            case "insertLast":
                structure.clear();
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    structure.insertLast(i);
                }
                end = System.nanoTime();
                return end - start;

            case "insertMiddle":
                structure.clear();
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    structure.insertMiddle(i);
                }
                end = System.nanoTime();
                return end - start;

            case "delete":
                structure.clear();
                populateSequential(structure, n);
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    if (!structure.delete(i)) {
                        checksum++;
                    }
                }
                end = System.nanoTime();
                blackHole ^= checksum;
                return end - start;

            case "search":
                structure.clear();
                populateSequential(structure, n);
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    checksum += structure.search(i);
                }
                end = System.nanoTime();
                blackHole ^= checksum;
                return end - start;

            case "get":
                structure.clear();
                populateSequential(structure, n);
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    checksum += structure.get(i);
                }
                end = System.nanoTime();
                blackHole ^= checksum;
                return end - start;

            case "replace":
                structure.clear();
                populateSequential(structure, n);
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    structure.replace(i, -i);
                }
                end = System.nanoTime();
                return end - start;

            case "size":
                structure.clear();
                populateSequential(structure, n);
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    checksum += structure.size();
                }
                end = System.nanoTime();
                blackHole ^= checksum;
                return end - start;

            case "clear":
                structure.clear();
                start = System.nanoTime();
                for (int i = 0; i < n; i++) {
                    structure.insertLast(i);
                    structure.clear();
                }
                end = System.nanoTime();
                return end - start;

            default:
                throw new IllegalArgumentException("Operacion desconocida: " + operation);
        }
    }

    private double measureMemoryOperation(IntStructure structure, int n) {
        structure.clear();
        populateSequential(structure, n);
        return structure.memoryBytes();
    }

    private void populateSequential(IntStructure structure, int n) {
        for (int i = 0; i < n; i++) {
            structure.insertLast(i);
        }
    }

    private void writeJsonResults(String structureName, ExperimentResult[] results, Path resultsDir) {
        String fileName = structureName + ".json";
        Path outputPath = resultsDir.resolve(fileName);
        String json = buildJson(structureName, results);

        try {
            Files.writeString(outputPath, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo escribir el archivo JSON: " + outputPath, e);
        }
    }

    private String buildJson(String structureName, ExperimentResult[] results) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("  \"structure\": \"").append(structureName).append("\",\n");
        sb.append("  \"results\": [\n");

        for (int r = 0; r < results.length; r++) {
            ExperimentResult result = results[r];
            sb.append("    {\n");
            sb.append("      \"size\": ").append(result.size).append(",\n");
            sb.append("      \"runs\": ").append(result.runs).append(",\n");
            sb.append("      \"timestamp\": \"").append(result.timestamp).append("\",\n");
            sb.append("      \"operations\": {\n");

            for (int i = 0; i < result.operations.length; i++) {
                OperationResult operation = result.operations[i];
                sb.append("        \"").append(operation.name).append("\": {\n");
                sb.append("          \"raw\": ").append(formatRawArray(operation.raw, operation.unit)).append(",\n");
                if ("bytes".equals(operation.unit)) {
                    sb.append("          \"avg\": ").append(String.format(Locale.US, "%.0f", operation.avg)).append(",\n");
                    sb.append("          \"stddev\": ").append(String.format(Locale.US, "%.0f", operation.stddev)).append(",\n");
                } else {
                    sb.append("          \"avg\": ").append(String.format(Locale.US, "%.2f", operation.avg)).append(",\n");
                    sb.append("          \"stddev\": ").append(String.format(Locale.US, "%.2f", operation.stddev)).append(",\n");
                }
                sb.append("          \"unit\": \"").append(operation.unit).append("\"\n");
                sb.append("        }");
                if (i < result.operations.length - 1) {
                    sb.append(",");
                }
                sb.append("\n");
            }

            sb.append("      }\n");
            sb.append("    }");
            if (r < results.length - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("  ]\n");
        sb.append("}\n");
        return sb.toString();
    }

    private String formatRawArray(double[] raw, String unit) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < raw.length; i++) {
            if ("bytes".equals(unit)) {
                sb.append(String.format(Locale.US, "%.0f", raw[i]));
            } else {
                sb.append(String.format(Locale.US, "%.0f", raw[i]));
            }
            if (i < raw.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    private double average(double[] values) {
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        return sum / values.length;
    }

    private double stddev(double[] values, double avg) {
        if (values.length < 2) {
            return 0.0;
        }
        double varianceAcc = 0.0;
        for (double value : values) {
            double diff = value - avg;
            varianceAcc += diff * diff;
        }
        return Math.sqrt(varianceAcc / values.length);
    }

    private final class OperationResult {
        private final String name;
        private final double[] raw;
        private final double avg;
        private final double stddev;
        private final String unit;

        private OperationResult(String name, double[] raw, String unit) {
            this.name = name;
            this.raw = raw;
            this.unit = unit;
            this.avg = average(raw);
            this.stddev = stddev(raw, this.avg);
        }
    }

    private static final class ExperimentResult {
        private final String structure;
        private final int size;
        private final int runs;
        private final String timestamp;
        private final OperationResult[] operations;

        private ExperimentResult(String structure, int size, int runs, String timestamp, OperationResult[] operations) {
            this.structure = structure;
            this.size = size;
            this.runs = runs;
            this.timestamp = timestamp;
            this.operations = operations;
        }
    }
}

package benchmark;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public final class ChartGenerator {
    private static final int[] SIZES = {10, 100, 1000, 10000};

    // Se agregan las nuevas operaciones de escenarios al grafico
    private static final String[] TIME_OPERATIONS = {
            "insertFirst",
            "insertLast",
            "insertMiddle",
            "delete",
            "search",
            "get",
            "replace",
            "insertar_accion",
            "deshacer_accion",
            "insertar_cliente",
            "atender_cliente"
    };

    // Se agregan las estructuras
    private static final String[] STRUCTURES = {"array", "singly", "doubly", "stack_array", "stack_list", "queue_array", "queue_list"};

    private static final Map<String, String> DISPLAY_NAMES = Map.of(
            "array", "Array",
            "singly", "Singly Linked List",
            "doubly", "Doubly Linked List",
            "stack_array", "Stack (Array)",
            "stack_list", "Stack (List)",
            "queue_array", "Queue (Array)",
            "queue_list", "Queue (List)"
    );

    // Se asignan colores adicionales
    private static final Map<String, Color> SERIES_COLORS = Map.of(
            "array", Color.decode("#2196F3"),
            "singly", Color.decode("#F44336"),
            "doubly", Color.decode("#4CAF50"),
            "stack_array", Color.decode("#9C27B0"),
            "stack_list", Color.decode("#FF9800"),
            "queue_array", Color.decode("#00BCD4"),
            "queue_list", Color.decode("#795548")
    );

    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final int CHART_WIDTH = 800;
    private static final int CHART_HEIGHT = 500;

    private ChartGenerator() {
    }

    public static void generate() {
        Path resultsDir = Path.of("results");
        
        // Agregamos las validaciones de los nuevos JSON
        Path arrayPath = resultsDir.resolve("array.json");
        Path singlyPath = resultsDir.resolve("singly.json");
        Path doublyPath = resultsDir.resolve("doubly.json");
        Path stackArrayPath = resultsDir.resolve("stack_array.json");
        Path stackListPath = resultsDir.resolve("stack_list.json");
        Path queueArrayPath = resultsDir.resolve("queue_array.json");
        Path queueListPath = resultsDir.resolve("queue_list.json");

        if (!Files.exists(arrayPath) || !Files.exists(singlyPath) || !Files.exists(doublyPath) || 
            !Files.exists(stackArrayPath) || !Files.exists(stackListPath) || 
            !Files.exists(queueArrayPath) || !Files.exists(queueListPath)) {
            System.out.println("Faltan archivos JSON en results/. Genera todos los benchmarks primero.");
            return;
        }

        try {
            Map<String, StructureData> dataByStructure = new HashMap<>();
            dataByStructure.put("array", readStructure(arrayPath));
            dataByStructure.put("singly", readStructure(singlyPath));
            dataByStructure.put("doubly", readStructure(doublyPath));
            dataByStructure.put("stack_array", readStructure(stackArrayPath));
            dataByStructure.put("stack_list", readStructure(stackListPath));
            dataByStructure.put("queue_array", readStructure(queueArrayPath));
            dataByStructure.put("queue_list", readStructure(queueListPath));

            Files.createDirectories(resultsDir);
            Path pdfPath = resultsDir.resolve("charts.pdf");
            writeChartsPdf(dataByStructure, pdfPath);

            System.out.println("PDF de graficos generado en: " + pdfPath.toAbsolutePath());
        } catch (IOException | ParseException e) {
            System.out.println("No se pudo generar charts.pdf: " + e.getMessage());
        }
    }

    private static void writeChartsPdf(Map<String, StructureData> dataByStructure, Path pdfPath) throws IOException {
        try (PDDocument document = new PDDocument()) {
            for (String operation : TIME_OPERATIONS) {
                JFreeChart chart = createTimeChart(operation, dataByStructure);
                addChartPage(document, chart);
            }

            JFreeChart memoryChart = createMemoryChart(dataByStructure);
            addChartPage(document, memoryChart);

            JFreeChart groupedBarChart = createGroupedBarChartAt10000(dataByStructure);
            addChartPage(document, groupedBarChart);

            document.save(pdfPath.toFile());
        }
    }

    private static JFreeChart createTimeChart(String operation, Map<String, StructureData> dataByStructure) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String structure : STRUCTURES) {
            XYSeries series = new XYSeries(DISPLAY_NAMES.get(structure));
            StructureData structureData = dataByStructure.get(structure);
            for (int size : SIZES) {
                series.add(size, structureData.getAvg(size, operation));
            }
            dataset.addSeries(series);
        }

        LogAxis xAxis = new LogAxis("Tamano");
        xAxis.setBase(10);
        xAxis.setSmallestValue(1);
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis yAxis = new NumberAxis("Tiempo promedio (ns)");
        yAxis.setAutoRangeIncludesZero(false);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        applySeriesStyles(renderer);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        styleXyPlot(plot);

        JFreeChart chart = new JFreeChart("Tiempo vs Tamano - " + operation, TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private static JFreeChart createMemoryChart(Map<String, StructureData> dataByStructure) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        for (String structure : STRUCTURES) {
            XYSeries series = new XYSeries(DISPLAY_NAMES.get(structure));
            StructureData structureData = dataByStructure.get(structure);
            for (int size : SIZES) {
                series.add(size, structureData.getAvg(size, "memoryBytes"));
            }
            dataset.addSeries(series);
        }

        LogAxis xAxis = new LogAxis("Tamano");
        xAxis.setBase(10);
        xAxis.setSmallestValue(1);
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        NumberAxis yAxis = new NumberAxis("Bytes");
        yAxis.setAutoRangeIncludesZero(true);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
        applySeriesStyles(renderer);

        XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
        styleXyPlot(plot);

        JFreeChart chart = new JFreeChart("Uso de Memoria vs Tamano", TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private static JFreeChart createGroupedBarChartAt10000(Map<String, StructureData> dataByStructure) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String operation : TIME_OPERATIONS) {
            for (String structure : STRUCTURES) {
                double value = dataByStructure.get(structure).getAvg(10000, operation);
                dataset.addValue(Math.max(1.0, value), DISPLAY_NAMES.get(structure), operation);
            }
        }

        CategoryAxis xAxis = new CategoryAxis("Operacion");
        LogAxis yAxis = new LogAxis("Tiempo promedio (ns)");
        yAxis.setBase(10);
        yAxis.setSmallestValue(1);

        BarRenderer renderer = new BarRenderer();
        for(int i = 0; i < STRUCTURES.length; i++) {
            renderer.setSeriesPaint(i, SERIES_COLORS.get(STRUCTURES[i]));
        }

        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setRangeGridlinesVisible(true);

        JFreeChart chart = new JFreeChart("Comparacion General a n=10,000", TITLE_FONT, plot, true);
        chart.setBackgroundPaint(Color.WHITE);
        return chart;
    }

    private static void addChartPage(PDDocument document, JFreeChart chart) throws IOException {
        BufferedImage image = chart.createBufferedImage(CHART_WIDTH, CHART_HEIGHT);
        PDPage page = new PDPage(new PDRectangle(CHART_WIDTH, CHART_HEIGHT));
        document.addPage(page);

        PDImageXObject chartImage = LosslessFactory.createFromImage(document, image);
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(chartImage, 0, 0, CHART_WIDTH, CHART_HEIGHT);
        }
    }

    private static void applySeriesStyles(XYLineAndShapeRenderer renderer) {
        for(int i = 0; i < STRUCTURES.length; i++) {
            renderer.setSeriesPaint(i, SERIES_COLORS.get(STRUCTURES[i]));
        }

        renderer.setDefaultShapesVisible(true);
        renderer.setDefaultShapesFilled(true);
        renderer.setDrawOutlines(true);
    }

    private static void styleXyPlot(XYPlot plot) {
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setRangeGridlinesVisible(true);
    }

    private static StructureData readStructure(Path jsonPath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();

        try (Reader reader = Files.newBufferedReader(jsonPath)) {
            JSONObject root = (JSONObject) parser.parse(reader);
            String structureName = (String) root.get("structure");
            JSONArray results = (JSONArray) root.get("results");

            Map<Integer, Map<String, Metric>> bySize = new LinkedHashMap<>();
            for (Object resultObj : results) {
                JSONObject result = (JSONObject) resultObj;
                int size = toInt(result.get("size"));

                JSONObject operations = (JSONObject) result.get("operations");
                Map<String, Metric> metrics = new HashMap<>();
                for (Object operationKey : operations.keySet()) {
                    String operation = (String) operationKey;
                    JSONObject metricJson = (JSONObject) operations.get(operation);
                    double avg = toDouble(metricJson.get("avg"));
                    String unit = (String) metricJson.get("unit");
                    metrics.put(operation, new Metric(avg, unit));
                }

                bySize.put(size, metrics);
            }

            return new StructureData(structureName, bySize);
        }
    }

    private static int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private static double toDouble(Object value) {
        if (value instanceof Number number) {
            return number.doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }

    private static final class Metric {
        private final double avg;
        private final String unit;

        private Metric(double avg, String unit) {
            this.avg = avg;
            this.unit = unit;
        }
    }

    private static final class StructureData {
        private final String structure;
        private final Map<Integer, Map<String, Metric>> metricsBySize;

        private StructureData(String structure, Map<Integer, Map<String, Metric>> metricsBySize) {
            this.structure = structure;
            this.metricsBySize = metricsBySize;
        }

        private double getAvg(int size, String operation) {
            Map<String, Metric> operations = metricsBySize.get(size);
            if (operations == null) {
                throw new IllegalArgumentException("No hay datos para size=" + size + " en " + structure);
            }
            Metric metric = operations.get(operation);
            if (metric == null) {
                throw new IllegalArgumentException("No hay datos para operacion=" + operation + " en " + structure + " size=" + size);
            }
            return metric.avg;
        }
    }
}
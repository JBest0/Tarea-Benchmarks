import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.BufferedReader;
import java.io.FileReader;

public class Graficos {

    public static void main(String[] args) {

        DefaultCategoryDataset insInicio = new DefaultCategoryDataset();
        DefaultCategoryDataset insFinal = new DefaultCategoryDataset();
        DefaultCategoryDataset insMedio = new DefaultCategoryDataset();
        DefaultCategoryDataset buscar = new DefaultCategoryDataset();
        DefaultCategoryDataset reemplazar = new DefaultCategoryDataset();
        DefaultCategoryDataset eliminar = new DefaultCategoryDataset();
        DefaultCategoryDataset memoria = new DefaultCategoryDataset();

        try {
            BufferedReader br = new BufferedReader(new FileReader("resultados.json"));
            String linea;
            String json = "";

            while ((linea = br.readLine()) != null) {
                json += linea;
            }
            br.close();

            int posicionActual = 0;

            while (true) {
                int inicioBloque = json.indexOf("\"tamano\":", posicionActual);

                if (inicioBloque == -1) {
                    break;
                }

                int siguienteBloque = json.indexOf("\"tamano\":", inicioBloque + 1);

                String bloque;
                if (siguienteBloque == -1) {
                    bloque = json.substring(inicioBloque);
                    posicionActual = json.length();
                } else {
                    bloque = json.substring(inicioBloque, siguienteBloque);
                    posicionActual = siguienteBloque;
                }

                int tam = Integer.parseInt(extraerNumeroSimple(bloque, "\"tamano\":"));

                long insIniD = Long.parseLong(extraerPromedio(bloque, "insertarInicio", "doble"));
                long insIniC = Long.parseLong(extraerPromedio(bloque, "insertarInicio", "circular"));

                long insFinD = Long.parseLong(extraerPromedio(bloque, "insertarFinal", "doble"));
                long insFinC = Long.parseLong(extraerPromedio(bloque, "insertarFinal", "circular"));

                long insMedD = Long.parseLong(extraerPromedio(bloque, "insertarMedio", "doble"));
                long insMedC = Long.parseLong(extraerPromedio(bloque, "insertarMedio", "circular"));

                long busD = Long.parseLong(extraerPromedio(bloque, "buscar", "doble"));
                long busC = Long.parseLong(extraerPromedio(bloque, "buscar", "circular"));

                long repD = Long.parseLong(extraerPromedio(bloque, "reemplazar", "doble"));
                long repC = Long.parseLong(extraerPromedio(bloque, "reemplazar", "circular"));

                long eliD = Long.parseLong(extraerPromedio(bloque, "eliminar", "doble"));
                long eliC = Long.parseLong(extraerPromedio(bloque, "eliminar", "circular"));

                long memD = Long.parseLong(extraerValorEnBloque(bloque, "memoria", "doble"));
                long memC = Long.parseLong(extraerValorEnBloque(bloque, "memoria", "circular"));

                insInicio.addValue(insIniD, "Doble", "" + tam);
                insInicio.addValue(insIniC, "Circular", "" + tam);

                insFinal.addValue(insFinD, "Doble", "" + tam);
                insFinal.addValue(insFinC, "Circular", "" + tam);

                insMedio.addValue(insMedD, "Doble", "" + tam);
                insMedio.addValue(insMedC, "Circular", "" + tam);

                buscar.addValue(busD, "Doble", "" + tam);
                buscar.addValue(busC, "Circular", "" + tam);

                reemplazar.addValue(repD, "Doble", "" + tam);
                reemplazar.addValue(repC, "Circular", "" + tam);

                eliminar.addValue(eliD, "Doble", "" + tam);
                eliminar.addValue(eliC, "Circular", "" + tam);

                memoria.addValue(memD, "Doble", "" + tam);
                memoria.addValue(memC, "Circular", "" + tam);
            }

        } catch (Exception e) {
            System.out.println("Error leyendo JSON");
            e.printStackTrace();
        }

        crearGraficaTiempo("Insertar Inicio", insInicio);
        crearGraficaTiempo("Insertar Final", insFinal);
        crearGraficaTiempo("Insertar Medio", insMedio);
        crearGraficaTiempo("Buscar", buscar);
        crearGraficaTiempo("Reemplazar", reemplazar);
        crearGraficaTiempo("Eliminar", eliminar);
        crearGraficaMemoria("Memoria", memoria);
    }

    public static void crearGraficaTiempo(String titulo, DefaultCategoryDataset dataset) {
        JFreeChart grafica = ChartFactory.createLineChart(
                titulo,
                "Tamaño",
                "Tiempo promedio (ns)",
                dataset
        );

        ChartFrame frame = new ChartFrame(titulo, grafica);
        frame.pack();
        frame.setVisible(true);
    }

    public static void crearGraficaMemoria(String titulo, DefaultCategoryDataset dataset) {
        JFreeChart grafica = ChartFactory.createLineChart(
                titulo,
                "Tamaño",
                "Memoria (bytes)",
                dataset
        );

        ChartFrame frame = new ChartFrame(titulo, grafica);
        frame.pack();
        frame.setVisible(true);
    }

    public static String extraerNumeroSimple(String texto, String clave) {
        int index = texto.indexOf(clave);

        if (index == -1) {
            return "0";
        }

        String sub = texto.substring(index + clave.length());
        String numero = "";

        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);

            if (c >= '0' && c <= '9') {
                numero += c;
            } else if (!numero.equals("")) {
                break;
            }
        }

        if (numero.equals("")) {
            return "0";
        }

        return numero;
    }

    public static String extraerPromedio(String texto, String operacion, String tipo) {
        int iOperacion = texto.indexOf("\"" + operacion + "\"");

        if (iOperacion == -1) {
            return "0";
        }

        int iTipo = texto.indexOf("\"" + tipo + "\"", iOperacion);

        if (iTipo == -1) {
            return "0";
        }

        int iPromedio = texto.indexOf("\"promedio\":", iTipo);

        if (iPromedio == -1) {
            return "0";
        }

        String sub = texto.substring(iPromedio + "\"promedio\":".length());
        String numero = "";

        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);

            if (c >= '0' && c <= '9') {
                numero += c;
            } else if (!numero.equals("")) {
                break;
            }
        }

        if (numero.equals("")) {
            return "0";
        }

        return numero;
    }

    public static String extraerValorEnBloque(String texto, String bloque, String tipo) {
        int iBloque = texto.indexOf("\"" + bloque + "\"");

        if (iBloque == -1) {
            return "0";
        }

        int iTipo = texto.indexOf("\"" + tipo + "\"", iBloque);

        if (iTipo == -1) {
            return "0";
        }

        int iDosPuntos = texto.indexOf(":", iTipo);

        if (iDosPuntos == -1) {
            return "0";
        }

        String sub = texto.substring(iDosPuntos + 1);
        String numero = "";

        for (int i = 0; i < sub.length(); i++) {
            char c = sub.charAt(i);

            if (c >= '0' && c <= '9') {
                numero += c;
            } else if (!numero.equals("")) {
                break;
            }
        }

        if (numero.equals("")) {
            return "0";
        }

        return numero;
    }
}
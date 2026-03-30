import java.io.FileWriter;
import java.io.IOException;

public class DoubleEndedyCircular {

    static class Nodo {
        int dato;
        Nodo siguiente;
        Nodo anterior;

        public Nodo(int dato) {
            this.dato = dato;
            this.siguiente = null;
            this.anterior = null;
        }
    }


    static class ListaDobleExtremo {

        private Nodo inicio;
        private Nodo fin;

        public void insertarInicio(int dato) {
            Nodo nuevo = new Nodo(dato);

            if (inicio == null) {
                inicio = nuevo;
                fin = nuevo;
            } else {
                nuevo.siguiente = inicio;
                inicio.anterior = nuevo;
                inicio = nuevo;
            }
        }

        public void insertarFinal(int dato) {
            Nodo nuevo = new Nodo(dato);

            if (fin == null) {
                inicio = nuevo;
                fin = nuevo;
            } else {
                fin.siguiente = nuevo;
                nuevo.anterior = fin;
                fin = nuevo;
            }
        }

        public void insertarEnPosicion(int dato, int posicion) {
            if (posicion == 0) {
                insertarInicio(dato);
                return;
            }

            Nodo actual = inicio;
            int contador = 0;

            while (contador < posicion - 1 && actual.siguiente != null) {
                actual = actual.siguiente;
                contador++;
            }

            Nodo nuevo = new Nodo(dato);

            nuevo.siguiente = actual.siguiente;
            nuevo.anterior = actual;

            if (actual.siguiente != null) {
                actual.siguiente.anterior = nuevo;
            } else {
                fin = nuevo;
            }

            actual.siguiente = nuevo;
        }

        public boolean buscar(int valor) {
            Nodo actual = inicio;
            while (actual != null) {
                if (actual.dato == valor) return true;
                actual = actual.siguiente;
            }
            return false;
        }

        public void reemplazar(int viejo, int nuevo) {
            Nodo actual = inicio;
            while (actual != null) {
                if (actual.dato == viejo) actual.dato = nuevo;
                actual = actual.siguiente;
            }
        }

        public void eliminar(int valor) {
            Nodo actual = inicio;

            while (actual != null) {
                if (actual.dato == valor) {

                    if (actual == inicio) {
                        eliminarInicio();
                        return;
                    }

                    if (actual == fin) {
                        eliminarFinal();
                        return;
                    }

                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;
                    return;
                }

                actual = actual.siguiente;
            }
        }

        public int eliminarInicio() {
            if (inicio == null) return -1;

            int valor = inicio.dato;

            if (inicio == fin) {
                inicio = null;
                fin = null;
            } else {
                inicio = inicio.siguiente;
                inicio.anterior = null;
            }

            return valor;
        }

        public int eliminarFinal() {
            if (fin == null) return -1;

            int valor = fin.dato;

            if (inicio == fin) {
                inicio = null;
                fin = null;
            } else {
                fin = fin.anterior;
                fin.siguiente = null;
            }

            return valor;
        }
    }

    static class ListaCircular {

        private Nodo inicio;

        public void insertarInicio(int dato) {
            Nodo nuevo = new Nodo(dato);

            if (inicio == null) {
                inicio = nuevo;
                inicio.siguiente = inicio;
                inicio.anterior = inicio;
            } else {
                Nodo ultimo = inicio.anterior;

                nuevo.siguiente = inicio;
                nuevo.anterior = ultimo;

                ultimo.siguiente = nuevo;
                inicio.anterior = nuevo;

                inicio = nuevo;
            }
        }

        public void insertarFinal(int dato) {
            if (inicio == null) {
                insertarInicio(dato);
            } else {
                Nodo nuevo = new Nodo(dato);
                Nodo ultimo = inicio.anterior;

                nuevo.siguiente = inicio;
                nuevo.anterior = ultimo;

                ultimo.siguiente = nuevo;
                inicio.anterior = nuevo;
            }
        }

        public void insertarEnPosicion(int dato, int posicion) {
            if (posicion == 0) {
                insertarInicio(dato);
                return;
            }

            Nodo actual = inicio;
            int contador = 0;

            while (contador < posicion - 1 && actual.siguiente != inicio) {
                actual = actual.siguiente;
                contador++;
            }

            Nodo nuevo = new Nodo(dato);

            nuevo.siguiente = actual.siguiente;
            nuevo.anterior = actual;

            actual.siguiente.anterior = nuevo;
            actual.siguiente = nuevo;
        }

        public boolean buscar(int valor) {
            if (inicio == null) return false;

            Nodo actual = inicio;

            while (true) {
                if (actual.dato == valor) return true;
                actual = actual.siguiente;
                if (actual == inicio) break;
            }

            return false;
        }

        public void reemplazar(int viejo, int nuevo) {
            if (inicio == null) return;

            Nodo actual = inicio;

            while (true) {
                if (actual.dato == viejo) actual.dato = nuevo;
                actual = actual.siguiente;
                if (actual == inicio) break;
            }
        }

        public void eliminar(int valor) {
            if (inicio == null) return;

            Nodo actual = inicio;

            while (true) {
                if (actual.dato == valor) {

                    if (actual.siguiente == actual) {
                        inicio = null;
                        return;
                    }

                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;

                    if (actual == inicio) {
                        inicio = actual.siguiente;
                    }

                    return;
                }

                actual = actual.siguiente;
                if (actual == inicio) break;
            }
        }
    }


    public static void main(String[] args) {

        int[] tamanos = {10, 100, 1000, 10000};
        String json = "[\n";

        for (int i = 0; i < tamanos.length; i++) {

            int t = tamanos[i];

            ListaDobleExtremo lista1 = new ListaDobleExtremo();
            ListaCircular lista2 = new ListaCircular();

            for (int j = 0; j < t; j++) {
                int valor = (int)(Math.random() * 1000);
                lista1.insertarFinal(valor);
                lista2.insertarFinal(valor);
            }

            long inicio, fin;

            //TIEMPOS  

// INSERTAR INICIO
long sumaInsIni1 = 0, sumaInsIni2 = 0;
long[] valsInsIni1 = new long[5];
long[] valsInsIni2 = new long[5];
for (int k = 0; k < 5; k++) {

    ListaDobleExtremo l1 = new ListaDobleExtremo();
    ListaCircular l2 = new ListaCircular();

    for (int j = 0; j < t; j++) {
        int valor = (int)(Math.random() * 1000);
        l1.insertarFinal(valor);
        l2.insertarFinal(valor);
    }

    inicio = System.nanoTime();
    l1.insertarInicio(999);
    fin = System.nanoTime();
    valsInsIni1[k] = fin - inicio;
    sumaInsIni1 += valsInsIni1[k];

    inicio = System.nanoTime();
    l2.insertarInicio(999);
    fin = System.nanoTime();
    valsInsIni2[k] = fin - inicio;
    sumaInsIni2 += valsInsIni2[k];
}
long insIni1 = sumaInsIni1 / 5;
long insIni2 = sumaInsIni2 / 5;


// INSERTAR FINAL
long sumaInsFin1 = 0, sumaInsFin2 = 0;
long[] valsInsFin1 = new long[5];
long[] valsInsFin2 = new long[5];
for (int k = 0; k < 5; k++) {

    ListaDobleExtremo l1 = new ListaDobleExtremo();
    ListaCircular l2 = new ListaCircular();

    for (int j = 0; j < t; j++) {
        int valor = (int)(Math.random() * 1000);
        l1.insertarFinal(valor);
        l2.insertarFinal(valor);
    }

    inicio = System.nanoTime();
    l1.insertarFinal(999);
    fin = System.nanoTime();
    valsInsFin1[k] = fin - inicio;
    sumaInsFin1 += valsInsFin1[k];

    inicio = System.nanoTime();
    l2.insertarFinal(999);
    fin = System.nanoTime();
    valsInsFin2[k] = fin - inicio;
    sumaInsFin2 += valsInsFin2[k];
}
long insFin1 = sumaInsFin1 / 5;
long insFin2 = sumaInsFin2 / 5;


// INSERTAR MEDIO
long sumaInsMed1 = 0, sumaInsMed2 = 0;
long[] valsInsMed1 = new long[5];
long[] valsInsMed2 = new long[5];
for (int k = 0; k < 5; k++) {

    ListaDobleExtremo l1 = new ListaDobleExtremo();
    ListaCircular l2 = new ListaCircular();

    for (int j = 0; j < t; j++) {
        int valor = (int)(Math.random() * 1000);
        l1.insertarFinal(valor);
        l2.insertarFinal(valor);
    }

    inicio = System.nanoTime();
    l1.insertarEnPosicion(999, t/2);
    fin = System.nanoTime();
    valsInsMed1[k] = fin - inicio;
    sumaInsMed1 += valsInsMed1[k];

    inicio = System.nanoTime();
    l2.insertarEnPosicion(999, t/2);
    fin = System.nanoTime();
    valsInsMed2[k] = fin - inicio;
    sumaInsMed2 += valsInsMed2[k];
}
long insMed1 = sumaInsMed1 / 5;
long insMed2 = sumaInsMed2 / 5;


// BUSCAR
long sumaBus1 = 0, sumaBus2 = 0;
long[] valsBus1 = new long[5];
long[] valsBus2 = new long[5];
for (int k = 0; k < 5; k++) {

    ListaDobleExtremo l1 = new ListaDobleExtremo();
    ListaCircular l2 = new ListaCircular();

    for (int j = 0; j < t; j++) {
        int valor = (int)(Math.random() * 1000);
        l1.insertarFinal(valor);
        l2.insertarFinal(valor);
    }

    int val = (int)(Math.random() * 1000);

    inicio = System.nanoTime();
    l1.buscar(val);
    fin = System.nanoTime();
    valsBus1[k] = fin - inicio;
    sumaBus1 += valsBus1[k];

    inicio = System.nanoTime();
    l2.buscar(val);
    fin = System.nanoTime();
    valsBus2[k] = fin - inicio;
    sumaBus2 += valsBus2[k];
}
long bus1 = sumaBus1 / 5;
long bus2 = sumaBus2 / 5;


// REEMPLAZAR
long sumaRep1 = 0, sumaRep2 = 0;
long[] valsRep1 = new long[5];
long[] valsRep2 = new long[5];
for (int k = 0; k < 5; k++) {

    ListaDobleExtremo l1 = new ListaDobleExtremo();
    ListaCircular l2 = new ListaCircular();

    for (int j = 0; j < t; j++) {
        int valor = (int)(Math.random() * 1000);
        l1.insertarFinal(valor);
        l2.insertarFinal(valor);
    }

    inicio = System.nanoTime();
    l1.reemplazar(500, 111);
    fin = System.nanoTime();
    valsRep1[k] = fin - inicio;
    sumaRep1 += valsRep1[k];

    inicio = System.nanoTime();
    l2.reemplazar(500, 111);
    fin = System.nanoTime();
    valsRep2[k] = fin - inicio;
    sumaRep2 += valsRep2[k];
}
long rep1 = sumaRep1 / 5;
long rep2 = sumaRep2 / 5;


            // ELIMINAR
            long sumaEli1 = 0, sumaEli2 = 0;
            long[] valsEli1 = new long[5];
            long[] valsEli2 = new long[5];
            for (int k = 0; k < 5; k++) {

               ListaDobleExtremo l1 = new ListaDobleExtremo();
               ListaCircular l2 = new ListaCircular();

               for (int j = 0; j < t; j++) {
                    int valor = (int)(Math.random() * 1000);
                    l1.insertarFinal(valor);
                    l2.insertarFinal(valor);
                }

                inicio = System.nanoTime();
                l1.eliminarInicio();
                fin = System.nanoTime();
                valsEli1[k] = fin - inicio;
                sumaEli1 += valsEli1[k];

                inicio = System.nanoTime();
                l2.eliminar(999); // o eliminarInicio si quieres hacerlo igual
                fin = System.nanoTime();
                valsEli2[k] = fin - inicio;
                sumaEli2 += valsEli2[k];
             }
            long eli1 = sumaEli1 / 5;
            long eli2 = sumaEli2 / 5;

            // ===== MEMORIA =====
            Runtime runtime = Runtime.getRuntime();

            runtime.gc();
            long memAntes = runtime.totalMemory() - runtime.freeMemory();
            ListaDobleExtremo temp1 = new ListaDobleExtremo();
            for (int j = 0; j < t; j++) temp1.insertarFinal(j);
            long memDespues = runtime.totalMemory() - runtime.freeMemory();
            long mem1 = memDespues - memAntes;

            runtime.gc();
            memAntes = runtime.totalMemory() - runtime.freeMemory();
            ListaCircular temp2 = new ListaCircular();
            for (int j = 0; j < t; j++) temp2.insertarFinal(j);
            memDespues = runtime.totalMemory() - runtime.freeMemory();
            long mem2 = memDespues - memAntes;

            // ===== JSON BONITO =====
            json += "  {\n";
            json += "    \"tamano\": " + t + ",\n";
            json += "    \"tiempos\": {\n";

json += "      \"insertarInicio\": {\n";
json += "        \"doble\": {\"promedio\": " + insIni1 + ", \"valores\": " + arrayToString(valsInsIni1) + "},\n";
json += "        \"circular\": {\"promedio\": " + insIni2 + ", \"valores\": " + arrayToString(valsInsIni2) + "}\n";
json += "      },\n";

json += "      \"insertarFinal\": {\n";
json += "        \"doble\": {\"promedio\": " + insFin1 + ", \"valores\": " + arrayToString(valsInsFin1) + "},\n";
json += "        \"circular\": {\"promedio\": " + insFin2 + ", \"valores\": " + arrayToString(valsInsFin2) + "}\n";
json += "      },\n";

json += "      \"insertarMedio\": {\n";
json += "        \"doble\": {\"promedio\": " + insMed1 + ", \"valores\": " + arrayToString(valsInsMed1) + "},\n";
json += "        \"circular\": {\"promedio\": " + insMed2 + ", \"valores\": " + arrayToString(valsInsMed2) + "}\n";
json += "      },\n";

json += "      \"buscar\": {\n";
json += "        \"doble\": {\"promedio\": " + bus1 + ", \"valores\": " + arrayToString(valsBus1) + "},\n";
json += "        \"circular\": {\"promedio\": " + bus2 + ", \"valores\": " + arrayToString(valsBus2) + "}\n";
json += "      },\n";

json += "      \"reemplazar\": {\n";
json += "        \"doble\": {\"promedio\": " + rep1 + ", \"valores\": " + arrayToString(valsRep1) + "},\n";
json += "        \"circular\": {\"promedio\": " + rep2 + ", \"valores\": " + arrayToString(valsRep2) + "}\n";
json += "      },\n";

json += "      \"eliminar\": {\n";
json += "        \"doble\": {\"promedio\": " + eli1 + ", \"valores\": " + arrayToString(valsEli1) + "},\n";
json += "        \"circular\": {\"promedio\": " + eli2 + ", \"valores\": " + arrayToString(valsEli2) + "}\n";
json += "      }\n";

json += "    },\n";
            json += "    \"memoria\": {\n";
            json += "      \"doble\": " + mem1 + ",\n";
            json += "      \"circular\": " + mem2 + "\n";
            json += "    }\n";
            json += "  }";

            if (i != tamanos.length - 1) json += ",\n";
        }

        json += "\n]";

        try {
            FileWriter archivo = new FileWriter("resultados.json");
            archivo.write(json);
            archivo.close();
            System.out.println("JSON generado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir archivo.");
        }
    }
    public static String arrayToString(long[] arr) {
    String s = "[";
    for (int i = 0; i < arr.length; i++) {
        s += arr[i];
        if (i < arr.length - 1) s += ", ";
    }
    return s + "]";
 }
}
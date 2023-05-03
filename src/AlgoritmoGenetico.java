import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoGenetico {
    private static final int LONGITUD_CROMOSOMA = 11; // 1 mas para el fitness
    private static final int PORCENTAJE_ELITE_SIGUIENTE_GENERACION = 10; // 1 mas para el fitness
    private static final int PORCENTAJE_INDIVIDUOS_MUTAR = 10; // NUMERO DE INDIVIDUOS QUE MUTAREMOS
    private static final int NUM_GENES_MUTAR = 1; // NUMERO DE GENES QUE MUTAREMOS EN CADA CROMOSOMA
    private static boolean solucion_encontrada = false;
    private static int individuo_solucion = 0;
    private static final int ELEMENTOS_POBLACION = 100;
    private static final int MAX_ITERACIONES = 10000;
    //CANTIDAD DE ENTEROS QUE PODRA TENER UN GEN DE 1 A X
    private static final int NUM_VARIACION_GENES = 100;
   // private static final int[] CODIGO_OBJETIVO = {27, 89, 1, 87, 99, 45, 67, 19, 9, 5};
    private static final int[] CODIGO_OBJETIVO = {0, 99, 3, 0, 98, 99, 7, 8, 97, 99};
    //ERROR:  REVISAR POR QUE CON ELEMENTO 99 NO ENCUENTRA SOLUCION
    public static int calcularFitness(int[] cromosoma) {
        Integer fitness = 10; //numero de cifras que difieren del objetivo
        for (int j = 0; j < LONGITUD_CROMOSOMA - 1; j++) {
            if (cromosoma[j] == CODIGO_OBJETIVO[j]) {
                fitness = fitness - 1;
                if (fitness == 0) {
                    solucion_encontrada = true;
                    break;

                }
            }
        }
        return fitness;
    }

    public static int[][] ordenarPorUltimaColumna(int[][] arr) {
        Comparator<int[]> comparator = new Comparator<int[]>() {
            @Override
            public int compare(int[] a, int[] b) {
                return Integer.compare(a[a.length - 1], b[b.length - 1]);
            }
        };
        Arrays.sort(arr, comparator);
        return arr;
    }

    public static void mostrarPoblacion(int[][] poblacion) {
        for (int i = 0; i < ELEMENTOS_POBLACION; i++) {
            System.out.print("INDIVIDUO -->" + i + " -->");
            for (int j = 0; j < LONGITUD_CROMOSOMA; j++) {
                System.out.print(poblacion[i][j] + " |");
            }
            System.out.println();
        }
    }

    // Función para generar siguiente generación de población
    public static int[][] generarSiguienteGeneracion(int[][] poblacion) {
        // Ordenar población actual por fitness
        Arrays.sort(poblacion, Comparator.comparingInt(a -> a[LONGITUD_CROMOSOMA - 1]));

        // Seleccionar el 10% de la población con mejor fitness para el cruce
        int numSeleccionados = poblacion.length / PORCENTAJE_ELITE_SIGUIENTE_GENERACION;
        int[][] seleccionados = new int[numSeleccionados][LONGITUD_CROMOSOMA];
        for (int i = 0; i < numSeleccionados; i++) {
            seleccionados[i] = Arrays.copyOf(poblacion[i], LONGITUD_CROMOSOMA);
        }

        // Generar nueva población cruzando los seleccionados con el resto
        int[][] nuevaPoblacion = new int[ELEMENTOS_POBLACION][LONGITUD_CROMOSOMA];
        Random rnd = new Random();
        int idx = 0;
        int numElite = ELEMENTOS_POBLACION / 10;
        int numNoElite = ELEMENTOS_POBLACION - numElite;

        //recorre los individuos ELITE uno a uno
        //RECORREMOS CADA UNO DE LOS INDIV. ELITE Y LOS METEMOS EN ARRAY NUEVAPOBLACION
        for (int i = 0; i < numSeleccionados; i++) {
            int[] padre = seleccionados[i]; //
            for (int j = 0; j < LONGITUD_CROMOSOMA; j++) {
                nuevaPoblacion[idx][j] = padre[j];
            }
            nuevaPoblacion[idx][10] = 0; // Inicializar fitness a cero
            idx++;
        }

        //RESTO DE INDIVIDUOS NO ELITE LOS MEZCLAMOS CON INDIVIDUOS ELITE DE FORMA ALEATORIA
        // Completar la nueva población con individuos aleatorios
        while (idx < ELEMENTOS_POBLACION) { //RECORREMOS RESTO DE POBLACION

            //TOMAMOS UN CROMOSOMA ELITE AL AZAR
            int cromosomaPadre[] = seleccionados[rnd.nextInt(seleccionados.length)];
            //TOMAMOS EL CROMOSOMA HIJO A MEZCLAR CON ELITE
            int cromosomaHijo[] = poblacion[idx];

            //MEZCLAMOS PADRE E HIJO, padre tiene mayor peso que es ELITE
            int hijo[] = new int[LONGITUD_CROMOSOMA];
           // int puntoCorte = LONGITUD_CROMOSOMA / 2;

            //nos quedaremos a veces con los 5 primeros genes
            //y otras con los 5 ultimos genes
            int inicio = rnd.nextInt(1);
            int fin = 0;
            if (inicio==0){
                inicio = 0;
                fin = LONGITUD_CROMOSOMA/2;
            }
            else {
                inicio = LONGITUD_CROMOSOMA/2;
                fin = LONGITUD_CROMOSOMA;
            }
            hijo = cromosomaPadre; //igualamos el hijo al padre
            for (int j = inicio; j < fin; j++) { //reemplazamos genes en el hijo
                   hijo[j] = cromosomaPadre[j];
            }
            nuevaPoblacion[idx] = hijo;
            idx = idx + 1;
        }
        return nuevaPoblacion;
    }

    static public int[][] mutarPoblacion(int[][] poblacion) {
        //hacemos un cambio pequeño
        //alteramos al azar un individuo de forma pequeña
        //mutaremos un numero pequeño de genes
        //A CADA INDIVIDUO SE LE DA UNA PROBABILIDAD DE MUTACION
        //SEGUN ESA PROBABILIDAD EL INDIVIDUO ES MUTADO O NO

        //MUTAREMOS EL % INDICADO EN LA CONSTANTE
        //DE LOS CROMOSOMAS CON PEOR FITNESS

        double elementos_mutar = poblacion.length * (double) PORCENTAJE_INDIVIDUOS_MUTAR / ELEMENTOS_POBLACION;
        int elem_mutar = (int) elementos_mutar;
        int contador = 1;
        elementos_mutar = (int) (elementos_mutar);
        // for (int i= poblacion.length; i>=0;i++){
        Random r = new Random();
        int individuo = 0;
        while (contador <= elem_mutar) {
            individuo = (int) (Math.random() * (poblacion.length - elem_mutar)) + elem_mutar;
            //elegimos individuo al azar dentro de los seleccionados a mutar
            for (int i = 0; i < NUM_GENES_MUTAR; i++) {
                int gen = (int) (Math.random() * LONGITUD_CROMOSOMA - 1);
                int valor = (int) (Math.random() * (NUM_VARIACION_GENES));
                poblacion[individuo][gen] = valor;
            }
            contador = contador + 1;
        }

        return poblacion;
    }

    public static void main(String[] args) {

        int[][] poblacion = new int[ELEMENTOS_POBLACION][LONGITUD_CROMOSOMA];

        //Crear primera generacion de poblacion de ELEMENTOS_POBLACION
        for (int i = 0; i < ELEMENTOS_POBLACION; i++) {
            for (int j = 0; j < LONGITUD_CROMOSOMA - 1; j++) {
                Random rd = new Random();
                poblacion[i][j] = rd.nextInt(NUM_VARIACION_GENES);
            }
        }

        //ITERAR ESTE BLOQUE HASTA QUE ENCONTRAMOS LA SOLUCION O BIEN
        //LLEGAMOS AL MÁXIMO DE ITERACIONES POSIBLE
        int iteraciones = 1;
        solucion_encontrada = false;

        while (iteraciones <= MAX_ITERACIONES && solucion_encontrada == false) {

            // Calcular y rellenar el fitness para cada individuo
            for (int i = 0; i < ELEMENTOS_POBLACION; i++) {
                //   VectorConFitness[i] = calcularFitness(poblacion[i]);
                poblacion[i][LONGITUD_CROMOSOMA - 1] = calcularFitness(poblacion[i]);
                if (solucion_encontrada == true) {
                    individuo_solucion = i;
                    break;
                }
            }

            if (solucion_encontrada == false) {
                poblacion = ordenarPorUltimaColumna(poblacion);
                //Creamos siguiente generacion cruzando el 10% de la elite con el resto
                poblacion = generarSiguienteGeneracion(poblacion);

                // Calcular el fitness para cada cromosoma
                for (int i = 0; i < ELEMENTOS_POBLACION && solucion_encontrada == false; i++) {
                    //   VectorConFitness[i] = calcularFitness(poblacion[i]);
                    poblacion[i][LONGITUD_CROMOSOMA - 1] = calcularFitness(poblacion[i]);
                }
                poblacion = ordenarPorUltimaColumna(poblacion);
                //mostrarPoblacion(poblacion);

                //MUTAMOS LA POBLACION
                poblacion = mutarPoblacion(poblacion);
                poblacion = ordenarPorUltimaColumna(poblacion);

                iteraciones += 1;
            }
        }
            if (solucion_encontrada == true) {
                //Mostrar POBLACION ORDENADA POR FITNESS
                System.out.println("POBLACION FINAL CON INDIVIDUO/S SOLUCION : ");
                mostrarPoblacion(poblacion);
            }


            System.out.println ("");
            System.out.println("SOLUCION ENCONTRADA : " + solucion_encontrada);
            System.out.println("NÚMERO DE ITERACIONES MAXIMA: " + MAX_ITERACIONES);
            System.out.println("NÚMERO DE ITERACIONES REALIZADAS: " + iteraciones);
            System.out.println("INDIVIDUO SOLUCION : " + individuo_solucion);

        }

    }

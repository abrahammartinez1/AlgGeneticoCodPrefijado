import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;

public class AlgoritmoGenetico {
    private static final int MAX_ITERACIONES = 5000; //NUMERO MÁXIMO DE ITERACIONES
    private static final int[] CODIGO_OBJETIVO = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
    //private static final int[] CODIGO_OBJETIVO = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30};
    //CODIGO PREFIJADO A ENCONTRAR
    private static final int NUM_VARIACION_GENES = 100; //VARIEDAD DE ENTEROS QUE PODRA TENER UN GEN, INCLUIDO EL 0
    private static final int ELEMENTOS_POBLACION = 1000; //NUMERO DE INDIVIDUOS QUE FORMAN LA POBLACION TOTAL
    private static final int LONGITUD_CROMOSOMA = 31; // CANTIDAD DE GENES DE UN INDIVIDUO + 1 ESPACIO PARA EL FITNESS
    private static final int PORCENTAJE_ELITE_SIGUIENTE_GENERACION = 10; //PORCENTAJE DE INDIVIDUOS QUE TOMAREMOS COMO ELITE, POR SU  MEJOR FITNESS
    private static final int PORCENTAJE_INDIVIDUOS_MUTAR = 10; // PORCENTAJE DE INDIVIDUOS DEL TOTAL QUE MUTAREMOS
    private static final int NUM_GENES_MUTAR = 1; // NUMERO DE GENES QUE MUTAREMOS EN CADA CROMOSOMA
    private static boolean solucion_encontrada = false;
    private static int individuo_solucion = 0;

    public static int calcularFitness(int[] cromosoma) {
        Integer fitness = LONGITUD_CROMOSOMA-1; //numero de cifras que difieren del objetivo, INICIALMENTE TODAS
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

            //MEZCLAMOS PADRE E HIJO, padre tiene mayor peso que es ELITE
            int hijo[];

            //nos quedaremos a veces con los 5 primeros genes
            //y otras con los 5 ultimos genes
            int inicio = rnd.nextInt(2); //booleano que controla si tomamos los 5 primeros o los 5 ultimos del padre
            int fin;
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

        double elementos_mutar = poblacion.length * (double) PORCENTAJE_INDIVIDUOS_MUTAR / ELEMENTOS_POBLACION;
        int elem_mutar = ELEMENTOS_POBLACION * (int) elementos_mutar / 100;
        int contador = 1;
        double elementos_elite = poblacion.length * (double) PORCENTAJE_ELITE_SIGUIENTE_GENERACION / ELEMENTOS_POBLACION;
       // int elem_elite = (int)elementos_elite;

        int individuo;
        while (contador <= elem_mutar) {//RECORREMOS EL NUMERO DE INDIVIDUOS A MUTAR SEGUN PORCENTAJE_INDIVIDUOS_MUTAR
            //ELEGIMOS INDIVIDUOS A MUTAR DE ENTRE TODA LA POBLACION
            individuo = (int) (Math.random() * (poblacion.length - elem_mutar)); //SELECCIONAMOS EL INDIVIDUO AL AZAR
            for (int i = 0; i < NUM_GENES_MUTAR; i++) { //LO REPETIMOS CON CADA GEN A MUTAR
                int gen = (int) (Math.random() * LONGITUD_CROMOSOMA - 1); //TOMAMOS 1 GEN AL AZAR que cambiaremos
                int valor = (int) (Math.random() * (NUM_VARIACION_GENES+1)); //VALOR POR EL QUE SUSTITUIREMOS EL GEN, CUALQUIERA DENTRO DE LOS POSIBLES
                poblacion[individuo][gen] = valor;
            }
            contador = contador + 1;
        }
        return poblacion;
    }

    public static void main(String[] args) {
        int[][] poblacion = new int[ELEMENTOS_POBLACION][LONGITUD_CROMOSOMA];
        //CREAMOS PRIMERA GENERACION DE POBLACION ALEATORIAMENTE
        for (int i = 0; i < ELEMENTOS_POBLACION; i++) {
            for (int j = 0; j < LONGITUD_CROMOSOMA - 1; j++) {
                Random rd = new Random();
                poblacion[i][j] = rd.nextInt(NUM_VARIACION_GENES+1);
            }
        }

        //ITERAR ESTE BLOQUE HASTA QUE ENCONTRAMOS LA SOLUCION O BIEN
        //LLEGAMOS AL MÁXIMO DE ITERACIONES POSIBLE
        int iteraciones = 1;
        solucion_encontrada = false;

        while (iteraciones <= MAX_ITERACIONES && !solucion_encontrada) {
            // 1.- CALCULAMOS EL FITNESS PARA CADA INDIVIDUO Y LO METEMOS EN LA ULTIMA POSICION DEL VECTOR
            for (int i = 0; i < ELEMENTOS_POBLACION; i++) { //RECORREMOS TODOS LOS INDIVIDUOS
                poblacion[i][LONGITUD_CROMOSOMA - 1] = calcularFitness(poblacion[i]); //LLAMAMOS A LA FUNCION QUE CALCULA EL FITNESS
                if (solucion_encontrada) {
                    individuo_solucion = i;
                    break;
                }
            }

            if (!solucion_encontrada) {
                // 2.- ORDENAMOS LA POBLACION SEGUN SU FITNESS
                poblacion = ordenarPorUltimaColumna(poblacion);

                // 3.- CREAMOS SIGUIENTE GENERACION CRUZANDO LA ELITE CON EL RESTO DE POBLACION
                poblacion = generarSiguienteGeneracion(poblacion);

                // 4.- CALCULAMOS FITNESS PARA ESTA NUEVA GENERACION
                for (int i = 0; i < ELEMENTOS_POBLACION && !solucion_encontrada; i++) {
                    //   VectorConFitness[i] = calcularFitness(poblacion[i]);
                    poblacion[i][LONGITUD_CROMOSOMA - 1] = calcularFitness(poblacion[i]);
                }

                // 5.- ORDENAMOS LA POBLACION SEGUN SU FITNESS
                poblacion = ordenarPorUltimaColumna(poblacion);

                // 6.- MUTAMOS LA POBLACION, CAMBIAMOS ALGUN GEN DE CADA INDIVIDUO DE TODA LA POBLACION
                poblacion = mutarPoblacion(poblacion);

                // 7.- ORDENAMOS LA POBLACION SEGUN SU FITNESS
                poblacion = ordenarPorUltimaColumna(poblacion);

                iteraciones += 1; //NUEVA ITERACION
            }
        }

        if (solucion_encontrada) {
                //Mostrar POBLACION ORDENADA POR FITNESS
                System.out.println("POBLACION FINAL CON INDIVIDUO/S SOLUCION : ");
                System.out.println();
                mostrarPoblacion(poblacion);
                System.out.println("SOLUCION ENCONTRADA : " + solucion_encontrada);
                System.out.println("NÚMERO DE ITERACIONES MAXIMA: " + MAX_ITERACIONES);
                System.out.println("NÚMERO DE ITERACIONES REALIZADAS: " + iteraciones);
                System.out.println("INDIVIDUO SOLUCION : " + individuo_solucion);
            }
        else {
            System.out.println("POBLACION FINAL SIN INDIVIDUO SOLUCION : ");
            System.out.println();
            mostrarPoblacion(poblacion);
            System.out.println();
            System.out.println("SOLUCION NO ENCONTRADA");
            System.out.println("NÚMERO DE ITERACIONES MAXIMA: " + MAX_ITERACIONES);
            System.out.println("NÚMERO DE ITERACIONES REALIZADAS: " + iteraciones);
        }

    }
}


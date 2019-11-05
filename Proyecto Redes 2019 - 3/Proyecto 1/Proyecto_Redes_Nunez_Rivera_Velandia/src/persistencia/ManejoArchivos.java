/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package persistencia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Velan
 */
public class ManejoArchivos {

    // ----------------------------
    //  Constantes
    // ----------------------------
    // ----------------------------
    //  Atributos
    // ----------------------------
    // ----------------------------
    //  Relaciones
    // ----------------------------
    // ----------------------------
    //  Constructores
    // ----------------------------
    // ----------------------------
    //  Auxiliares
    // ----------------------------
    /**
     * Corresponde al archivo en una ruta especificada por el programador.
     */
    private File archivo = null;

    /**
     * Contiene la información leída del archivo.
     */
    private Scanner entrada = null;

    /**
     * Permite sobrescribir el contenido de un archivo.
     */
    private FileWriter fichero = null;

    /**
     * Permite sobrescribir el contenido de un archivo.
     */
    private PrintWriter dataOutStream = null;

    // ----------------------------
    //  Constructor 
    // ----------------------------
    /**
     * Constructor por defecto de la Clase Manejo de Archivos
     */
    // ----------------------------
    //  Métodos
    // ----------------------------
    /**
     * Método que ingresa los datos de una Embajada específica mediante la carga
     * de un Archivo de Texto
     *
     * @param embajada - Nombre del país del cúal se requiere la información
     * para el Sistema de Citas
     * @param ruta - Nombre del Archivo donde se encuentran todas las Embajadas
     * disponibles
     * @return datosEmbajada - Lista de datos de la Embajada
     * @throws Exception - No es posible abrir el Archivo de Texto
     */
    public List<String[]> leerHostActivos(String IP_RED, String MAC) throws FileNotFoundException {
        
        archivo = new File("persistenciaProyecto\\" + IP_RED + "-" + MAC + ".txt");
        System.out.println("ruta a buscar: persistenciaProyecto\\" + IP_RED + "-" + MAC + ".txt");
        if (archivo.exists()) {
            System.out.println("EL archivo existe");
            try {
                String hostActivo[] = null;
                
                List<String[]> hostActivos = new ArrayList<>();
                
                
                
                entrada = new Scanner(archivo);
                //entrada.nextLine();
                String lineaActual;

                while (entrada.findInLine("#FIN") == null) {
                    lineaActual = entrada.nextLine();
                    //System.out.println("while lineaActual " + lineaActual);
                    hostActivo = lineaActual.split("%");
                   // System.out.println("while lineaActual split" + hostActivo[0] + " " + hostActivo[1]+ " " + hostActivo[2]+ " " + hostActivo[3]);
                    hostActivos.add(hostActivo);

                }
                
                
                /*for (int i = 0; i < hostActivos.size(); i++) {
                System.out.println("Host: " + i + " ");
                for (int j = 0; j < hostActivos.get(i).length; j++) {
                System.out.print(hostActivos.get(i)[j] + " ");
                }
                System.out.println("\n");
                }*/
                return hostActivos;
            } catch (FileNotFoundException ex) {
                //Logger.getLogger(ManejoArchivos.class.getName()).log(Level.SEVERE, null, ex);
                throw new FileNotFoundException("El archivo " + archivo.getName() + " no se encontro");
            }

        } else {
            throw new NullPointerException("El archivo " + archivo.getName() + " no se encontro un sh");
        }

    }

    public void escribirHostActivos(List<String[]> hostActivos, String IP_RED, String MAC) throws IOException {
        try {

            fichero = new FileWriter("persistenciaProyecto\\" + IP_RED + "-" + MAC + ".txt", false); //192.168.0.1-255.255.255.0

            dataOutStream = new PrintWriter(fichero);

            for (int i = 0; i < hostActivos.size(); i++) {

                for (int j = 0; j < hostActivos.get(i).length; j++) {
                    if (j == hostActivos.get(i).length-1)
                    {
                        dataOutStream.print(hostActivos.get(i)[j].replace("%", ""));
                    }else
                        dataOutStream.print(hostActivos.get(i)[j]);
                }
                dataOutStream.println();
            }
            dataOutStream.println("#FIN");
            dataOutStream.println();

        } catch (IOException e) {
            throw e;
        } finally {
            try {
                fichero.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

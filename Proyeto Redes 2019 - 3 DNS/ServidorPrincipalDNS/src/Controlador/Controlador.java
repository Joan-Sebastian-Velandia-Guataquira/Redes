/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controlador;

import Modelo.EmuladorDNS;
import Modelo.IEmulador;
import Persistencia.ManejadorDirecciones;
import Persistencia.ManejoArchivos;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Velan
 */
public class Controlador {

    private IEmulador modelo;
    //private static InterfazConsola vista;
    //private static InterfazGrafica interfazGrafica;
    private ManejoArchivos persistencia;
    private ManejadorDirecciones masterFile;

    public static void main(String[] args) {
        Controlador Coordinador = new Controlador();
        System.out.println("\nFin");
    }

    public Controlador() {
        this.modelo = new EmuladorDNS();
        iniciarServerDNS();
    }

    public void iniciarServerDNS() {

        try {
            masterFile = this.persistencia.leerDirecciones();
        } catch (Exception ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {
            String dominioBuscado = this.modelo.iniciarEmulador();

            System.err.println("---------------------------------------------------");
            System.err.println("EL DOMINO A BUSCAR ES " + dominioBuscado);
            System.err.println("---------------------------------------------------");

            if (dominioBuscado != "") {
                try {

                    String IP = masterFile.buscarDireccion(dominioBuscado);

                    if (IP != null) {
                        this.modelo.enviarRespuesta(IP);
                    } else {
                        System.err.println("---------------------------------------------------");
                        System.err.println("Se intentara con otro servidor " );
                        System.err.println("---------------------------------------------------");

                        this.modelo.servidorAdicional();

                        System.err.println("Se intento----------------------------------------------------- ");
                    }

                } catch (Exception ex) {

                    System.err.println("---------------------------------------------------");
                    System.err.println("Se intentara con otro servidor " + ex.getMessage());
                    System.err.println("---------------------------------------------------");

                    this.modelo.servidorAdicional();

                    System.err.println("Se intento ");

                    //continue;
                }
                

            }

        }

    }
}

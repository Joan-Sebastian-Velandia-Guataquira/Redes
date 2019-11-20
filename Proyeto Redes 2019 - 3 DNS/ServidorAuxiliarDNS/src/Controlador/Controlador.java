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
//101 53 55 100

    public Controlador() {
        this.modelo = new EmuladorDNS();

        try {
            masterFile = this.persistencia.leerDirecciones();
        } catch (Exception ex) {
            Logger.getLogger(Controlador.class.getName()).log(Level.SEVERE, null, ex);
        }

        while (true) {

            String dominioBuscado = this.modelo.iniciarEmuladorAux();
            
            System.err.println("---------------------------------------------------");
            System.err.println("EL DOMINO A BUSCAR ES " + dominioBuscado);
            System.err.println("---------------------------------------------------");
            if (dominioBuscado != "") {
                try {

                    String IP = masterFile.buscarDireccion(dominioBuscado);
                    if (IP != null) {
                        this.modelo.enviarRespuesta(IP);
                    }else
                    {
                        this.modelo.enviarRespuesta("0");
                    }

                } catch (Exception ex) {

                    System.err.println("Error Controlador " + ex.getMessage());

                }

            }

        }

    }

}

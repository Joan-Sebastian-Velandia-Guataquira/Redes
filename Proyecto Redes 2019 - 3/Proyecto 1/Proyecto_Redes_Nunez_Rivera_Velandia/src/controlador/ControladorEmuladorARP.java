/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.WindowConstants;
import modelo.ARP;
import modelo.Dispositivo;
import modelo.Dispositivo;
import modelo.Emulador;
import modelo.Emulador;
import modelo.IEmulador;
import modelo.IEmulador;
import modelo.TiposDispositivos;
import modelo.TiposEstado;
import persistencia.ManejoArchivos;
import utils.Utils;
import vista.InterfazConsola;
import vista.InterfazGrafica;

/**
 *
 * @author Nuñez Santos, Rivera Gustavo, Velandia Joan
 */
public class ControladorEmuladorARP {

    private IEmulador modelo;
    private static InterfazConsola vista;
    private static InterfazGrafica interfazGrafica;
    private ManejoArchivos persistencia;

    public ControladorEmuladorARP() {
        this.vista = new InterfazConsola();
        this.persistencia = new ManejoArchivos();
        this.interfazGrafica = new InterfazGrafica(this);
        this.interfazGrafica.setVisible(true);

    }

    public static void main(String[] args) {

        ControladorEmuladorARP controlador = new ControladorEmuladorARP();
        /*List<String[]> leerHostActivos = ManejoArchivos.leerHostActivos(); 
        ManejoArchivos.escribirHostActivos(leerHostActivos);*/

    }

    public void encontrarRedAnterior(String IPGUI, String mascaraGUI) throws FileNotFoundException {

        try {

            List<String[]> hostActivosAnteriores = new ArrayList<>();

            String IPRed = Utils.darIPRedString(IPGUI, mascaraGUI);
            System.out.println("IP SubRed " + IPRed);

            System.out.println("Se intentará recuperar Host anteriores");

            hostActivosAnteriores = persistencia.leerHostActivos(IPRed, mascaraGUI);

            if (hostActivosAnteriores.size() == 0) {
                interfazGrafica.IP_MascaraVaciaError("Se encontró una sesión anterior con IP: " + IPGUI + " y mascara: " + mascaraGUI + " pero no habian host activos \nSe buscarán nuevamente");
                this.interfazGrafica.getContentPane().removeAll();
                this.encontrarRedNueva(IPGUI, mascaraGUI);
            } else {
                this.encontrarRedNueva(IPGUI, mascaraGUI);

                modelo.crearHosts();

                System.out.println("Se recuperaron Host anteriores");

                List<String> IPsHostActivos = new ArrayList<>();
                List<String> MACsHostActivos = new ArrayList<>();
                List<List<String>> IPsMACsHostActivos = new ArrayList<>();

                for (int i = 0; i < hostActivosAnteriores.size(); i++) {
                    IPsHostActivos.add(hostActivosAnteriores.get(i)[0]);
                    MACsHostActivos.add(hostActivosAnteriores.get(i)[1]);
                }

                IPsMACsHostActivos.add(IPsHostActivos);
                IPsMACsHostActivos.add(MACsHostActivos);

                modelo.setIPsHostActivos(IPsHostActivos);
                modelo.setMACsHostActivos(MACsHostActivos);
                modelo.setIPsMACsHostActivos(IPsMACsHostActivos);
                modelo.crearDispositivos();

                interfazGrafica.mostrarHostAnterioresActivos(hostActivosAnteriores);

            }
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException(ex.getMessage());
            //Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
            throw new NullPointerException("No se encontraron host anteriores con IP: " + IPGUI + " y mascara: " + mascaraGUI);
        }

    }

    public void encontrarRedNueva(String IPGUI, String mascaraGUI) {

        try {

            List<Integer> ipDesc = Utils.descomponer(IPGUI); // Octetos - IP Desompuesta
            List<Integer> masDesc = Utils.descomponer(mascaraGUI); // Octetos - mascara Desompuesta

            List<String> IPRed = new ArrayList<>();
            List<String> IPBroudcast = new ArrayList<>();

            try {

                IPRed = Utils.darIPRed(ipDesc, masDesc);
                System.out.println("IP SubRed " + IPRed);

                IPBroudcast = Utils.darIPBroud(ipDesc, masDesc);
                System.out.println("IP broudcast " + IPBroudcast);

                if (this.validarDatosRed(IPRed, IPBroudcast) == false) {
                    throw new IndexOutOfBoundsException("Campos de Red invalidos 1");
                }

                modelo = new Emulador(IPRed, IPBroudcast, mascaraGUI);
                System.out.println("Se creo el modelo correctamente");
            } catch (IndexOutOfBoundsException e) {
                throw new IndexOutOfBoundsException("Campos de Red invalidos 0");
            }

        } catch (NumberFormatException e) {
            throw new NumberFormatException("Campos de Red Vacios");
        }

    }

    public List<String[]> econtrarDispositivos() throws Exception {

        try {

            modelo.crearHosts();

            System.out.println("Se crearon los posibles Host");

        } catch (Exception e) {

            throw new Exception("No se pudieron crear las IP de los posibles host activos " + e.getMessage());
        }

        try {

            modelo.buscarHostActivos();

            System.out.println("Se buscaron los posibles Host");

        } catch (Exception e) {

            throw new Exception("No se pudieron buscar los host activos porque " + e.getMessage());
        }

        try {

            modelo.crearDispositivos();

            System.out.println("Se crearon los posibles Dispositivos");

            List<Dispositivo> dispositivos = modelo.getDispositivos();
            List<String[]> dis = new ArrayList<>();

            for (int i = 0; i < dispositivos.size(); i++) {
                String[] agg = new String[4];
                agg[0] = dispositivos.get(i).getIP();
                agg[1] = dispositivos.get(i).getMAC();
                agg[2] = dispositivos.get(i).getEstado().toString();
                agg[3] = dispositivos.get(i).getTipo().toString();

                dis.add(agg);
            }

            for (int i = 0; i < dis.size(); i++) {

                System.out.println("Dispositivo creado " + dis.get(i)[0]);

            }
            return dis;

        } catch (Exception e) {

            throw new Exception("No se pudieron crear los dispositivos activos" + e.getMessage());
        }

    }

    private boolean validarDatosRed(List<String> IPRed, List<String> IPBroudcast) {
        for (int i = 0; i < IPRed.size(); i++) {

            if (IPRed.get(i).length() > 3 || IPBroudcast.get(i).length() > 3) {
                return false;
            }

        }
        return true;
    }

    public void guardarHost(List<String[]> hostGuardar) throws Exception {
        try {
            /*for (int i = 0; i < hostGuardar.size(); i++) {
            
            System.out.println("Host " + i);
            
            for (int j = 0; j < hostGuardar.get(i).length; j++) {
            
            System.out.print(hostGuardar.get(i)[j]);
            }
            System.out.println("\n");
            }*/

            List<String> IP_RED = modelo.getIPRed();
            String IP_REDAux = IP_RED.get(0) + "." + IP_RED.get(1) + "." + IP_RED.get(2) + "." + IP_RED.get(3);

            persistencia.escribirHostActivos(hostGuardar, IP_REDAux, modelo.getMascara());

        } catch (IOException ex) {
            //Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("No se pudieron guardar los dispositivos activos en la base de datos" + ex.getMessage());
        }
    }

    public List<String[]> actualizarDispositivos(String cantidadTramas, String tiempo) throws Exception {

        try {
            modelo.actualizarHostActivos(cantidadTramas);

        } catch (Exception e) {

            throw new Exception("No se pudieron buscar los host activos porque " + e.getMessage());////ARREGLAR PRA LECTURA DE ARCHIVO
        }

        try {

            //System.out.println("controlador.ControladorEmuladorARP. le dieron " +  cantidadTramas);
            modelo.actualizarDispositivos(cantidadTramas, tiempo);

            List<Dispositivo> dispositivos = modelo.getDispositivos();
            List<String[]> dis = new ArrayList<>();

            for (int i = 0; i < dispositivos.size(); i++) {

                String[] agg = new String[4];

                agg[0] = dispositivos.get(i).getIP();
                agg[1] = dispositivos.get(i).getMAC();
                agg[2] = dispositivos.get(i).getEstado().toString();
                agg[3] = dispositivos.get(i).getTipo().toString();

                dis.add(agg);
            }
            /*for (int i = 0; i < dis.size(); i++) {
                
               System.out.println("asasa " + dis.get(i)[0]);
                
            }*/
            return dis;

        } catch (Exception e) {

            throw new Exception("No se pudieron crear los dispositivos activos" + e.getMessage());
        }

    }

    public void actualizarTipoDispositivo(String IP, String Tipo) {
        modelo.actualizarTipoDispositivos(IP, Tipo);
    }

}

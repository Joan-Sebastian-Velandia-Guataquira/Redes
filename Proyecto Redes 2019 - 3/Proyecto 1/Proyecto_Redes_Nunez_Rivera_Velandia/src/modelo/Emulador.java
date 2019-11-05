/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import controlador.ControladorEmuladorARP;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import utils.Utils;

/**
 *
 * @author Velan
 */
public class Emulador implements IEmulador {

    private ARP tramaARP = new ARP();

    private String mascara;
    private List<String> IPRed;
    private List<String> IPBroudcast;
    private List<String> IPsHost; // Posibles

    private List<String> IPsHostActivos;
    private List<String> MACsHostActivos;
    private List<List<String>> IPsMACsHostActivos;

    private List<Dispositivo> dispositivos;
    private List<Dispositivo> antiguos;

    public Emulador(List<String> IPRed, List<String> IPBroudcast, String mascaraGUI) {
        this.IPRed = IPRed;
        this.IPBroudcast = IPBroudcast;
        this.mascara = mascaraGUI;
    }

    public void crearHosts() {

        int cantOctantes = 3;

        int i = 0;
        int j = 0;
        int k;

        int limOct4 = Integer.valueOf(IPBroudcast.get(3));
        int limOct3 = Integer.valueOf(IPBroudcast.get(2));
        int limOct2 = Integer.valueOf(IPBroudcast.get(1));

        String IP_ARP = new String(); //IP que se le hará ARP

        String actual1 = new String(); // 1er octante 192.168.1.0 - 192.168.1.x
        String actual2 = new String(); // 2do octante 192.168.0.0 - 192.168.x.255
        String actual3 = new String(); // 3er octante 192.0.0.0 - 192.x.255.255

        this.IPsHost = new ArrayList<>();

        if (this.IPRed.get(2).equals(this.IPBroudcast.get(2))) {
            cantOctantes = 1;
        } else if (this.IPRed.get(1).equals(this.IPBroudcast.get(1))) {
            cantOctantes = 2;
        }

        //System.out.println("cantOctonates " + cantOctantes);
        switch (cantOctantes) {
            case 1:
                for (i = 1; i <= limOct4 - 1; i++) {
                    actual1 = String.valueOf(i);
                    //System.out.println("i " + actual1);

                    IP_ARP = this.IPRed.get(0) + "." + this.IPRed.get(1) + "." + this.IPRed.get(2) + "." + actual1;
                    //System.out.println("IP_ARP " + IP_ARP);

                    this.IPsHost.add(IP_ARP);
                }
                break;
            case 2:
                for (j = 0; j < limOct3; i++) {

                    actual1 = String.valueOf(i);
                    actual2 = String.valueOf(j);

                    IP_ARP = this.IPRed.get(0) + "." + this.IPRed.get(1) + "." + actual2 + "." + actual1;

                    this.IPsHost.add(IP_ARP);
                    if (i == limOct4) {
                        i = 0;
                        j++;
                    }

                }
                break;
            case 3:

                for (k = 0; k < limOct2; i++) {

                    actual1 = String.valueOf(i);
                    actual2 = String.valueOf(j);
                    actual3 = String.valueOf(k);

                    IP_ARP = this.IPRed.get(0) + "." + actual3 + "." + actual2 + "." + actual1;

                    this.IPsHost.add(IP_ARP);
                    if (i == limOct4) {
                        j++;
                        i = 0;
                    }
                    if (j == limOct3) {
                        k++;
                        i = 0;
                        j = 0;
                    }
                }

                break;
        }

    }

    public void buscarHostActivos() { // En Proceso

        try {
            System.out.println("Obteniendo IP de está máquina");
            String IP_Host = InetAddress.getLocalHost().getHostAddress();
            System.out.println("la IP de esta máquina es " + IP_Host);
        } catch (UnknownHostException ex) {
            Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);
            throw new NullPointerException("No se puede acceder a la dirección IP de está máquina");
        }

        this.IPsHostActivos = new ArrayList<>();
        this.MACsHostActivos = new ArrayList<>();

        this.IPsMACsHostActivos = new ArrayList<>(2);

        byte[] mac_destino = null;

        StringBuilder MAC_DestinoString = new StringBuilder();

        String IPHostActual = new String();

        InetAddress ipDestino = null;
        int k = 0;

        System.out.println("Cantidad posibles Host es: " + this.IPsHost.size());

        for (int i = 0; i < this.IPsHost.size(); i++) {

            IPHostActual = this.IPsHost.get(i);
            i = i + 1;
            System.out.println("buscando Host Activo N° " + i + " con  Ip  =  " + IPHostActual);
            i = i - 1;
            try {

                ipDestino = Inet4Address.getByName(IPHostActual);
                System.out.println("enviando trama a " + ipDestino.getHostAddress());
                mac_destino = tramaARP.arp(ipDestino);

                MAC_DestinoString = new StringBuilder(mac_destino.length * 2);

                for (byte b : mac_destino) {
                    MAC_DestinoString.append(String.format("%02x", b)).append(":");
                }

                MAC_DestinoString.deleteCharAt(MAC_DestinoString.length() - 1);

                System.out.println("Ip  =  " + IPHostActual + "  MAC " + MAC_DestinoString.toString());
                IPsHostActivos.add(IPHostActual);
                MACsHostActivos.add(MAC_DestinoString.toString());

                k = 0;
                MAC_DestinoString.delete(0, MAC_DestinoString.length() - 1);

                //System.out.println("MAC " + MAC_DestinoString.toString());
            } catch (NullPointerException e) {
                System.out.println("No esta activo el Host " + ipDestino);
                k++;

            } catch (UnknownHostException ex) {
                Logger.getLogger(Emulador.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IOException | InterruptedException ex) {
                Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);

            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        this.IPsMACsHostActivos.add(IPsHostActivos);
        this.IPsMACsHostActivos.add(MACsHostActivos);
    }

    public void crearDispositivos() {

        dispositivos = new ArrayList<>();
        antiguos = new ArrayList<>();

        Dispositivo dispositivoAcual = null;

        for (int i = 0; i < this.IPsHostActivos.size(); i++) {

            dispositivoAcual = new Dispositivo(this.IPsHostActivos.get(i), this.MACsHostActivos.get(i));
            dispositivos.add(dispositivoAcual);
        }

        antiguos = dispositivos;
    }

    public void actualizarHostActivos(String cantidadTramas) { // En Proceso

      
        try {

            String IP_Host = InetAddress.getLocalHost().getHostAddress();

        } catch (UnknownHostException ex) {
            Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);
            throw new NullPointerException("No se puede acceder a la dirección IP de está máquina");
        }

        this.IPsHost = this.IPsHostActivos;

        for (String string : IPsHost) {
            System.out.println("Direcciones " + string); //Borrar
        }

        this.IPsHostActivos = new ArrayList<>();
        this.MACsHostActivos = new ArrayList<>();
        this.IPsMACsHostActivos = new ArrayList<>(2);

        byte[] mac_destino = null;

        StringBuilder MAC_DestinoString = new StringBuilder();

        String IPHostActual = new String();

        InetAddress ipDestino = null;
        
        if (antiguos.size() == 0) {
            System.out.println("No hay antiguos");
        }
        if (IPsHost.size() == 0) {
            System.out.println("No hay IPsHost");
        }
        boolean flag;

        for (int i = 0; i < this.antiguos.size(); i++) {
            flag = false;
            for (int j = 0; j < Integer.valueOf(cantidadTramas) && !flag; j++) {
                System.out.println("Envio de trama No " + (j + 1));
                IPHostActual = this.antiguos.get(i).getIP();
                i = i + 1;
                System.out.println("buscando Host Activo N° " + i + " con  Ip  =  " + IPHostActual);
                i = i - 1;
                try {

                    //System.out.println("llegue");
                    ipDestino = Inet4Address.getByName(IPHostActual);
                    mac_destino = tramaARP.arp(ipDestino);

                    MAC_DestinoString = new StringBuilder(mac_destino.length * 2);

                    for (byte b : mac_destino) {
                        MAC_DestinoString.append(String.format("%02x", b)).append(":");
                    }

                    MAC_DestinoString.deleteCharAt(MAC_DestinoString.length() - 1);

                    System.out.println("Ip  =  " + IPHostActual + "  MAC " + MAC_DestinoString.toString());
                    IPsHostActivos.add(IPHostActual);
                    MACsHostActivos.add(MAC_DestinoString.toString());
                    MAC_DestinoString.delete(0, MAC_DestinoString.length() - 1);

                    flag = true;
                    //System.out.println("MAC " + MAC_DestinoString.toString());
                } catch (NullPointerException e) {
                    System.out.println("No esta activo el Host " + ipDestino);
                    
                    this.antiguos.get(i).setTramasNoRespondidas( this.antiguos.get(i).getTramasNoRespondidas() + 1);
                    this.dispositivos.get(i).setTramasNoRespondidas( this.antiguos.get(i).getTramasNoRespondidas() + 1);
                    
                } catch (UnknownHostException ex) {
                    Logger.getLogger(Emulador.class.getName()).log(Level.SEVERE, null, ex);

                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(ControladorEmuladorARP.class.getName()).log(Level.SEVERE, null, ex);

                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(e.getMessage());
                }
            }
        }
        this.IPsMACsHostActivos.add(IPsHostActivos);
        this.IPsMACsHostActivos.add(MACsHostActivos);
    }

    public void actualizarDispositivos(String cantidadTramas, String tiempo) {

        //System.out.println("modelo.Emulador.actualizarDispositivos() le dieron" + cantidadTramas);
        int inactivo;
        int pendiente;
        int cantidadTramasInactivo = Integer.valueOf(cantidadTramas); //Parametro;
        int cantidadTramasPendiente = Integer.valueOf(cantidadTramas); //Parametro;
        int tiempoTrama = Integer.valueOf(tiempo) * 60;

        //System.out.println("despues de valuof " + cantidadTramas );
        System.out.println("Cantidad Tramas max: " + Integer.valueOf(cantidadTramas));

        dispositivos = new ArrayList<>();

        Dispositivo dispositivoAcual = null;

        boolean flag;

        List<Dispositivo> temporal = new ArrayList<>();

        Calendar calendario = new GregorianCalendar();

        int hora, minutos, segundos;
        for(int i=0; i < antiguos.size(); i++)
        {
            for (int j = 0; j < IPsHostActivos.size(); j++) {
                if(antiguos.get(i).getIP().equals(IPsHostActivos.get(j)))
                {
                    this.antiguos.get(i).setTramasNoRespondidas(0);
                    this.antiguos.get(i).setTiempoInactivo(0);
                    this.antiguos.get(i).setEstado(TiposEstado.ACTIVO);
                }             
            }   
        }

        for (int i = 0; i < antiguos.size(); i++) {

            if (antiguos.get(i).getTramasNoRespondidas() >= cantidadTramasInactivo + cantidadTramasPendiente) {
                    if(!antiguos.get(i).getEstado().equals(TiposEstado.INACTIVO))
                    {
                        if(antiguos.get(i).getEstado().equals(TiposEstado.PENDIENTE))
                        {
                            LocalDateTime locaDate = LocalDateTime.now();
                            minutos = locaDate.getMinute();
                            segundos = locaDate.getSecond();
                            segundos = segundos + minutos * 60;

                            antiguos.get(i).setEstado(TiposEstado.INACTIVO);
                            antiguos.get(i).setTiempoInactivo(segundos);
                        }
                        
                    }
                    
            }
            if (antiguos.get(i).getTramasNoRespondidas() >= cantidadTramasPendiente && !antiguos.get(i).getEstado().equals(TiposEstado.INACTIVO)) {
                antiguos.get(i).setEstado(TiposEstado.PENDIENTE);
            }
        }
        for (Dispositivo antiguo : antiguos) {
            if (antiguo.getEstado().equals(TiposEstado.INACTIVO)) {
                LocalDateTime locaDate = LocalDateTime.now();
                minutos = locaDate.getMinute();
                segundos = locaDate.getSecond();
                segundos = segundos + minutos * 60;
                int diferencia = segundos - antiguo.getTiempoInactivo();
            if (diferencia >= tiempoTrama) {
                    System.out.println("Eliminado dispositivo " + antiguo.getIP());
                } else {
                    temporal.add(antiguo);
                }
            } else {
                temporal.add(antiguo);
            }

        }

        antiguos = temporal;
        dispositivos = antiguos;

        IPsHostActivos = new ArrayList<>();

        MACsHostActivos = new ArrayList<>();
        for (Dispositivo antiguo2 : antiguos) {
            IPsHostActivos.add(antiguo2.getIP());
            MACsHostActivos.add(antiguo2.getMAC());
        }
        IPsMACsHostActivos = new ArrayList<>();

        IPsMACsHostActivos.add(IPsHostActivos);
        IPsMACsHostActivos.add(MACsHostActivos);
    }

    /*-------------------------------------------------------------------    GET  AND SET --------------------------------------------------------------------------------------*/
    public String getMascara() {
        return mascara;
    }

    public void setMascara(String mascara) {
        this.mascara = mascara;
    }

    public List<Dispositivo> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<Dispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }

    public ARP getTramaARP() {
        return tramaARP;
    }

    public void setTramaARP(ARP tramaARP) {
        this.tramaARP = tramaARP;
    }

    public List<String> getIPRed() {
        return IPRed;
    }

    public void setIPRed(List<String> IPRed) {
        this.IPRed = IPRed;
    }

    public List<String> getIPBroudcast() {
        return IPBroudcast;
    }

    public void setIPBroudcast(List<String> IPBroudcast) {
        this.IPBroudcast = IPBroudcast;
    }

    public List<String> getIPsHost() {
        return IPsHost;
    }

    public void setIPsHost(List<String> IPsHost) {
        this.IPsHost = IPsHost;
    }

    public List<String> getIPsHostActivos() {
        return IPsHostActivos;
    }

    public void setIPsHostActivos(List<String> IPsHostActivos) {
        this.IPsHostActivos = IPsHostActivos;
    }

    public List<String> getMACsHostActivos() {
        return MACsHostActivos;
    }

    public void setMACsHostActivos(List<String> MACsHostActivos) {
        this.MACsHostActivos = MACsHostActivos;
    }

    public List<List<String>> getIPsMACsHostActivos() {
        return IPsMACsHostActivos;
    }

    public void setIPsMACsHostActivos(List<List<String>> IPsMACsHostActivos) {
        this.IPsMACsHostActivos = IPsMACsHostActivos;
    }

    public void actualizarTipoDispositivos(String IP, String Tipo) {
        for (int i = 0; i < dispositivos.size(); i++) {
            System.out.println("Cambiando tipo de host: " + dispositivos.get(i).getIP() + " Por" + Tipo + " Ya que la misma IP es: " + IP);
            if (dispositivos.get(i).getIP().equals(IP)) {
                System.out.println("Cambiando tipo de host: " + dispositivos.get(i).getIP() + " Por" + Tipo + " Ya que la misma IP es: " + IP);
                switch (Tipo.toUpperCase()) { //TIPO

                    case "DESCONOCIDO":
                        dispositivos.get(i).setTipo(TiposDispositivos.DESCONOCIDO);
                        antiguos.get(i).setTipo(TiposDispositivos.DESCONOCIDO);
                        break;
                    case "DESKTOP":
                        dispositivos.get(i).setTipo(TiposDispositivos.DESKTOP);
                        antiguos.get(i).setTipo(TiposDispositivos.DESKTOP);
                        break;
                    case "LAPTOP":
                        dispositivos.get(i).setTipo(TiposDispositivos.LAPTOP);
                        antiguos.get(i).setTipo(TiposDispositivos.LAPTOP);
                        break;
                    case "CELULAR":
                        dispositivos.get(i).setTipo(TiposDispositivos.CELULAR);
                        antiguos.get(i).setTipo(TiposDispositivos.CELULAR);
                        break;
                    case "IMPRESORA":
                        dispositivos.get(i).setTipo(TiposDispositivos.IMPRESORA);
                        antiguos.get(i).setTipo(TiposDispositivos.IMPRESORA);
                        break;
                    case "SMART_TV":
                        dispositivos.get(i).setTipo(TiposDispositivos.SMART_TV);
                        antiguos.get(i).setTipo(TiposDispositivos.SMART_TV);
                        break;
                    case "SMART_WATCH":
                        dispositivos.get(i).setTipo(TiposDispositivos.SMART_WATCH);
                        antiguos.get(i).setTipo(TiposDispositivos.SMART_WATCH);
                        break;
                    case "OTRO":
                        dispositivos.get(i).setTipo(TiposDispositivos.OTRO);
                        antiguos.get(i).setTipo(TiposDispositivos.OTRO);
                        break;
                }

            }
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Utils.Utils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Velan
 */
public class EmuladorDNS implements IEmulador {

    private static final String DNS_SERVER_ADDRESS = "10.0.0.3";
    private static final int DNS_SERVER_PORT = 53;

    private static final int DNS_AUX_SERVER_PORT = 5000;

    private ByteArrayInputStream bais;
    private DataInputStream din;

    private String dominio;

    private InetAddress ipAddress;

    private DNS Datos_DNS;

    private DatagramSocket socket;
    private DatagramPacket Paquete_UDP;

    private DatagramSocket auxSocket;
    private DatagramPacket auxPaquete_UDP;

    private DatagramPacket paqueteRespuestaDNS;

    private byte[] DNS_Pedido; // Aca se guarda la solicitud buffer[]
    private short ID_DNS_Pedido;

    private int portCliente;
    private InetAddress ipCliente;

    private boolean bandera = false;

    public EmuladorDNS() {
        this.socket = null;
        this.dominio = "";
        try {
            //this.ipAddress = InetAddress.getByName(DNS_SERVER_ADDRESS);
            this.ipAddress = InetAddress.getLocalHost();

        } catch (UnknownHostException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        //this.Datos_DNS = new DNS(domain);
    }

    private String DesencapsularDominio() {

        System.out.println("\nSe recibieron: " + Paquete_UDP.getLength() + " bytes: ");

        for (int i = 0; i < this.Paquete_UDP.getLength(); i++) {
            if (i > 0 && 0 == i % 10) {
                System.out.print("\n");
            }
            System.out.print("0x" + String.format("%x", this.DNS_Pedido[i]) + " ");

        }
        System.out.println("\n");

        this.bais = new ByteArrayInputStream(this.DNS_Pedido); // Comvertir paquete a bytes
        this.din = new DataInputStream(this.bais);

        try {

            this.ID_DNS_Pedido = din.readShort();

            System.out.println("ID DNS pedida: 0x" + String.format("%x", this.ID_DNS_Pedido));

        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Flags DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Questions DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Answers RRs DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Authority RRs DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Additional RRs: 0x DNS pedida" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        int tamDominio = 0;
        int iter = 0;
        try {
            while ((tamDominio = din.readByte()) > 0) {
                byte[] record = new byte[tamDominio];

                for (int i = 0; i < tamDominio; i++) {
                    record[i] = din.readByte();
                }
                String recordAcutual = new String(record, "UTF-8");
                System.out.println("Nombre dominio: " + recordAcutual);

                if (iter == 0) {
                    this.dominio = recordAcutual;
                    iter++;
                } else {
                    this.dominio = this.dominio + "." + recordAcutual;
                }

            }
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            short TYPE_Record = din.readShort();
            if (TYPE_Record == 0x01) {
                System.out.println("Record Type DNS pedida: 0x" + String.format("%x", TYPE_Record));
            } else {
                System.out.println("Record Type DNS pedida: 0x" + String.format("%x", TYPE_Record));
                System.out.println("No es tipo A");
                return "";
            }

        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("Class: 0x DNS pedida" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            System.out.println("Field DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("Tipo DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("Clase DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.out.println("TTL DNS pedida: 0x" + String.format("%x", din.readInt()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        short addrLen = 0;
        try {
            addrLen = din.readShort();
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Len DNS pedida: 0x" + String.format("%x", addrLen));

        System.out.print("Address: ");

        /* for (int i = 0; i < addrLen; i++) {

            try {
                System.out.print("" + String.format("%d", (din.readByte() & 0xFF)) + ".");
            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
        return this.dominio;
    }

    @Override
    public void enviarRespuesta(String IP) {
        this.ipCliente = Paquete_UDP.getAddress();
        if (IP.equals("0")) {

            byte[] buf = {0};
            this.paqueteRespuestaDNS = new DatagramPacket(buf, buf.length, this.ipCliente, 5959);
            System.err.println("TAMAÑO: " + buf.length);

            try {
                socket.send(this.paqueteRespuestaDNS);
            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("\n\nEnviando respuesta al dominio solicitado: " + this.dominio + " con ID solicitud: " + this.ID_DNS_Pedido);

            try {
                this.Datos_DNS = new DNS(this.DNS_Pedido, this.dominio, IP);
            } catch (UnknownHostException ex) {
                System.err.println("Error: " + ex.getMessage());
            }
            this.portCliente = 5959;
            this.paqueteRespuestaDNS = new DatagramPacket(Datos_DNS.getDatosDNS(), Datos_DNS.getDatosDNS().length, this.ipCliente, 5959);

            try {
                socket.send(this.paqueteRespuestaDNS);

                InetAddress IP_Respuesta = InetAddress.getByAddress(Datos_DNS.getADRESS());

                System.out.println("\n\nRespuesta enviada al dominio: " + this.dominio + " con IP: " + IP_Respuesta.toString());
                System.out.println("\n\nRespuesta enviada al puesto: " + this.paqueteRespuestaDNS.getPort() + " con IP: " + this.paqueteRespuestaDNS.getAddress());

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    @Override
    public String iniciarEmuladorAux() {

        if (this.socket == null) {
            try {
                this.socket = new DatagramSocket(8989);
            } catch (SocketException ex) {

                System.out.println("AYUDAAAAAA");
            }
        }
        this.DNS_Pedido = new byte[1024];

        this.Paquete_UDP = new DatagramPacket(DNS_Pedido, DNS_Pedido.length);

        try {

            System.out.println("Esperando Datos del Server principal");
            socket.receive(this.Paquete_UDP);

            byte[] concat = new byte[4];

            for (int i = 0; i < 4; i++) {
                concat[i] = DNS_Pedido[i];
            }

            ipCliente = InetAddress.getByAddress(concat);
            byte[] concat2 = new byte[this.Paquete_UDP.getLength() - 4];
            System.out.println("Tama ----------- " + this.Paquete_UDP.getLength());
            for (int i = 4, j = 0; i < this.Paquete_UDP.getLength(); i++, j++) {
                concat2[j] = DNS_Pedido[i];
            }

            portCliente = Utils.bytesToInt(concat2);

            System.out.println("IP cliente : " + ipCliente.toString() + "Puerto cliente " + portCliente);

            //-------------------------------------------SEGUNDO ENVIO--------------------------------------           
            socket.receive(this.Paquete_UDP);
            System.out.println("\nSe recibieron: " + this.Paquete_UDP.getLength() + " bytes: ");

            System.out.println("Tama 2----------- " + this.Paquete_UDP.getLength());
            for (int i = 0; i < this.Paquete_UDP.getLength(); i++) {
                if (i > 0 && 0 == i % 10) {
                    System.out.print("\n");
                }
                System.out.print("0x" + String.format("%x", DNS_Pedido[i]) + " ");

            }
            System.out.println("\n");
            System.out.println(this.Paquete_UDP.getAddress() + " " + this.Paquete_UDP.getPort());

            return DesencapsularDominio();

        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (socket != null) {

            try {

                this.DNS_Pedido = new byte[1024];
                this.Paquete_UDP = new DatagramPacket(this.DNS_Pedido, this.DNS_Pedido.length);

                System.out.println("Esperando Datos del Server principal");
                socket.receive(this.Paquete_UDP);

                byte[] concat = new byte[4];

                for (int i = 0; i < 4; i++) {
                    concat[i] = DNS_Pedido[i];
                }

                ipCliente = InetAddress.getByAddress(concat);
                byte[] concat2 = new byte[this.Paquete_UDP.getLength() - 4];
                System.out.println("Tama ----------- " + this.Paquete_UDP.getLength());
                for (int i = 4, j = 0; i < this.Paquete_UDP.getLength(); i++, j++) {
                    concat2[j] = DNS_Pedido[i];
                }

                portCliente = Utils.bytesToInt(concat2);

                System.out.println("IP cliente : " + ipCliente.toString() + "Puerto cliente " + portCliente);

                try {

                    this.socket.receive(this.Paquete_UDP);
                    System.out.println("Recibi un paquete DNS");

                    return DesencapsularDominio();

                } catch (IOException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        System.out.println("Estoy sordo");
        return null;

    }

}

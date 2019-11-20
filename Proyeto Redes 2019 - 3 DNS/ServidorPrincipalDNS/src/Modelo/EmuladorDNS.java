/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Utils.Utils;
import static com.oracle.jrockit.jfr.ContentType.Bytes;
import com.sun.org.apache.xerces.internal.util.IntStack;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.DatagramSocketImpl;
import java.net.Inet4Address;
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

    private static final String DNS_SERVER_ADDRESS = "255.255.255.255";
    private static final int DNS_SERVER_PORT = 53;

    private static final int DNS_AUX_SERVER_PORT = 8989;

    private ByteArrayInputStream bais;
    private DataInputStream din;

    private String dominio;

    private InetAddress ipAddress;

    private DNS Datos_DNS;

    private DatagramSocket socket;
    private DatagramPacket Paquete_UDP;

    private DatagramSocket auxSocket = null;
    private DatagramPacket auxPaquete_UDP;

    private DatagramSocket auxSocketRespuesta = null;
    private DatagramPacket auxPaquete_UDPRespuesta;

    private DatagramPacket paqueteRespuestaDNS;

    private byte[] DNS_Pedido; // Aca se guarda la solicitud buffer[]
    private short ID_DNS_Pedido;

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

    /**
     *
     * @return
     */
    @Override
    public String iniciarEmulador() {

        if (this.socket == null) {
            try {

                this.socket = new DatagramSocket(this.DNS_SERVER_PORT);
                this.DNS_Pedido = new byte[1024];
                this.Paquete_UDP = new DatagramPacket(this.DNS_Pedido, this.DNS_Pedido.length);
                System.out.println("Voy a escuchar");

                try {

                    this.socket.receive(this.Paquete_UDP);
                    System.out.println("Recibi un paquete");

                    return DesencapsularDominio();

                } catch (IOException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (SocketException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            this.DNS_Pedido = new byte[1024];
            this.Paquete_UDP = new DatagramPacket(this.DNS_Pedido, this.DNS_Pedido.length);
            System.out.println("Voy a escuchar");

            try {

                this.socket.receive(this.Paquete_UDP);
                System.out.println("Recibi un paquete");

                return DesencapsularDominio();

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.err.println("Estoy sordo");
        return null;

    }

    private String DesencapsularDominio() {

        System.out.println("\nSe recibieron: " + Paquete_UDP.getLength() + " bytes: ");

        /*for (int i = 0; i < this.Paquete_UDP.getLength(); i++) {
            if (i > 0 && 0 == i % 10) {
                System.out.print("\n");
            }
            System.out.print("0x" + String.format("%x", this.DNS_Pedido[i]) + " ");

        }
        System.out.println("\n");*/
        this.bais = new ByteArrayInputStream(this.DNS_Pedido); // Comvertir paquete a bytes
        this.din = new DataInputStream(this.bais);

        String lecturaActual;

        try {

            this.ID_DNS_Pedido = din.readShort();

            System.out.println("ID DNS pedida: 0x" + String.format("%x", this.ID_DNS_Pedido));

        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());

            //System.out.println("Flags DNS pedida: 0x" + String.format("%x", lecturaActual));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Questions DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Answers RRs DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Authority RRs DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Additional RRs: 0x DNS pedida" + String.format("%x", din.readShort()));
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
                System.err.println("ES TIPO A\n");

            } else {
                System.out.println("Record Type DNS pedida: 0x" + String.format("%x", TYPE_Record));
                System.err.println("NO ES TIPO A\n");
                return "";
            }

        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Class: 0x DNS pedida" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Field DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Tipo DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lecturaActual = String.valueOf(din.readShort());
            //System.out.println("Clase DNS pedida: 0x" + String.format("%x", din.readShort()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            lecturaActual = String.valueOf(din.readInt());
            //System.out.println("TTL DNS pedida: 0x" + String.format("%x", din.readInt()));
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        /*short addrLen = 0;
        try {
            addrLen = din.readShort();
        } catch (IOException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Len DNS pedida: 0x" + String.format("%x", addrLen));

        System.out.print("Address: ");

         for (int i = 0; i < addrLen; i++) {

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

        System.err.println("\n\n----------------------------------------------------------------------------------------------------------");
        System.err.println("\nEnviando respuesta al dominio solicitado: " + this.dominio + " con ID solicitud: " + this.ID_DNS_Pedido);
        System.err.println("----------------------------------------------------------------------------------------------------------\n");

        try {
            this.Datos_DNS = new DNS(this.DNS_Pedido, this.dominio, IP);

            this.paqueteRespuestaDNS = new DatagramPacket(Datos_DNS.getDatosDNS(), Datos_DNS.getDatosDNS().length, this.Paquete_UDP.getAddress(), this.Paquete_UDP.getPort());

            try {
                socket.send(this.paqueteRespuestaDNS);

                InetAddress IP_Respuesta = InetAddress.getByAddress(Datos_DNS.getADRESS());

                System.err.println("\n\n-------------------------------------------------------------------------------------------");
                System.err.println("\nRespuesta enviada al dominio: " + this.dominio + " con IP: " + IP_Respuesta.toString());
                System.err.println("-------------------------------------------------------------------------------------------\n\n");

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (UnknownHostException ex) {
            System.err.println("Error No se pudo crear los datos DNS + IP: " + ex.getMessage());
        }

    }

    @Override
    public void servidorAdicional() {
        try {
            InetAddress IPserverAux = InetAddress.getByName("100.0.0.6");
            if (this.auxSocket == null) {

                try {

                    this.auxSocket = new DatagramSocket();

                    InetAddress ipCliente = Paquete_UDP.getAddress();
                    byte[] ipClienteByte = ipCliente.getAddress();

                    int portCliente = Paquete_UDP.getPort();
                    System.out.println("La ip del cliente es: " + ipCliente.toString() + " el puerto es: " + portCliente);

                    byte[] portClienteByte = null;
                    try {
                        portClienteByte = Utils.intToBytes(portCliente);
                    } catch (IOException ex) {
                        Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    byte[] finalData = new byte[ipClienteByte.length + portClienteByte.length];
                    System.arraycopy(ipClienteByte, 0, finalData, 0, ipClienteByte.length);
                    System.arraycopy(portClienteByte, 0, finalData, ipClienteByte.length, portClienteByte.length);

                    this.auxPaquete_UDP = new DatagramPacket(finalData, finalData.length, IPserverAux, this.DNS_AUX_SERVER_PORT);

                    try {
                        this.auxSocket.send(this.auxPaquete_UDP); //DATOS del cliente 1°
                    } catch (IOException ex) {
                        Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    //----------------------------SEgundo------------------
                    try {

                        this.auxPaquete_UDP = new DatagramPacket(this.DNS_Pedido, this.Paquete_UDP.getLength(), IPserverAux, this.DNS_AUX_SERVER_PORT);

                        this.auxSocket.send(this.auxPaquete_UDP); // ¿que pasa si el otro no lo encuentra?

                    } catch (IOException ex) {
                        Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } catch (SocketException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {

                InetAddress ipCliente = Paquete_UDP.getAddress();
                byte[] ipClienteByte = ipCliente.getAddress();

                int portCliente = Paquete_UDP.getPort();
                System.out.println("La ip del cliente es: " + ipCliente.toString() + " el ´puerto es: " + portCliente);

                byte[] portClienteByte = null;

                try {
                    portClienteByte = Utils.intToBytes(portCliente);
                } catch (IOException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }

                byte[] finalData = new byte[ipClienteByte.length + portClienteByte.length];
                System.arraycopy(ipClienteByte, 0, finalData, 0, ipClienteByte.length);
                System.arraycopy(portClienteByte, 0, finalData, ipClienteByte.length, portClienteByte.length);

                this.auxPaquete_UDP = new DatagramPacket(finalData, finalData.length, IPserverAux, this.DNS_AUX_SERVER_PORT);

                try {
                    this.auxSocket.send(this.auxPaquete_UDP); //DATOS del cliente 1°
                } catch (IOException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }
                bandera = true;

                try {

                    this.auxPaquete_UDP = new DatagramPacket(this.DNS_Pedido, this.Paquete_UDP.getLength(), IPserverAux, this.DNS_AUX_SERVER_PORT);
                    this.auxSocket.send(this.auxPaquete_UDP); // ¿que pasa si el otro no lo encuentra?
                    recibirDatosServidorAuxiliarDNS();
                } catch (IOException ex) {
                    Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void recibirDatosServidorAuxiliarDNS() {

        if (this.auxSocketRespuesta == null) {
            try {
                this.auxSocketRespuesta = new DatagramSocket(5959);
            } catch (SocketException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }

            byte[] DatosServAuxDNS = new byte[1024];
            this.auxPaquete_UDPRespuesta = new DatagramPacket(DatosServAuxDNS, DatosServAuxDNS.length);
            try {
                System.out.println("ENTRE 5");
                auxSocketRespuesta.receive(auxPaquete_UDPRespuesta);
                System.out.println("ENTRE 6");

                if (auxPaquete_UDPRespuesta.getLength() > 1) {

                    System.out.println("\nSe recibieron: " + this.auxPaquete_UDPRespuesta.getLength() + " bytes desde el servidor AUXLIAR ");

                    for (int i = 0; i < this.auxPaquete_UDPRespuesta.getLength(); i++) {
                        if (i > 0 && 0 == i % 10) {
                            System.out.print("\n");
                        }
                        System.out.print("0x" + String.format("%x", DatosServAuxDNS[i]) + " ");

                    }

                    this.paqueteRespuestaDNS = new DatagramPacket(DatosServAuxDNS, DatosServAuxDNS.length, this.Paquete_UDP.getAddress(), this.Paquete_UDP.getPort());
                    socket.send(paqueteRespuestaDNS);

                }

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            System.out.println("ENTRE 1_1");
            byte[] DatosServAuxDNS = new byte[1024];
            this.auxPaquete_UDPRespuesta = new DatagramPacket(DatosServAuxDNS, DatosServAuxDNS.length);

            try {

                auxSocketRespuesta.receive(auxPaquete_UDPRespuesta);
                System.out.println("ENTRE 1_2");
                if (auxPaquete_UDPRespuesta.getLength() > 1) {
                    System.out.println("\nSe recibieron: " + this.auxPaquete_UDPRespuesta.getLength() + " bytes desde el servidor AUXLIAR(else) ");

                    for (int i = 0; i < this.auxPaquete_UDPRespuesta.getLength(); i++) {
                        if (i > 0 && 0 == i % 10) {
                            System.out.print("\n");
                        }
                        System.out.print("0x" + String.format("%x", DatosServAuxDNS[i]) + " ");

                    }
                    if (this.auxPaquete_UDPRespuesta.getLength() > 1) {
                        this.paqueteRespuestaDNS = new DatagramPacket(DatosServAuxDNS, DatosServAuxDNS.length, this.Paquete_UDP.getAddress(), this.Paquete_UDP.getPort());
                        socket.send(paqueteRespuestaDNS);
                    }
                }

            } catch (IOException ex) {
                Logger.getLogger(EmuladorDNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}

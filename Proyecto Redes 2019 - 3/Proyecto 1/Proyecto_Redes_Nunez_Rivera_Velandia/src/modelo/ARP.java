package modelo;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Arrays;

import jpcap.*;
import jpcap.packet.*;

public class ARP {

    private  InetAddress ip;
    
    private  int maxSniffer = 4;
    private  int cantDatosFrame = 2000;
    private  int tiempo_ms = 1500;

    private  byte[] todos = new byte[]{(byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255, (byte) 255};

    private  NetworkInterface[] NIC = JpcapCaptor.getDeviceList();
    private  NetworkInterface NIC_Buscado = null;

    private  InetAddress IPEstaMaquina = null;

    private  boolean modoPromiscuo = false;
    private  boolean optimizar = true;

    private  ARPPacket paqueteARP_Enviar = new ARPPacket(); //Creación paquete ARP
    private  EthernetPacket ethernetPaqueteEnviar = new EthernetPacket(); // Crear paquete ethernet
    private  JpcapSender sender;
    private  JpcapCaptor captor;
    
    public  void buscarNIC() {

        buscarNIC:
        for (NetworkInterface adaptador : NIC) {

            for (NetworkInterfaceAddress direccionActual : adaptador.addresses) {

                //System.out.println("adaptador.addresses " + adaptador.addresses);
                if (!(direccionActual.address instanceof Inet4Address)) {
                    continue;
                }

                byte[] bip = ip.getAddress();
                /*for (int i = 0; i < ip.getAddress().length; i++) {
                 System.out.print("bip " +  Integer.toBinaryString(ip.getAddress()[i])  );       
                }
                System.out.println("\n");*/

                byte[] subRed = direccionActual.subnet.getAddress();
                /*for (int i = 0; i < direccionActual.subnet.getAddress().length; i++) {
                 System.out.print("subRed " +  Integer.toBinaryString(direccionActual.subnet.getAddress()[i])  );       
                }
                System.out.println("\n");*/

                byte[] bif = direccionActual.address.getAddress();
                /*for (int i = 0; i < direccionActual.address.getAddress().length; i++) {
                 System.out.print("bif " +  Integer.toBinaryString(direccionActual.address.getAddress()[i])  );       
                }
                System.out.println("\n");*/

                for (int i = 0; i < 4; i++) {

                    bip[i] = (byte) (bip[i] & subRed[i]);
                    bif[i] = (byte) (bif[i] & subRed[i]);
                }

                if (Arrays.equals(bip, bif)) {

                    this.NIC_Buscado = adaptador;
                    break buscarNIC;
                }
            }
            if (this.NIC_Buscado == null) {
                throw new IllegalArgumentException(ip + ": no es una dirección IP valida o que pertenezca a esta red LAN");
            }

        }

    }

    public ARP() {
    }
 
    public  byte[] arp(InetAddress ip) throws java.io.IOException, InterruptedException, IllegalArgumentException {
        //find network interface
        this.ip = ip;
        
        this.buscarNIC();
        //open Jpcap
        captor = JpcapCaptor.openDevice(NIC_Buscado, cantDatosFrame, modoPromiscuo, tiempo_ms);

        captor.setFilter("arp", optimizar); //Filtro, optimizar

        sender = captor.getJpcapSenderInstance();

        for (NetworkInterfaceAddress IPsNICs : NIC_Buscado.addresses) {

            if (IPsNICs.address instanceof Inet4Address) {
                this.IPEstaMaquina = IPsNICs.address;
                break;
            }
        }
        
        this.crearPaqueteEhernet();
        this.crearPaqueteARP();
        return this.enviarARP();
              
    }

    private  void crearPaqueteEhernet() {
        ethernetPaqueteEnviar.frametype = 2054; //  ETHERTYPE_ARP = 2054;
        ethernetPaqueteEnviar.src_mac = NIC_Buscado.mac_address; // MAC de Esta maquina
        ethernetPaqueteEnviar.dst_mac = todos; // 255.255.255.255.255.255
    }

    private  void crearPaqueteARP() {
          /*(ar $ hrd) = ares_hrd $ Ethernet
         (ar $ pro) = ET (IP)
         (ar $ hln) = longitud (EA (X))
         (ar $ pln) = longitud (IPA (X))
         (ar $ op) = ares_op $ SOLICITUD
         (ar $ sha) = EA (X)
         (ar $ spa) = IPA (X)
         (ar $ tha) = no me importa
         (ar $ tpa) = IPA (Y)*/
 /*La máquina Y recibe este paquete y determina que comprende
        el tipo de hardware (Ethernet), que habla lo indicado
        protocolo (Internet) y que el paquete es para ello
        ((ar $ tpa) = IPA (Y)).  Entra (probablemente reemplazando cualquier existente
        entrada) la información que <ET (IP), IPA (X)> se asigna a EA (X).  Eso
        luego se da cuenta de que es una solicitud, por lo que intercambia campos, colocando
        EA (Y) en el nuevo campo de dirección Ethernet del remitente (ar $ sha), establece el
        opcode para responder y envía el paquete directamente (no transmitido) a
        EA (X).  En este punto, Y sabe cómo enviar a X, pero X todavía
        no sabe cómo enviar a Y.

        La máquina X obtiene el paquete de respuesta de Y, forma el mapa de
        <ET (IP), IPA (Y)> a EA (Y), nota que el paquete es una respuesta y
        lo tira  La próxima vez que el módulo de Internet de X intente enviar
        un paquete a Y en Ethernet, la traducción tendrá éxito y
        el paquete llegará (con suerte).  Si el módulo de Internet de Y entonces
        quiere hablar con X, esto también tendrá éxito ya que Y ha recordado
        la información de la solicitud de X para la resolución de direcciones.*/
        this.paqueteARP_Enviar.hardtype = 1;     // HARDTYPE_ETHER = 1     (ar $ hrd) = ares_hrd $ Ethernet
        this.paqueteARP_Enviar.prototype = 2048; // PROTOTYPE_IP = 2048     (ar $ pro) = ET (IP)
        this.paqueteARP_Enviar.operation = 1;    // ARP_REQUEST = 1         (ar $ op) = ares_op $ SOLICITUD

        this.paqueteARP_Enviar.hlen = 6;         //                         (ar $ hln) = longitud (EA (X))
        this.paqueteARP_Enviar.plen = 4;         //                         (ar $ pln) = longitud (IPA (X))

        this.paqueteARP_Enviar.sender_hardaddr = this.NIC_Buscado.mac_address; //(ar $ sha) dirección Ethernet(IP de esta maquina) del remitente (ar $ sha)
        this.paqueteARP_Enviar.sender_protoaddr = this.IPEstaMaquina.getAddress();     //(ar $ spa) = IPA (X)

        this.paqueteARP_Enviar.target_hardaddr = this.todos;                    //(ar $ tha) = no me importa ---- MAC DESTINO ---- Broudcast
        this.paqueteARP_Enviar.target_protoaddr = this.ip.getAddress();         // (ar $ tpa) = IPA (Y)  - IP maquina destino  

        this.paqueteARP_Enviar.datalink = this.ethernetPaqueteEnviar;
    }

    private  byte[] enviarARP() {
        
        this.sender.sendPacket(this.paqueteARP_Enviar); // Enviar Paquete ARP
        int j = 0;
        while (j < this.maxSniffer) { //Cantidad de paquetes leidos

            ARPPacket paqueteARP_Actual = (ARPPacket) captor.getPacket();

            if (paqueteARP_Actual == null) {
                System.out.println("No se encontro el dispositivo con la ip" + ip);
                //Thread.sleep(50);
            } else if (Arrays.equals(paqueteARP_Actual.target_protoaddr, this.IPEstaMaquina.getAddress())) {
                return paqueteARP_Actual.sender_hardaddr;
            }
            j++;
        }

        return null;
    }

}

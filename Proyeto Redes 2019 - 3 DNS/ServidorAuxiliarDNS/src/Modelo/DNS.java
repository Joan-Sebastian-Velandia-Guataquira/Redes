/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import Utils.Utils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.print.DocFlavor;

/**
 *
 * @author Velan
 */
public class DNS {

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    private final DataOutputStream dos;

    private byte[] ID = Utils.hexStringToByteArray("0000"); // Parametro
    private byte[] FLAGS = Utils.hexStringToByteArray("8100");
    private byte[] QUESTIONS = Utils.hexStringToByteArray("0001");
    private byte[] ANSWERS = Utils.hexStringToByteArray("0001");
    private byte[] AUTORITY_RRS = Utils.hexStringToByteArray("0000");
    private byte[] ADICIONAL_RRS = Utils.hexStringToByteArray("0000");

    private byte[] NAME = Utils.hexStringToByteArray("0000"); // Parametro
    private byte[] NAME_LENGTH = Utils.hexStringToByteArray("0000");// Parametro
    private byte[] LABEL_COUNT = Utils.hexStringToByteArray("0000"); // Parametro
    private byte[] TYPE = Utils.hexStringToByteArray("0001"); // A
    private byte[] CLASS = Utils.hexStringToByteArray("0001"); // A

    //---------------- RESPUESTA ----------------
    private byte[] NAME2 = Utils.hexStringToByteArray("c00c");
    private byte[] TYPE2 = Utils.hexStringToByteArray("0001");
    private byte[] CLASS2 = Utils.hexStringToByteArray("0001");
    private byte[] TTL = Utils.hexStringToByteArray("00000708");
    private byte[] DATA_LENGTH = Utils.hexStringToByteArray("0004");
    private byte[] ADRESS = Utils.hexStringToByteArray("0D352BA7");//Parametro

    private byte[] DatosDNS; // Aca se guarda 

    private final int CAMPOS_CONSTANTES = 6;
    private final int CAMPOS_RESPUESTA = 11;
    private final List<byte[]> camposDatosDNS = new ArrayList<>();

    private String dominio = null;

    public DNS(byte[] buf, String dominio, String IP) throws UnknownHostException {

        this.dos = new DataOutputStream(this.baos);
        this.dominio = dominio;

        this.ID[0] = buf[0]; //0x00
        this.ID[1] = buf[1]; //0x02

        camposDatosDNS.add(this.ID);
        camposDatosDNS.add(this.FLAGS);
        camposDatosDNS.add(this.QUESTIONS);
        camposDatosDNS.add(this.ANSWERS);
        camposDatosDNS.add(this.AUTORITY_RRS);
        camposDatosDNS.add(this.AUTORITY_RRS);
        camposDatosDNS.add(this.NAME);
        camposDatosDNS.add(this.NAME_LENGTH);
        camposDatosDNS.add(this.LABEL_COUNT);
        camposDatosDNS.add(this.TYPE);
        camposDatosDNS.add(this.CLASS);

        camposDatosDNS.add(this.NAME2);
        camposDatosDNS.add(this.TYPE2);
        camposDatosDNS.add(this.CLASS2);
        camposDatosDNS.add(this.TTL);
        camposDatosDNS.add(this.DATA_LENGTH);

        this.ADRESS = Utils.StringToBytes(IP);

        camposDatosDNS.add(this.ADRESS);

        crearDatosDNS();
        responderDNS();

        visualizarDNS_Request();
    }

    private void crearDominio() {

        String[] partesDominio = this.dominio.split("\\.");
        System.out.println("\nEl dominio: " + this.dominio + " se compone de " + partesDominio.length + " partes");
        byte[] escribirDominioEnBytes = null;

        for (String parteDominioActual : partesDominio) {

            System.out.println("Escribiendo Parte: " + parteDominioActual);

            try {
                escribirDominioEnBytes = parteDominioActual.getBytes("UTF-8");

            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.dos.writeByte(escribirDominioEnBytes.length);//NAME_Length

            } catch (IOException ex) {
                Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                this.dos.write(escribirDominioEnBytes); //NAME

            } catch (IOException ex) {
                Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private void visualizarDNS_Request() {

        DatosDNS = this.baos.toByteArray();

        System.out.println("\nDatos creados con un tamaño de: " + DatosDNS.length + " bytes: ");

        for (int i = 0; i < DatosDNS.length; i++) {

            if (i > 0 && i % 10 == 0) {
                System.out.print("\n");
            }
            System.out.print("0x" + String.format("%x", DatosDNS[i]) + " ");
        }

    }

    private void crearDatosDNS() {

        for (int i = 0; i < CAMPOS_CONSTANTES; i++) // ID, FLAGS, QUESTIONS, ANSWERS, AUTORITY_RRS, AUTORITY_RRS
        {
            try {
                this.dos.write(this.camposDatosDNS.get(i));

            } catch (IOException ex) {
                Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        crearDominio();

        try {
            this.dos.writeByte(0x00);

            this.dos.write(this.camposDatosDNS.get(9));  // TYPE 
            this.dos.write(this.camposDatosDNS.get(10)); // CLASS

        } catch (IOException ex) {
            Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void responderDNS() {
        for (int i = CAMPOS_RESPUESTA; i < camposDatosDNS.size(); i++) // NAME2, TYPE2, CLASS2, TTL, DATA_LENGTH, ADRESS
        {
            try {
                this.dos.write(this.camposDatosDNS.get(i));
            } catch (IOException ex) {
                Logger.getLogger(DNS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public byte[] getDatosDNS() {
        return DatosDNS;
    }

    public byte[] getADRESS() {
        return ADRESS;
    }

}

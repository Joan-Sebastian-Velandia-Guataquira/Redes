/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Velan
 */
public class Utils {

    public static List< Integer> descomponer(String ip) {

        String[] octetosS = ip.split("\\.");

        List< Integer> octetos = new ArrayList<>();

        for (String octetosS1 : octetosS) {
            octetos.add(Integer.valueOf(octetosS1));
        }

        return octetos;
    }

    public static List<String> darIPRed(List<Integer> ipDesc, List<Integer> masDesc) {

        List<String> redIP = new ArrayList<>();

        List<String> octetoIP = new ArrayList<String>();
        List<String> octetoMas = new ArrayList<String>();

        //Integer.toBinaryString() no guarda los 8 bytes, si hay ceros a la izquierda no los guarda
        char[] octAuxMas = {'0', '0', '0', '0', '0', '0', '0', '0'};
        char[] octAuxIP = {'0', '0', '0', '0', '0', '0', '0', '0'};

        //Para un conteo al reves, cuando no se guardan los 8 bytes
        int k;

        //System.out.println("ipDesc " + ipDesc);
        //System.out.println("masDesc " + masDesc);
        for (int i = 0; i < 4; i++)//por cada octeto
        {
            octetoMas.add(i, Integer.toBinaryString(masDesc.get(i))); // Este octeto se convierte a binario y se almacena en un string, finalmente se añade a la lista de octetos en binario

            if (octetoMas.get(i).equalsIgnoreCase("0")) // Si el octeto es  0(decimal), Integer.toBinaryString() no almacena los ocho bytes
            {
                octetoMas.set(i, "00000000");

            } else if (octetoMas.get(i).length() < 8) //Integer.toBinaryString() no guarda los 8 bytes, si hay ceros a la izquierda no los guarda
            {
                //Se empieza a copiar los bytes de atrás hacia delante hasta que se completan los ceros a la derecha
                octAuxMas = octetoMas.get(i).toCharArray();
                k = octetoMas.get(i).length() - 1;

                for (int j = 7; j > 0 && k >= 0; j--, k--) {
                    octAuxMas[j] = octetoMas.get(i).toCharArray()[k];
                }
                octetoMas.set(i, String.copyValueOf(octAuxMas)); // Se reasignan los 8 bytes
            }

            octetoIP.add(i, Integer.toBinaryString(ipDesc.get(i)));
            if (octetoIP.get(i).equalsIgnoreCase("0")) {
                octetoIP.set(i, "00000000");
            } else if (octetoIP.get(i).length() < 8) {
                //System.out.println("utils.Utils.darIPRed()"+octetoIP.get(i).length()+ " " + octetoIP.get(i));

                k = octetoIP.get(i).length() - 1;
                for (int j = 7; j > 0 && k >= 0; j--, k--) {
                    octAuxIP[j] = octetoIP.get(i).toCharArray()[k];
                }
                octetoIP.set(i, String.copyValueOf(octAuxIP));
            }

            //System.out.println("iter " + i +" mas " + octetoMas.get(i));
            //System.out.println("iter " + i +" IP " + octetoIP.get(i));
        }

        //System.out.println("oc IP "+ octetoIP);
        //System.out.println("oc mas "+ octetoMas);
        for (int i = 0; i < octetoMas.size(); i++)//por cada octeto
        {

            if (octetoMas.get(i).contains("0")) // si el octeto tiene bytes en 0 significa que ese byte es un host
            {

                octAuxMas = octetoMas.get(i).toCharArray();
                octAuxIP = octetoIP.get(i).toCharArray();

                //System.out.println("octAuxMas "+ octAuxMas[0]+ octAuxMas[1]+ octAuxMas[2]+ octAuxMas[3]+ octAuxMas[4]+ octAuxMas[5]+ octAuxMas[6]+ octAuxMas[7]);
                //System.out.println("octAuxIP "+ octAuxIP[0]+ octAuxIP[1]+ octAuxIP[2]+ octAuxIP[3]+ octAuxIP[4]+ octAuxIP[5]+ octAuxIP[6]+ octAuxIP[7]);
                for (int j = 0; j < octAuxMas.length; j++) //se busca la posición en el octeto de la dirección de la máscara donde está el cero, para poner en 0 el byte correspondiente en la direccion IP
                {
                    if (octAuxMas[j] == '0') {
                        octAuxIP[j] = octAuxMas[j];
                    }
                }

                octetoIP.set(i, String.copyValueOf(octAuxIP));
            }

        }
        //System.out.println("IPRED" + octetoIP);

        String octetoDecimal = new String();
        int decimal;

        for (int i = 0; i < octetoIP.size(); i++) {
            decimal = (Integer.parseInt(octetoIP.get(i), 2));
            octetoDecimal = String.valueOf(decimal);
            octetoIP.set(i, octetoDecimal);
        }
        //System.out.println("occc " + octetoDecimal);

        return octetoIP;
    }

    public static List<String> darIPBroud(List<Integer> ipDesc, List<Integer> masDesc) {
        List<String> redIP = new ArrayList<>();

        List<String> octetoIP = new ArrayList<String>();
        List<String> octetoMas = new ArrayList<String>();

        //Integer.toBinaryString() no guarda los 8 bytes, si hay ceros a la izquierda no los guarda
        char[] octAuxMas = {'0', '0', '0', '0', '0', '0', '0', '0'};
        char[] octAuxIP = {'0', '0', '0', '0', '0', '0', '0', '0'};

        //Para un conteo al reves, cuando no se guardan los 8 bytes
        int k;

        //System.out.println("ipDesc " + ipDesc);
        //System.out.println("masDesc " + masDesc);
        for (int i = 0; i < 4; i++)//por cada octeto
        {
            octetoMas.add(i, Integer.toBinaryString(masDesc.get(i))); // Este octeto se convierte a binario y se almacena en un string, finalmente se añade a la lista de octetos en binario

            if (octetoMas.get(i).equalsIgnoreCase("0")) // Si el octeto es  0(decimal), Integer.toBinaryString() no almacena los ocho bytes
            {
                octetoMas.set(i, "00000000");

            } else if (octetoMas.get(i).length() < 8) //Integer.toBinaryString() no guarda los 8 bytes, si hay ceros a la izquierda no los guarda
            {
                //Se empieza a copiar los bytes de atrás hacia delante hasta que se completan los ceros a la derecha
                octAuxMas = octetoMas.get(i).toCharArray();
                k = octetoMas.get(i).length() - 1;

                for (int j = 7; j > 0 && k >= 0; j--, k--) {
                    octAuxMas[j] = octetoMas.get(i).toCharArray()[k];
                }
                octetoMas.set(i, String.copyValueOf(octAuxMas)); // Se reasignan los 8 bytes
            }

            octetoIP.add(i, Integer.toBinaryString(ipDesc.get(i)));
            if (octetoIP.get(i).equalsIgnoreCase("0")) {
                octetoIP.set(i, "00000000");
            } else if (octetoIP.get(i).length() < 8) {
                //System.out.println("utils.Utils.darIPRed()"+octetoIP.get(i).length());
                k = octetoIP.get(i).length() - 1;
                for (int j = 7; j > 0 && k >= 0; j--, k--) {
                    octAuxIP[j] = octetoIP.get(i).toCharArray()[k];
                }
                octetoIP.set(i, String.copyValueOf(octAuxIP));
            }
        }

        //System.out.println("oc IP "+ octetoIP);
        //System.out.println("oc mas "+ octetoMas);
        for (int i = 0; i < octetoMas.size(); i++)//por cada octeto
        {

            if (octetoMas.get(i).contains("0")) // si el octeto tiene bytes en 0 significa que ese byte es un host
            {

                octAuxMas = octetoMas.get(i).toCharArray();
                octAuxIP = octetoIP.get(i).toCharArray();

                //System.out.println("octAuxMas "+ octAuxMas[0]+ octAuxMas[1]+ octAuxMas[2]+ octAuxMas[3]+ octAuxMas[4]+ octAuxMas[5]+ octAuxMas[6]+ octAuxMas[7]);
                //System.out.println("octAuxIP "+ octAuxIP[0]+ octAuxIP[1]+ octAuxIP[2]+ octAuxIP[3]+ octAuxIP[4]+ octAuxIP[5]+ octAuxIP[6]+ octAuxIP[7]);
                for (int j = 0; j < octAuxMas.length; j++) //se busca la posición en el octeto de la dirección de la máscara donde está el cero, para poner en 0 el byte correspondiente en la direccion IP
                {
                    if (octAuxMas[j] == '0') {
                        octAuxIP[j] = '1';
                    }
                }

                octetoIP.set(i, String.copyValueOf(octAuxIP));
            }

        }
        //System.out.println("IPRED" + octetoIP);

        String octetoDecimal = new String();
        int decimal;

        for (int i = 0; i < octetoIP.size(); i++) {
            decimal = (Integer.parseInt(octetoIP.get(i), 2));
            octetoDecimal = String.valueOf(decimal);
            octetoIP.set(i, octetoDecimal);
        }

        return octetoIP;
    }

    public static String darIPRedString(String IPGUI, String mascaraGUI) {

        List<String> dadaIPRed = darIPRed(descomponer(IPGUI), descomponer(mascaraGUI));

        String IP_RED_String = new String(dadaIPRed.get(0) + "." + dadaIPRed.get(1) + "." + dadaIPRed.get(2) + "." + dadaIPRed.get(3));

        //System.out.println("IP_RED_SRING " + IP_RED_String );
        return IP_RED_String;
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");

        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }

        return output.toString();
    }
}

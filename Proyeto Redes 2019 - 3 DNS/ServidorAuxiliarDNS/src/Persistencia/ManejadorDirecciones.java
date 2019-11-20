/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import java.util.List;

/**
 *
 * @author Velan
 */
public class ManejadorDirecciones {
    

    private List<String> direccionesIP;
    private List<String> direcciones;

    public ManejadorDirecciones( List<String> direccionesIP, List<String> direcciones) {

        this.direccionesIP = direccionesIP;
        this.direcciones = direcciones;
    }

    public void setDireccionesIP(List<String> direccionesIP) {
        this.direccionesIP = direccionesIP;
    }

    public void setDirecciones(List<String> direcciones) {
        this.direcciones = direcciones;
    }

    public List<String> getDireccionesIP() {
        return direccionesIP;
    }

    public List<String> getDirecciones() {
        return direcciones;
    }
    
    public String buscarDireccion(String dominioBuscado)
    {
        String encontrada = null; 
        if ( dominioBuscado.startsWith("www.") )
        {
            if (dominioBuscado.endsWith("com") || dominioBuscado.endsWith("co"))
            {
                 dominioBuscado = dominioBuscado.substring(4, dominioBuscado.length()) + ".";
            }
            else
            {
                dominioBuscado = dominioBuscado.substring(4, dominioBuscado.length()) + ".com.";
            }
            
            
            System.err.println("EL WWW ahora es: " + dominioBuscado);
            
        } else {
            dominioBuscado = dominioBuscado + "."; 
        }
        String dire; 
        
        for (int i = 0; i < direcciones.size(); i++) {
            dire = direcciones.get(i); 
            if(dire.equalsIgnoreCase(dominioBuscado))
            {
                encontrada = direccionesIP.get(i);
                break; 
            }
        }
        System.err.println("---------------------------------------------------");
        System.err.println("IP " + encontrada + " Dominio "+ dominioBuscado);
        System.err.println("---------------------------------------------------");
        return encontrada; 
    }
}

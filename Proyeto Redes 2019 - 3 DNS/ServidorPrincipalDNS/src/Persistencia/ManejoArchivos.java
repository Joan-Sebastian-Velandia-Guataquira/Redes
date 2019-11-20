/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Persistencia;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Velan
 */
public class ManejoArchivos {
    
    public  static ManejadorDirecciones leerDirecciones() throws Exception
	{
            ManejadorDirecciones man = null; 
            ArrayList<String> listaAux = new ArrayList<>(); 
            ArrayList<String> listaTem = new ArrayList<>();
            String dominio =""; 
            String direccionIP;
            String direccion; 
            String temporal; 
            String[] parts; 
            

            BufferedReader br = new BufferedReader (new FileReader("./masters.txt"));
            String linea = br.readLine();
            boolean bandera = false;
            String linea2 = linea; 
            parts = linea2.split(" ");
            if(linea2 != null)
                    linea2 = br.readLine();;
                if(linea2 != null)
                    linea2 = br.readLine();
            
            while(linea2!= null)
            {             
                dominio = parts[1];
                linea2 = br.readLine(); 
                bandera = false;
                temporal = ""; 
                linea = linea2; 
                
                while(linea != null) 
                {
                    direccion = "";
                    direccionIP = "";
                    parts = linea.split(" "); 
                    if(parts[0].toString().equals("$ORIGIN"))
                    {
                        
                        bandera = true;
                        break; 
                    }
                        
                    if(!parts[0].toString().equals("$TTL") )
                    {                      
                    
                    for(int i = 0; i < parts.length;i++)
                    {
                        if(parts[i].toString().equals(";"))
                        {
                            break; 
                        }
                        if(!parts[i].toString().equals(" ") ) //Posible error
                        {
                            if(i == 0)
                            {
                                if(parts[0].toString().equalsIgnoreCase(dominio))
                                    direccion = dominio; 
                                else
                                {
                                    direccion = parts[0].toString() +"."+ dominio;    
                                    if(parts[0].toString().equals(""))
                                    {
                                        direccion = temporal; 
                                    }
                                } 
                            }
                            if(i > 0 && !parts[i].toString().equals("A") && !parts[i].toString().equals("AAAA") && !parts[i].toString().equals("IN") )
                            {
                                direccionIP= parts[i].toString();     
                                
                            }
                            

                        }
                        if(!direccion.equals("")&& !direccionIP.equals(""))
                        {
                            listaAux.add(direccionIP);
                            listaTem.add(direccion);

                        }
                        
                    }                        
                    
                    temporal = direccion;
                    }            
                    if(linea != null)
                        linea = br.readLine();
                }
                if(!bandera)
                {
                    if(linea2 != null)
                    linea2 = br.readLine();
                    if(linea2 != null)
                        linea2 = br.readLine();
                }
                
               
            }
             man = new ManejadorDirecciones(listaAux, listaTem);

            
            br.close();
            return man;
	}
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

/**
 *
 * @author Velan
 */
public interface IEmulador {
    
    public void crearHosts();
    public void buscarHostActivos();
    public void crearDispositivos();
    
    public void actualizarDispositivos(String tiempo, String cantidadTramas);
    public void actualizarHostActivos(String cantidadTramas);
            
    public String getMascara();
    public void setMascara(String mascara);
    public List<Dispositivo> getDispositivos();
    public void setDispositivos(List<Dispositivo> dispositivos);
    public ARP getTramaARP();
    public void setTramaARP(ARP tramaARP);
    public List<String> getIPRed();
    public void setIPRed(List<String> IPRed);
    public List<String> getIPBroudcast();
    public void setIPBroudcast(List<String> IPBroudcast);
    public List<String> getIPsHost();
    public void setIPsHost(List<String> IPsHost);
    public List<String> getIPsHostActivos();
    public void setIPsHostActivos(List<String> IPsHostActivos);
    public List<String> getMACsHostActivos();
    public void setMACsHostActivos(List<String> MACsHostActivos);
    public List<List<String>> getIPsMACsHostActivos();
    public void setIPsMACsHostActivos(List<List<String>> IPsMACsHostActivos);

    public void actualizarTipoDispositivos(String IP, String Tipo);

}

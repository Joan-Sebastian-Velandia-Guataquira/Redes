/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

/**
 *
 * @author Velan
 */
public class Dispositivo 
{
    
    private String IP;
    private String MAC;
    private TiposDispositivos tipo ;
    private TiposEstado estado;
    private int tramasNoRespondidas;
    private int tiempoInactivo;

    public Dispositivo(String IP, String MAC, TiposDispositivos tipo, TiposEstado estado)
    {
        this.IP = IP;
        this.MAC = MAC;
        this.tipo = tipo;
        this.estado = estado;
        this.tramasNoRespondidas = 0;
        this.tiempoInactivo = 0;
    }
    public Dispositivo(String IP, String MAC)
    {
        this.IP = IP;
        this.MAC = MAC;
        this.tipo = TiposDispositivos.DESCONOCIDO;
        this.estado = TiposEstado.ACTIVO;
        this.tramasNoRespondidas = 0;
        this.tiempoInactivo = 0;
    }

    public int getTramasNoRespondidas() {
        return tramasNoRespondidas;
    }

    public void setTramasNoRespondidas(int tramasNoRespondidas) {
        this.tramasNoRespondidas = tramasNoRespondidas;
    }

    public int getTiempoInactivo() {
        return tiempoInactivo;
    }

    public void setTiempoInactivo(int tiempoInactivo) {
        this.tiempoInactivo = tiempoInactivo;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public TiposEstado getEstado() {
        return estado;
    }

    public void setEstado(TiposEstado estado) {
        this.estado = estado;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public TiposDispositivos getTipo() {
        return tipo;
    }

    public void setTipo(TiposDispositivos tipo) {
        this.tipo = tipo;
    }
    
    
}

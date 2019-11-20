/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author Velan
 */
public interface IEmulador 
{
    public String iniciarEmulador();

    public void enviarRespuesta(String IP);

    public void servidorAdicional();
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vista;


import controlador.ControladorEmuladorARP;
import java.awt.BorderLayout;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

/**
 *
 * @author Velan
 */
public class InterfazGrafica extends JFrame {

    private ControladorEmuladorARP controlador;
    private PanelMenu panelMenu;
    private PanelDispositivos panelDispositivos;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    public long invervalo = 60;
    private boolean actualizando = false;

    public InterfazGrafica(ControladorEmuladorARP pControlador) {
        
        setTitle("Emulador ARP");
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                cerrar();
            }
        });
        getContentPane().setLayout(new BorderLayout());

        controlador = pControlador;
        panelMenu = new PanelMenu(this);

        this.setSize(panelMenu.getSize());
        this.getContentPane().add(panelMenu, BorderLayout.CENTER);
        this.pack();

        System.out.println("----------Actualizar Automatico--------");
        
        /*try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error en Hilo------");
        }*/
        
        actualizar();

    }

    public void buscarHostActivosAnterior(String IP, String mascara) {

        try {

            controlador.encontrarRedAnterior(IP, mascara);

            this.econtrarDispositivos();
            
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            JOptionPane.showMessageDialog(null, "no se pudo leer el archivo correctamente " + e.getMessage());

            this.buscarHostActivosNuevos(IP, mascara);

        } catch (FileNotFoundException ex) {
            //Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage());

            this.buscarHostActivosNuevos(IP, mascara);
        }
    }

    public void buscarHostActivosNuevos(String IP, String mascara) {

        try {

            controlador.encontrarRedNueva(IP, mascara);

            this.econtrarDispositivos();

        } catch (NumberFormatException e) {

            System.out.println(e.getMessage());
            this.IP_MascaraVaciaError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            System.out.println(e.getMessage());
            this.IP_MascaraVaciaError(e.getMessage());
        }

    }

    public void econtrarDispositivos() {

        try {

            List<String[]> econtrarDispositivos = controlador.econtrarDispositivos();
            panelDispositivos = new PanelDispositivos(this, econtrarDispositivos);

            this.setSize(panelDispositivos.getSize());
            this.getContentPane().add(panelDispositivos, BorderLayout.CENTER);
            this.pack();

        } catch (Exception e) {
            this.IP_MascaraVaciaError(e.getMessage());
            System.out.println("No se pudieron crear disp " + e.getMessage());
        }

    }

    public void IP_MascaraVaciaError(String message) {
        
        this.getContentPane().removeAll();
        this.getContentPane().add(panelMenu, BorderLayout.CENTER);
        JOptionPane.showMessageDialog(null, message);

    }

    public void cerrar() {
        this.setVisible(false);
        this.dispose();
        
        if ( this.panelDispositivos != null && this.panelDispositivos.isGuardar()) {
            try {
                List<String[]> hostGuardar = this.panelDispositivos.getDispositivos();
                controlador.guardarHost(hostGuardar);
            } catch (Exception ex) {
                //Logger.getLogger(InterfazGrafica.class.getName()).log(Level.SEVERE, null, ex);
                IP_MascaraVaciaError(ex.getMessage());
            }
        }
        System.exit(0);
    }

    public void mostrarHostAnterioresActivos(List<String[]> hostActivosAnteriores) {

        panelDispositivos = new PanelDispositivos(this, hostActivosAnteriores);
        
        this.setSize(panelDispositivos.getSize());
        this.getContentPane().add(panelDispositivos, BorderLayout.CENTER);
        this.pack();

        this.panelDispositivos.llenarT(hostActivosAnteriores);
    }

    public void actualizar() {
        
         actualizando = true;
        
        
        final Runnable beeper = new Runnable() {
            public void run() {
                
                String tiempo = panelDispositivos.getTiempo();
                String cantidadTramas = panelDispositivos.getCantidadTramas();
                
                if (tiempo != null && cantidadTramas != null) {

                    System.out.println("--Actualizar automatico--");
                    
                    //System.out.println(".run() " +  cantidadTramas);
                    
                    actualizarDispositivos(cantidadTramas, tiempo);
                    
                }
            }
        };
        final ScheduledFuture<?> beeperHandle
                = scheduler.scheduleAtFixedRate(beeper, this.invervalo + 120, this.invervalo, SECONDS);
        scheduler.schedule(new Runnable() {
            public void run() {
                beeperHandle.cancel(true);
            }
        }, 60 * 60, SECONDS);
        
        
    }
    
    public void actualizarDispositivos(String cantidadTramas, String tiempo) {
        
        try {
            
            System.out.println("...Actualizando...");
            
           // System.out.println("vista.InterfazGrafica tiene + " +  cantidadTramas);
            
            List<String[]> actualizarDispositivos = controlador.actualizarDispositivos(cantidadTramas, tiempo);
            
            /*System.out.println("Estados actuales");
            
            for (int i = 0; i < actualizarDispositivos.size(); i++) {
                System.out.println("Estado " + actualizarDispositivos.get(i)[2]);
            }*/
            
            this.panelDispositivos = new PanelDispositivos(this, actualizarDispositivos);
            
            this.panelDispositivos.setTxtFldMaxTramasNoRecividas(cantidadTramas);
            this.getContentPane().removeAll();
            this.setSize(panelDispositivos.getSize());
            this.getContentPane().add(panelDispositivos, BorderLayout.CENTER);
            this.pack();
            
            actualizando = false;
            System.out.println("...Actualizado..."); 
            
        } catch (Exception e) {
            
            this.IP_MascaraVaciaError(e.getMessage());
            
            System.out.println(e.getMessage());
        }

    }
    
    void actualizarDispositivo(String IP, String Tipo) {
        controlador.actualizarTipoDispositivo(IP, Tipo);
    }

    public ControladorEmuladorARP getControlador() {
        return controlador;
    }

    public void setControlador(ControladorEmuladorARP controlador) {
        this.controlador = controlador;
    }

    public PanelMenu getPanelMenu() {
        return panelMenu;
    }

    public void setPanelMenu(PanelMenu panelMenu) {
        this.panelMenu = panelMenu;
    }

    public PanelDispositivos getPanelDispositivos() {
        return panelDispositivos;
    }

    public void setPanelDispositivos(PanelDispositivos panelDispositivos) {
        this.panelDispositivos = panelDispositivos;
    }

    public long getInvervalo() {
        return invervalo;
    }

    public void setInvervalo(long invervalo) {
        this.invervalo = invervalo;
    }

    public boolean isActualizando() {
        return actualizando;
    }

    public void setActualizando(boolean actualizando) {
        this.actualizando = actualizando;
    }
    
    
}

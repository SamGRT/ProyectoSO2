/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package model;
import edd.ListaEnlazada;
import gui.MainWindow;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author sarazo
 */
public class ProyectoSO2 {

    public static void main(String[] args) {
        
 try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            try {
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        MainWindow ventana = new MainWindow();
        
        // Agregar WindowListener para guardar al cerrar
        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                model.PersistenciaManager.guardarEstado(ventana.getSistemaArchivos());
                System.out.println("Estado guardado al cerrar la aplicación");
                System.exit(0);
            }
        });
        
        ventana.setVisible(true);
    }}
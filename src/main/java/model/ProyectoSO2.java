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
        
        
        //final SistemaArchivos sistema = PersistenciaManager.cargarEstado();
        MainWindow ventana = new MainWindow();
        
        //ventana.addWindowListener(new WindowAdapter() {
           // @Override
            //public void windowClosing(WindowEvent e) {
               // PersistenciaManager.guardarEstado(sistema);
                //System.exit(0);
            //}});
        ventana.setVisible(true);
        
    }}
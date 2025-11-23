/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import model.Archivo;
import model.Directorio;

/**
 * Renderizador personalizado para mostrar diferentes íconos
 * en el árbol de archivos según si el nodo es un archivo o un directorio.
 * 
 * @author Samantha
 */
public class CustomTreeRendered extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(
            JTree tree,
            Object value,
            boolean sel,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        // Llama al método padre para mantener los estilos base
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

        // Asegurarse de que el valor sea un nodo del árbol
        if (value instanceof DefaultMutableTreeNode node) {
            Object userObject = node.getUserObject();

            // Si es un directorio → usar íconos de carpeta
            if (userObject instanceof Directorio) {
                if (expanded) {
                    setIcon(getOpenIcon());
                } else {
                    setIcon(getClosedIcon());
                }
            }

            // Si es un archivo → usar el ícono de hoja
            else if (userObject instanceof Archivo) {
                setIcon(getLeafIcon());
            }
        }

        return this;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import edd.ListaEnlazada;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import model.Archivo;
import model.Directorio;
import model.FileSystemElement;
import model.Proceso;
import model.SistemaArchivos;

/**
 *
 * @author Samantha
 */
public class MainWindow extends javax.swing.JFrame {
    private SistemaArchivos sistemaArchivos;
    private DefaultTreeModel treeModel;
    private DefaultMutableTreeNode rootNode;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainWindow.class.getName());
    //Para el disco>
    private javax.swing.JPanel diskPanel;
    private edd.ListaEnlazada blocks;
    private static final int TOTAL_BLOCKS = 100;
    private static final int BLOCKS_PER_ROW = 10;
    DefaultTableModel Tm=new DefaultTableModel();
    DefaultTableModel Tcola= new DefaultTableModel();
    private javax.swing.Timer processTimer;

    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        sistemaArchivos = new SistemaArchivos();
        InicializarArbol();
        inicializarVistaDisco();
        

// === TIMER para atender solicitudes de E/S progresivamente ===
processTimer = new javax.swing.Timer(5000, e -> {
    edd.Cola solicitudes = sistemaArchivos.getSolicitudesES();
    
    if (!solicitudes.isEmpty()) {
        // Usar el planificador para obtener el proceso a atender según la política
        Proceso procesoAtendido = sistemaArchivos.atenderSolicitudES();
        
        if (procesoAtendido != null) {
            System.out.println("Proceso completado y removido: " + procesoAtendido.getName());
        }
        
        // Refrescar vista
        actualizarTablaCola();
    }
});
processTimer.start();
////////////////////////////////////////////


       String titles[]= {"Nombre Archivo", "Bloques Asignados", "Dir Primer Bloque"};
        Tm.setColumnIdentifiers(titles);
        jTable1.setModel(Tm);
        
        
        String titlesCola[] = {"ID Proceso", "Archivo Solicitado", "Bloque Actual", "Estado"};
        Tcola.setColumnIdentifiers(titlesCola);
        jTable2.setModel(Tcola);
        
        actualizarTablaArchivos();
        actualizarTablaCola();
    }
    
    private void InicializarArbol(){
     // Crear el nodo raíz a partir del directorio raíz del sistema
        Directorio raiz = sistemaArchivos.getRaiz();
        rootNode = new DefaultMutableTreeNode(raiz);
        treeModel = new DefaultTreeModel(rootNode);
         jTree1.setModel(treeModel);
         jTree1.setCellRenderer(new CustomTreeRendered());
          jTree1.expandPath(new TreePath(rootNode.getPath()));

    }
    
    private DefaultMutableTreeNode getSelectedNode() {
    TreePath selectedPath = jTree1.getSelectionPath();
    if (selectedPath != null) {
        return (DefaultMutableTreeNode) selectedPath.getLastPathComponent();
    }
    return null;
}
private void agregarNodoAlArbol(DefaultMutableTreeNode parentTreeNode, Directorio directorio) {
    FileSystemElement nuevoElemento = directorio.buscarElemento(FileDirName.getText().trim());
    if (nuevoElemento != null) {
        DefaultMutableTreeNode nuevoNodo = new DefaultMutableTreeNode(nuevoElemento);
        treeModel.insertNodeInto(nuevoNodo, parentTreeNode, parentTreeNode.getChildCount());
        jTree1.scrollPathToVisible(new TreePath(nuevoNodo.getPath()));
    }
}

private void inicializarVistaDisco(){
    jPanel6.setLayout(new java.awt.BorderLayout());
    //titulo
    javax.swing.JLabel titleLabel = new javax.swing.JLabel("DISCO - BLOQUES DE ALMACENAMIENTO", javax.swing.SwingConstants.CENTER);
    titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
    jPanel6.add(titleLabel, java.awt.BorderLayout.NORTH);
    //panel de bloques
    diskPanel = new javax.swing.JPanel();
    diskPanel.setLayout(new java.awt.GridLayout(0, BLOCKS_PER_ROW, 2, 2));
    diskPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
    diskPanel.setBackground(new java.awt.Color(255, 255, 255));
    blocks = new edd.ListaEnlazada();
    // Crear Bloques visuales
    for (int i = 0; i < TOTAL_BLOCKS; i++) {
        gui.BlockCard block = new gui.BlockCard(i);
        blocks.agregar(block); // Usando tu método agregar
        diskPanel.add(block);
    }
    javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(diskPanel);
    scrollPane.setPreferredSize(new java.awt.Dimension(600, 300));
    jPanel6.add(scrollPane, java.awt.BorderLayout.CENTER);
}
private void actualizarVistaDisco(Archivo archivo) {
    if (archivo == null) return;
    
    ListaEnlazada bloquesAsignados = archivo.getBloquesAsignados();
    if (bloquesAsignados == null) return;
    
    // Obtener color del archivo
    Color colorArchivo = Color.decode(archivo.getColor());
    
    // Actualizar cada bloque asignado
    for (int i = 0; i < bloquesAsignados.tamaño(); i++) {
        int numeroBloque = (int) bloquesAsignados.obtener(i);
        if (numeroBloque >= 0 && numeroBloque < TOTAL_BLOCKS) {
            BlockCard blockCard = (BlockCard) blocks.obtener(numeroBloque);
            if (blockCard != null) {
                blockCard.setOccupied(true, archivo.getNombre(), colorArchivo);
            }
        }
    }
    
    // Refrescar la vista
    diskPanel.revalidate();
    diskPanel.repaint();
}
private void liberarBloquesEnDisco(Archivo archivo) {
    if (archivo == null) return;
    
    ListaEnlazada bloquesAsignados = archivo.getBloquesAsignados();
    if (bloquesAsignados == null) return;
    
    // Liberar cada bloque asignado
    for (int i = 0; i < bloquesAsignados.tamaño(); i++) {
        int numeroBloque = (int) bloquesAsignados.obtener(i);
        if (numeroBloque >= 0 && numeroBloque < TOTAL_BLOCKS) {
            BlockCard blockCard = (BlockCard) blocks.obtener(numeroBloque);
            if (blockCard != null) {
                blockCard.setOccupied(false, "", Color.LIGHT_GRAY);
            }
        }
    }
    
    // Refrescar la vista
    diskPanel.revalidate();
    diskPanel.repaint();
}

private void actualizarTablaArchivos() {
    // Limpiar la tabla
    Tm.setRowCount(0);
    
    // Recorrer todos los archivos del sistema y agregarlos a la tabla
    ListaEnlazada todosLosArchivos = sistemaArchivos.obtenerTodosLosArchivos();
    for (int i = 0; i < todosLosArchivos.tamaño(); i++) {
        Archivo archivo = (Archivo) todosLosArchivos.obtener(i);
        
        // Obtener información del archivo
        String nombre = archivo.getNombre();
        int bloquesAsignados = archivo.getBloquesAsignados() != null ? archivo.getBloquesAsignados().tamaño() : 0;
        int primerBloque = archivo.getPrimerBloque(); // Necesitas implementar este método
        
        // Agregar fila a la tabla
        Tm.addRow(new Object[]{nombre, bloquesAsignados, primerBloque});
    }
    //aplicar rendered con colores
     jTable1.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            // Solo aplicar color a la primera columna (nombre del archivo)
            if (column == 0 && value != null) {
                try {
                    String nombreArchivo = value.toString();
                    // Buscar el archivo en la lista para obtener su color
                    ListaEnlazada archivos = sistemaArchivos.obtenerTodosLosArchivos();
                    for (int i = 0; i < archivos.tamaño(); i++) {
                        Archivo archivo = (Archivo) archivos.obtener(i);
                        if (archivo.getNombre().equals(nombreArchivo)) {
                            Color colorArchivo = Color.decode(archivo.getColor());
                            c.setForeground(colorArchivo);
                            break;
                        }
                    }
                } catch (Exception e) {
                    c.setForeground(Color.BLACK);
                }
            } else {
                c.setForeground(Color.BLACK);
            }
            
            return c;
        }
    });
     
    // Refrescar la tabla
    jTable1.revalidate();
    jTable1.repaint();
}

private void actualizarTablaCola(){
    Tcola.setRowCount(0);
    edd.Cola solicitudes = sistemaArchivos.getSolicitudesES();
    
    System.out.println("Actualizando tabla cola - Total solicitudes: " + solicitudes.size());
    for (int i = 0; i < solicitudes.size(); i++) {
        // Castear el elemento obtenido a Proceso
        Proceso proceso = (Proceso) solicitudes.get(i); 
        
        
         String idProceso = proceso.getName(); 
        String archivoSolicitado = proceso.getArchivoObjetivo();
        String bloqueActual = proceso.getBloqueActual(); 
        String estado = proceso.getEstado();
        System.out.println("Proceso en cola: " + idProceso + " - Estado: " + estado);
        Tcola.addRow(new Object[]{idProceso, archivoSolicitado, bloqueActual, estado});
    }
       jTable2.revalidate();
        jTable2.repaint();}

       private void liberarBloquesDeDirectorio(Directorio directorio) {
    if (directorio == null) return;
    
    // Liberar bloques de todos los archivos en este directorio
    ListaEnlazada elementos = directorio.getElementos();
    for (int i = 0; i < elementos.tamaño(); i++) {
        FileSystemElement elemento = (FileSystemElement) elementos.obtener(i);
        
        if (elemento instanceof Archivo) {
            // Liberar bloques del archivo
            liberarBloquesEnDisco((Archivo) elemento);
        } else if (elemento instanceof Directorio) {
            // Llamada recursiva para subdirectorios
            liberarBloquesDeDirectorio((Directorio) elemento);
        }
    }
    diskPanel.revalidate();
    diskPanel.repaint();
       }
 
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        botones = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTree1 = new javax.swing.JTree();
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        isUser = new javax.swing.JRadioButton();
        IsAdmin = new javax.swing.JRadioButton();
        CreateFile = new javax.swing.JButton();
        CreateDir = new javax.swing.JButton();
        FileDirName = new javax.swing.JTextField();
        Tamano = new javax.swing.JSpinner();
        edit = new javax.swing.JButton();
        mode = new javax.swing.JButton();
        delete = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        isFIFO = new javax.swing.JRadioButton();
        isSSTF = new javax.swing.JRadioButton();
        isSCAN = new javax.swing.JRadioButton();
        isC_SCAN = new javax.swing.JRadioButton();
        ConfirmPolitics = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(204, 204, 204));
        jPanel1.setPreferredSize(new java.awt.Dimension(230, 612));

        jTree1.setBackground(new java.awt.Color(51, 51, 51));
        jTree1.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane1.setViewportView(jTree1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.LINE_START);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jPanel3.setBackground(new java.awt.Color(204, 255, 255));
        jPanel3.setPreferredSize(new java.awt.Dimension(779, 260));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3"
            }
        ));
        jScrollPane2.setViewportView(jTable1);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 510, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        jPanel2.add(jPanel3, java.awt.BorderLayout.PAGE_END);

        jPanel4.setLayout(new java.awt.BorderLayout());

        jPanel5.setBackground(new java.awt.Color(255, 255, 204));
        jPanel5.setPreferredSize(new java.awt.Dimension(270, 372));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 51, 51));
        jLabel1.setText("CRUD");

        isUser.setForeground(new java.awt.Color(51, 51, 51));
        isUser.setText("User");
        isUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isUserActionPerformed(evt);
            }
        });

        IsAdmin.setForeground(new java.awt.Color(51, 51, 51));
        IsAdmin.setText("Admin");
        IsAdmin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IsAdminActionPerformed(evt);
            }
        });

        CreateFile.setText("Crear Archivo");
        CreateFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateFileActionPerformed(evt);
            }
        });

        CreateDir.setText("Crear Directorio");
        CreateDir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CreateDirActionPerformed(evt);
            }
        });

        FileDirName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileDirNameActionPerformed(evt);
            }
        });

        Tamano.setModel(new javax.swing.SpinnerNumberModel(1, 1, 100, 1));

        edit.setText("Editar");
        edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editActionPerformed(evt);
            }
        });

        mode.setText("Confirmar Modo");
        mode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeActionPerformed(evt);
            }
        });

        delete.setText("Eliminar");
        delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 51, 51));
        jLabel2.setText("Politica");

        isFIFO.setForeground(new java.awt.Color(0, 0, 0));
        isFIFO.setText("FIFO");

        isSSTF.setForeground(new java.awt.Color(0, 0, 0));
        isSSTF.setText("SSTF");

        isSCAN.setForeground(new java.awt.Color(0, 0, 0));
        isSCAN.setText("SCAN");

        isC_SCAN.setForeground(new java.awt.Color(0, 0, 0));
        isC_SCAN.setText("C-SCAN");

        ConfirmPolitics.setText("Confirmar");
        ConfirmPolitics.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ConfirmPoliticsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(isUser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(IsAdmin, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(isSCAN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(isC_SCAN, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(isFIFO, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(isSSTF, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel5Layout.createSequentialGroup()
                                    .addComponent(FileDirName, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(26, 26, 26)
                                    .addComponent(Tamano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(9, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(mode)
                        .addGap(71, 71, 71))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CreateDir)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(CreateFile)))
                        .addGap(73, 73, 73))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(85, 85, 85))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(delete)
                                .addComponent(edit)))
                        .addGap(94, 94, 94))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(ConfirmPolitics)
                        .addGap(91, 91, 91))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isUser)
                    .addComponent(IsAdmin))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mode, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(FileDirName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Tamano, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(CreateDir)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CreateFile)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(edit)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(delete)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isFIFO)
                    .addComponent(isSSTF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(isC_SCAN)
                    .addComponent(isSCAN))
                .addGap(18, 18, 18)
                .addComponent(ConfirmPolitics)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel5, java.awt.BorderLayout.LINE_END);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 509, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
        );

        jPanel4.add(jPanel6, java.awt.BorderLayout.CENTER);

        jPanel2.add(jPanel4, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanel2, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void isUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isUserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_isUserActionPerformed

    private void IsAdminActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IsAdminActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IsAdminActionPerformed

    private void CreateFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateFileActionPerformed
        // TODO add your handling code here:
        String nombre = FileDirName.getText().trim();
        int sizeArchivo=(int )Tamano.getValue();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para el directorio");
            return;
        }
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        Directorio directorioPadre;
        
        if (selectedNode ==null){
            directorioPadre = sistemaArchivos.getRaiz();
            selectedNode = rootNode;
        }else{
        Object userObject = selectedNode.getUserObject();
            if (userObject instanceof Directorio) {
                directorioPadre = (Directorio) userObject;
            }else{
                Archivo archivo = (Archivo) userObject;
                directorioPadre = archivo.getDirectorioPadre();
                selectedNode= (DefaultMutableTreeNode) selectedNode.getParent();
                
            }
        }
        //Crear en el sistema
        if (sistemaArchivos.crearArchivo(nombre, sizeArchivo, directorioPadre)){
            FileSystemElement nuevoElemento = directorioPadre.buscarElemento(nombre);
            DefaultMutableTreeNode nuevoNodo = new DefaultMutableTreeNode(nuevoElemento);
            treeModel.insertNodeInto(nuevoNodo, selectedNode, selectedNode.getChildCount());
            jTree1.expandPath(new TreePath(selectedNode.getPath()));
            jTree1.scrollPathToVisible(new TreePath(nuevoNodo.getPath()));
            FileDirName.setText("");
            //mesaje error maybe
            
            //actualizar vista en el disco
            actualizarVistaDisco((Archivo) nuevoElemento);
            //Actualizar Tabla
            actualizarTablaArchivos();
            //acualizar cola
            actualizarTablaCola();
        JOptionPane.showMessageDialog(this, "Archivo creado exitosamente");
        }
        
        
    }//GEN-LAST:event_CreateFileActionPerformed

    private void CreateDirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CreateDirActionPerformed
        // TODO add your handling code here:
        String nombre = FileDirName.getText().trim();
        if (nombre.isEmpty()){
            JOptionPane.showMessageDialog(this, "Ingrese un nombre para el directorio");
            return;
        }
         // Obtener el nodo seleccionado
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        Directorio directorioPadre;
   
        
        
         if (selectedNode==null) {
            directorioPadre = sistemaArchivos.getRaiz();
            selectedNode = rootNode;
        }else{
             Object userObject = selectedNode.getUserObject();
             if (userObject instanceof Directorio) {
            directorioPadre = (Directorio) userObject;
        } else {
            Archivo archivo = (Archivo) userObject;
            directorioPadre = archivo.getDirectorioPadre();
            selectedNode = (DefaultMutableTreeNode) selectedNode.getParent();
             }}
         
         //crear dir en el sistema
         if (sistemaArchivos.crearDirectorio(nombre, directorioPadre)) {
        // Crear el nodo visual para el nuevo directorio
        FileSystemElement nuevoElemento = directorioPadre.buscarElemento(nombre);
        DefaultMutableTreeNode nuevoNodo = new DefaultMutableTreeNode(nuevoElemento);

        // Insertar el nuevo nodo dentro del árbol visual
        treeModel.insertNodeInto(nuevoNodo, selectedNode, selectedNode.getChildCount());

        // Expandir para que se vea el nuevo directorio
        jTree1.expandPath(new TreePath(selectedNode.getPath()));
        jTree1.scrollPathToVisible(new TreePath(nuevoNodo.getPath()));

        actualizarTablaCola();
        JOptionPane.showMessageDialog(this, "Directorio creado exitosamente");
        FileDirName.setText("");
    } else {
        JOptionPane.showMessageDialog(this, "Error al crear el directorio (sin permisos o duplicado)");
    }

           
    }//GEN-LAST:event_CreateDirActionPerformed

    private void FileDirNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileDirNameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FileDirNameActionPerformed

    private void editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editActionPerformed
        // TODO add your handling code here:
        if (sistemaArchivos.getUsuarioActual()=="User") {
            JOptionPane.showMessageDialog(this, "No puede Editar en modo Usuario");
            return;
        }
        
        DefaultMutableTreeNode selectedNode = getSelectedNode();
        String nuevoNombre = FileDirName.getText().trim();
        if (selectedNode ==null || selectedNode.getUserObject().equals(sistemaArchivos.getRaiz())) {
            JOptionPane.showMessageDialog(this, "Seleccione un archivo o directorio (que no sea la raíz) para editar.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        
        if (nuevoNombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el nuevo nombre");
            return;
        }
        FileSystemElement elemento = (FileSystemElement) selectedNode.getUserObject();
        Directorio directorioPadre = elemento instanceof Archivo ? ((Archivo) elemento).getDirectorioPadre() : ((Directorio) elemento).getDirectorioPadre();
        
        if (directorioPadre ==null) {
            JOptionPane.showMessageDialog(this, "No se puede renombrar el elemento raíz.", "Error de Edición", JOptionPane.ERROR_MESSAGE);
             return;
        }
        // Guardar referencia al archivo ANTES de editar (si es un archivo)
    Archivo archivoAntes = null;
    if (elemento instanceof Archivo) {
        archivoAntes = (Archivo) elemento;
    }
        
        //renombrar en el sistema
        if (sistemaArchivos.EditarElemento(elemento,nuevoNombre,directorioPadre)) {
            selectedNode.setUserObject(elemento);
            treeModel.nodeChanged(selectedNode);
            //Actualizar vista del disco si es un archivo
        if (elemento instanceof Archivo) {
            Archivo archivoEditado = (Archivo) elemento;
            
            //  Limpiar los bloques del nombre anterior
            if (archivoAntes != null) {
                liberarBloquesEnDisco(archivoAntes);
            }
            actualizarVistaDisco(archivoEditado);}
        //actuaizar tabla cola
            actualizarTablaCola();
            //Actualizar Tabla
            actualizarTablaArchivos();
            FileDirName.setText("");
            ;
            
        }
    }//GEN-LAST:event_editActionPerformed

    private void modeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeActionPerformed
        // TODO add your handling code here:
        botones.add(isUser);
        botones.add(IsAdmin);
        if (isUser.isSelected()) {
            sistemaArchivos.setModoAdministrador(false);
            sistemaArchivos.setUsuarioActual("User");
            JOptionPane.showMessageDialog(this, "Actualmente esta en modo Usuario, solo lectura");
        }else if (IsAdmin.isSelected()) {
             sistemaArchivos.setModoAdministrador(true);
            sistemaArchivos.setUsuarioActual("Admin");
            JOptionPane.showMessageDialog(this, "Actualmente esta en modo Administrador");
        }else{
        JOptionPane.showMessageDialog(this, "no se selecciono ninguna opcion");
        }
        
    }//GEN-LAST:event_modeActionPerformed

    private void deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteActionPerformed
        // TODO add your handling code here:
        if (sistemaArchivos.getUsuarioActual().equals("User")) {
            JOptionPane.showMessageDialog(this, "En modo Usuario no puede eliminar Archivos o Directorios");
            return;
        }
        
         DefaultMutableTreeNode selectedNode = getSelectedNode();
         if (selectedNode == null || selectedNode.getUserObject().equals(sistemaArchivos.getRaiz())) {
             JOptionPane.showMessageDialog(this, "Seleccione un Archivo que no sea la Raiz");
             return;
         }
         FileSystemElement elemento = (FileSystemElement) selectedNode.getUserObject();
         //confirmar eliminacion'
         int confirmacion= JOptionPane.showConfirmDialog(this, "Esta seguro de que desea eliminar" + " "+ elemento.getNombre() + "?","Confirmar Eliminacion", JOptionPane.YES_NO_OPTION);
         
         if (confirmacion != JOptionPane.YES_OPTION) {
             return;
        }
        //
        Directorio directorioPadre;
        if (elemento instanceof Archivo) {
            
            directorioPadre=  ((Archivo) elemento).getDirectorioPadre();
        }else{
            directorioPadre = ((Directorio) elemento).getDirectorioPadre();
        }
        //verificar que el dir padre no sea nulo
        if (directorioPadre == null) {
        JOptionPane.showMessageDialog(this, 
            "No se puede eliminar el elemento raíz.", 
            "Error de Eliminación", 
            JOptionPane.ERROR_MESSAGE);
        return;
    }
        //remover en el sistema
        boolean eliminado = false;
        if (elemento instanceof Archivo) {
        liberarBloquesEnDisco((Archivo) elemento);
        eliminado = sistemaArchivos.eliminarArchivo(elemento.getNombre(), directorioPadre);
    } else if (elemento instanceof Directorio) {
        liberarBloquesDeDirectorio((Directorio) elemento);
        eliminado = sistemaArchivos.eliminarDirectorio(elemento.getNombre(), directorioPadre);
    }
        //remover visualmente
        if (eliminado) {
            treeModel.removeNodeFromParent(selectedNode);
            actualizarTablaArchivos();
            actualizarTablaCola();
            JOptionPane.showMessageDialog(this, "Eliminado exitosamente");
        }else{
            JOptionPane.showMessageDialog(this, "Error al eliminar elemento");
        }
        
    }//GEN-LAST:event_deleteActionPerformed

    private void ConfirmPoliticsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ConfirmPoliticsActionPerformed
        // TODO add your handling code here:
        buttonGroup1.add(isFIFO);
        buttonGroup1.add(isSSTF);
        buttonGroup1.add(isSCAN);
        buttonGroup1.add(isC_SCAN);
       
        String politicaSeleccionada = "";

    if (isFIFO.isSelected()) {
        politicaSeleccionada = "FIFO";
    } else if (isSSTF.isSelected()) {
        politicaSeleccionada = "SSTF";
    } else if (isSCAN.isSelected()) {
        politicaSeleccionada = "SCAN";
    } else if (isC_SCAN.isSelected()) {
        politicaSeleccionada = "C-SCAN";
    }
    
    if (politicaSeleccionada.isEmpty()) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Seleccione una política de planificación.", 
            "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
        return;
    }
    //cambiar en el sistema
    sistemaArchivos.getPlanificador().setPolitica(politicaSeleccionada);

    javax.swing.JOptionPane.showMessageDialog(this, 
        "Política de planificación cambiada a: " + politicaSeleccionada,
        "Confirmación", javax.swing.JOptionPane.INFORMATION_MESSAGE);

    System.out.println(" Política de disco cambiada a: " + politicaSeleccionada);
    
    
    }//GEN-LAST:event_ConfirmPoliticsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainWindow().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton ConfirmPolitics;
    private javax.swing.JButton CreateDir;
    private javax.swing.JButton CreateFile;
    private javax.swing.JTextField FileDirName;
    private javax.swing.JRadioButton IsAdmin;
    private javax.swing.JSpinner Tamano;
    private javax.swing.ButtonGroup botones;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton delete;
    private javax.swing.JButton edit;
    private javax.swing.JRadioButton isC_SCAN;
    private javax.swing.JRadioButton isFIFO;
    private javax.swing.JRadioButton isSCAN;
    private javax.swing.JRadioButton isSSTF;
    private javax.swing.JRadioButton isUser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTree jTree1;
    private javax.swing.JButton mode;
    // End of variables declaration//GEN-END:variables
}
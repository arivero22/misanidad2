/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package misanidad2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author alvar
 */
public class VistaMedico extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaMedico.class.getName());

    private Sistema sistema;
    private Medico medico;
    /**
     * Creates new form VistaMedico
     */
    public VistaMedico(Sistema sistema, Medico medico) {
        this.sistema = sistema;
        this.medico = medico;
        initComponents();
        cargarTablaCitas(medico);
        cargarSelectorPacientes(medico);
    }
    
    private void mostrarImagenPrueba(PruebaImagen prueba){
        if(prueba == null || !prueba.tieneImagen()){
            JOptionPane.showMessageDialog(this, "Esta prueba no tiene imagen", "Sin imagen", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFrame ventanaImagen = new JFrame(prueba.getNombre());
        ventanaImagen.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        ventanaImagen.setSize(800, 600);
        ventanaImagen.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());

        ImageIcon icono = null;

        if(prueba.getImagenBytes() != null){
            icono = new ImageIcon(prueba.getImagenBytes());
        } else if(prueba.getRutaImagen() != null){
            icono = new ImageIcon(prueba.getRutaImagen());
        }
        
        Image imagenEscalada = icono.getImage().getScaledInstance(750, 500, Image.SCALE_SMOOTH);
        JLabel labelImagen = new JLabel(new ImageIcon(imagenEscalada));
        labelImagen.setHorizontalAlignment(JLabel.CENTER);

        JScrollPane scrollImagen = new JScrollPane(labelImagen);
        panel.add(scrollImagen, BorderLayout.CENTER);            
        

        ventanaImagen.add(panel);
        ventanaImagen.setVisible(true);
    }

    
    private void cargarSelectorPacientes(Medico medico){
        List<Paciente> pacientes = sistema.getPacientesParaMedico(medico);
        for(Paciente p : pacientes) {
            SeleccionPaciente.addItem(p);
        }
    }
    
    private Medicamento obtenerMedicamentoSeleccionado(){
        int indice = TablaMedicamentos1.getSelectedRow();
        
        if(indice == -1) return null;
        
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        
        int fila = TablaMedicamentos1.convertRowIndexToModel(indice);
        List<Medicamento> medicamentos = paciente.getMedicamentos();
        
        return medicamentos.get(fila);
    }
    
    private Cita getCitaSeleccionada(){
        int filaSeleccionada = TablaCitas.getSelectedRow();

        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Selecciona una cita", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int fila_modelo = TablaCitas.convertRowIndexToModel(filaSeleccionada);
        List<Cita> citas_medico = sistema.getCitasParaMedico(medico);

        if(filaSeleccionada >= citas_medico.size()){
            return null;
        }

        return citas_medico.get(filaSeleccionada);
    }
    
    private void cargarTablaMedicamentos(Paciente p){
        String[] columnas = {"Nombre", "Dosis", "Fecha de fin", "Fecha de receta"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Medicamento m : p.getMedicamentos()){
            Object[] fila = {
                m.getNombre(),
                m.getDosis() + "/" + m.getFrecuencia(),
                m.getFechaFin(),
                m.getFechaReceta()
            };
            modelo.addRow(fila);
        }

        TablaMedicamentos1.setModel(modelo);
        TablaMedicamentos1.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaVacunas(Paciente p){
        String[] columnas = {"Nombre", "Dosis", "Fecha de administracion", "Fecha de siguiente dosis"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Vacuna v : p.getVacunas()){
            Object[] fila = {
                v.getNombre(),
                v.getDosis(),
                v.getFechaAdministracion(),
                v.getFechaSiguienteDosis()
            };
            modelo.addRow(fila);
        }

        TablaVacunas.setModel(modelo);
        TablaVacunas.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaConsultas(Paciente p){
        String[] columnas = {"Fecha", "Motivo", "Médico", "Recomendaciones"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Consulta c : p.getHistorialClinico()){
            Object[] fila = {
                c.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                c.getMotivo(),
                c.getMedico().getNombre(),
                c.getRecomendaciones()
            };
            modelo.addRow(fila);
        }

        TablaConsultas1.setModel(modelo);
        TablaConsultas1.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaPruebas(Paciente p){
        String[] columnas = {"Fecha", "Prueba", "Centro", "Informe"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(PruebaLaboratorio prue : p.getPruebas()){
            Object[] fila = {
                prue.getFecha(),
                prue.getNombre(),
                prue.getCentro(),
                prue.getInforme()
            };
            modelo.addRow(fila);
        }

        TablaPruebas.setModel(modelo);
        TablaPruebas.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaCitas(Medico m){
        String[] columnas = {"Paciente", "Motivo", "Fecha", "Hora", "Centro", "Estado", "Tipo"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Cita c : sistema.getCitasParaMedico(m)){
            Object[] fila = {
                c.getPaciente().getNombre(),
                c.getMotivo(),
                c.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                c.getFechaHora().format(DateTimeFormatter.ofPattern("HH:mm")),
                c.getCentro(),
                c.getEstado().getNombre(),
                c.isTelefonica() ? "Telefónica" : "Presencial"
            };
            modelo.addRow(fila);
        }

        TablaCitas.setModel(modelo);
        TablaCitas.setAutoCreateRowSorter(true);
    }
    
    private void reagendarDia(){
        String fecha_str = JOptionPane.showInputDialog(this, "Introduce la fecha de las citas a reagendar(dd/mm/yyyy):",LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        
        if(fecha_str == null) return;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate fecha = LocalDate.parse(fecha_str, formatter);
        
        List<Cita> citas_dia = medico.getAgenda().getCitasDelDia(fecha);
        
        if(citas_dia.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay citas este día.", "Sin citas", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int reagendadas = medico.getAgenda().reagendarDia(fecha);
        
        if(reagendadas > 0){
            cargarTablaCitas(medico);
            GestorArchivos.guardarSistema(sistema);
            for(Cita c : citas_dia){
                c.getPaciente().agregarNotificacion(new Notificacion("Su cita del dia "+fecha_str+" con "+medico+" ha sido reagendada."));
            }
            JOptionPane.showMessageDialog(this, "Las citas han sido reagendadas correctamente al siguiente dia disponible.", "Citas reagendadas", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private Consulta getConsultaSeleccionada(){
        int filaSeleccionada = TablaConsultas1.getSelectedRow();
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();

        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Selecciona una consulta", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<Consulta> consultas_paciente = paciente.getHistorialClinico();

        if(filaSeleccionada >= consultas_paciente.size()){
            return null;
        }

        return consultas_paciente.get(filaSeleccionada);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaCitas = new javax.swing.JTable();
        BotonReagendar = new javax.swing.JButton();
        BotonPrescripcion = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        SeleccionPaciente = new javax.swing.JComboBox<>();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TablaMedicamentos1 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaConsultas1 = new javax.swing.JTable();
        jButton9 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        TablaPruebas = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaVacunas = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TablaCitas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(TablaCitas);

        BotonReagendar.setText("Reagendar día");
        BotonReagendar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonReagendarActionPerformed(evt);
            }
        });

        BotonPrescripcion.setText("Crear prescripción");
        BotonPrescripcion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonPrescripcionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(BotonReagendar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonPrescripcion)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 377, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonReagendar)
                    .addComponent(BotonPrescripcion))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Citas", jPanel1);

        SeleccionPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SeleccionPacienteActionPerformed(evt);
            }
        });

        TablaMedicamentos1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Nombre", "Dosis", "Fecha de fin", "Fecha de receta"
            }
        ));
        jScrollPane6.setViewportView(TablaMedicamentos1);

        jButton3.setText("Borrar");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Recetar medicamento");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton3))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Medicaciones", jPanel5);

        TablaConsultas1.setModel(new javax.swing.table.DefaultTableModel(
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
        TablaConsultas1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TablaConsultas1MousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(TablaConsultas1);

        jButton9.setText("Borrar consulta");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton9)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Consultas", jPanel6);

        TablaPruebas.setModel(new javax.swing.table.DefaultTableModel(
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
        TablaPruebas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TablaPruebasMousePressed(evt);
            }
        });
        jScrollPane4.setViewportView(TablaPruebas);

        jButton2.setText("Añadir prueba de laboratorio");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setText("Ver imagen");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Añadir prueba de imagen");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("Borrar prueba");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton5)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton5)
                    .addComponent(jButton6)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        jTabbedPane2.addTab("Pruebas", jPanel7);

        TablaVacunas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(TablaVacunas);

        jButton1.setText("Añadir vacuna");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 640, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 308, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Vacunas", jPanel8);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SeleccionPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jTabbedPane2)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SeleccionPaciente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane2))
        );

        jTabbedPane1.addTab("Historiales", jPanel4);

        jMenu1.setText("Cerrar sesión");
        jMenu1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jMenu1MouseClicked(evt);
            }
        });
        jMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenu1ActionPerformed(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        this.dispose();
        new VistaInicioSesion(sistema).setVisible(true);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        if(paciente == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre del medicamento:", "Crear receta", JOptionPane.QUESTION_MESSAGE);
        if(nombre == null) {
            return;
        }

        String dosis = JOptionPane.showInputDialog(this, "Dosis:", "Crear receta", JOptionPane.QUESTION_MESSAGE);
        String frecuencia = JOptionPane.showInputDialog(this, "Frecuencia:", "Crear receta", JOptionPane.QUESTION_MESSAGE);

        int opcion = JOptionPane.showConfirmDialog(this, "¿Crónico?", "Crear receta", JOptionPane.YES_NO_OPTION);

        Medicamento nuevo;

        if(opcion == JOptionPane.YES_OPTION){
            nuevo = new Medicamento(nombre, dosis, frecuencia);
        }else {
            String fecha_str = JOptionPane.showInputDialog(this, "Fecha de fin del tratamiento (dd/mm/yyyy):", "Crear receta", JOptionPane.QUESTION_MESSAGE);
            if(fecha_str == null) return;
            LocalDate fecha_fin;

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyy");
            fecha_fin = LocalDate.parse(fecha_str, formatter);

            nuevo = new Medicamento(nombre, dosis, frecuencia, fecha_fin);
        }

        paciente.agregarMedicamento(nuevo);
        paciente.agregarNotificacion(new Notificacion("Se te ha recetado "+nuevo.getNombre()+"."));
        cargarTablaMedicamentos(paciente);
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        Medicamento medicamento = obtenerMedicamentoSeleccionado();
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar receta para "+medicamento.getNombre()+ " del paciente "+paciente.getNombre()+"?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            paciente.borrarMedicamento(medicamento);
            cargarTablaMedicamentos(paciente);
        }
        
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void SeleccionPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SeleccionPacienteActionPerformed
        // TODO add your handling code here:
        //AQUI!!!
        Paciente p = (Paciente) SeleccionPaciente.getSelectedItem();
        cargarTablaMedicamentos(p);
        cargarTablaConsultas(p);
        cargarTablaVacunas(p);
        cargarTablaPruebas(p);
    }//GEN-LAST:event_SeleccionPacienteActionPerformed

    private void BotonPrescripcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonPrescripcionActionPerformed
        Cita cita = getCitaSeleccionada();
        if(cita != null){
            JTextArea textArea = new JTextArea(8, 40);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            JScrollPane scrollPane = new JScrollPane(textArea);

            int opcion = JOptionPane.showConfirmDialog(this, scrollPane, "Prescripcion:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (opcion == JOptionPane.OK_OPTION){
                String prescripcion = textArea.getText();
                Consulta nueva = new Consulta(cita, prescripcion);
                cita.getPaciente().agregarConsulta(nueva);
                cita.setEstado(EstadoCita.COMPLETADA);
                cargarTablaConsultas((Paciente) SeleccionPaciente.getSelectedItem());
                cargarTablaCitas(medico);
            }
        }
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_BotonPrescripcionActionPerformed

    private void BotonReagendarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonReagendarActionPerformed
        // TODO add your handling code here:
        reagendarDia();
    }//GEN-LAST:event_BotonReagendarActionPerformed

    private void TablaConsultas1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaConsultas1MousePressed
        Consulta consulta = getConsultaSeleccionada();
        if (consulta != null) {
            String recomendaciones = consulta.getRecomendaciones();
            if (recomendaciones.length() > 100) {
                JTextArea textArea = new JTextArea(recomendaciones, 10, 40);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Recomendaciones medicas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, recomendaciones, "Recomendaciones medicas", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_TablaConsultas1MousePressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        if(paciente == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la vacuna:", "Añadir vacuna", JOptionPane.QUESTION_MESSAGE);
        if(nombre == null){
            return;
        }

        String dosis = JOptionPane.showInputDialog(this, "Dosis:", "Añadir vacuna", JOptionPane.QUESTION_MESSAGE);

        String fecha_str = JOptionPane.showInputDialog(this, "Fecha de administración (dd/mm/yyyy):", "Añadir vacuna", JOptionPane.QUESTION_MESSAGE);
        if(fecha_str == null) return;
        LocalDate fecha_admin = LocalDate.parse(fecha_str, DateTimeFormatter.ofPattern("dd/MM/yyy"));
        
        int opcion = JOptionPane.showConfirmDialog(this, "¿Hay siguiente dosis?", "Añadir vacuna", JOptionPane.YES_NO_OPTION);

        Vacuna nuevo;

        if(opcion == JOptionPane.YES_OPTION){
            String fecha_str2 = JOptionPane.showInputDialog(this, "Fecha de la siguiente dosis (dd/mm/yyyy):", "Añadir vacuna", JOptionPane.QUESTION_MESSAGE);
            if(fecha_str2 == null) return;
            LocalDate fecha_sig_dosis = LocalDate.parse(fecha_str, DateTimeFormatter.ofPattern("dd/MM/yyy"));
            nuevo = new Vacuna(nombre, dosis, fecha_admin, fecha_sig_dosis);
        } else{
            nuevo = new Vacuna(nombre, dosis, fecha_admin);
        }

        paciente.agregarVacuna(nuevo);
        cargarTablaVacunas(paciente);
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_jButton1ActionPerformed

    private PruebaLaboratorio getPruebaSeleccionada(){
        int filaSeleccionada = TablaPruebas.getSelectedRow();
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();

        if (filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Selecciona una prueba", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<PruebaLaboratorio> pruebas_paciente = paciente.getPruebas();

        if (filaSeleccionada >= pruebas_paciente.size()){
            return null;
        }

        return pruebas_paciente.get(filaSeleccionada);
    }
    
    private void TablaPruebasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaPruebasMousePressed
        PruebaLaboratorio prueba = getPruebaSeleccionada();
        if (prueba != null) {
            String recomendaciones = prueba.getInforme();
            if (recomendaciones.length() > 100) {
                JTextArea textArea = new JTextArea(recomendaciones, 10, 40);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                textArea.setEditable(false);
                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane, "Informe medico", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, recomendaciones, "Informe medico", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_TablaPruebasMousePressed

    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        if(paciente == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la prueba:", "Añadir prueba", JOptionPane.QUESTION_MESSAGE);
        if(nombre == null){
            return;
        }
        
        String centro = JOptionPane.showInputDialog(this, "Centro:", "Añadir prueba", JOptionPane.QUESTION_MESSAGE);
        
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, "Informe:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (opcion == JOptionPane.OK_OPTION){
            String informe = textArea.getText();
            PruebaLaboratorio nuevo = new PruebaLaboratorio(nombre, LocalDate.now(), centro, informe);
            paciente.agregarPrueba(nuevo);
            cargarTablaPruebas(paciente);
        }
        
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        if(getPruebaSeleccionada().getClass() == PruebaImagen.class){
            mostrarImagenPrueba((PruebaImagen) getPruebaSeleccionada());
        }
        
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        Paciente paciente = (Paciente) SeleccionPaciente.getSelectedItem();
        if (paciente == null) return;

        String nombre = JOptionPane.showInputDialog(this, "Nombre de la prueba:", "Añadir prueba de imagen", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null) return;

        String centro = JOptionPane.showInputDialog(this, "Centro:", "Añadir prueba de imagen", JOptionPane.QUESTION_MESSAGE);
        if (centro == null) return;

        JTextArea textArea = new JTextArea(8, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        int opcion = JOptionPane.showConfirmDialog(this, scrollPane, "Informe médico:", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) return;

        String informe = textArea.getText();

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar imagen");

        int resultado = fileChooser.showOpenDialog(this);
        PruebaImagen nueva;

        if (resultado == JFileChooser.APPROVE_OPTION){
            File archivo = fileChooser.getSelectedFile();
            try{
                byte[] imagenBytes = java.nio.file.Files.readAllBytes(archivo.toPath());
                nueva = new PruebaImagen(nombre, LocalDate.now(), centro, informe, imagenBytes);
            } catch (Exception e){
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } else {
            nueva = new PruebaImagen(nombre, LocalDate.now(), centro, informe);
        }

        paciente.agregarPrueba(nueva);
        cargarTablaPruebas(paciente);
        GestorArchivos.guardarSistema(sistema);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        Paciente p = (Paciente) SeleccionPaciente.getSelectedItem();
        PruebaLaboratorio prueba = getPruebaSeleccionada();
                
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar "+prueba.getNombre()+"?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            p.borrarPrueba(prueba);
            cargarTablaPruebas(p);
            GestorArchivos.guardarSistema(sistema);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        Consulta c =getConsultaSeleccionada();
        Paciente p = (Paciente) SeleccionPaciente.getSelectedItem();
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar esta consulta?", "Confirmar", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            p.borrarConsulta(c);
            cargarTablaConsultas(p);
            GestorArchivos.guardarSistema(sistema);
        }
    }//GEN-LAST:event_jButton9ActionPerformed

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
       // java.awt.EventQueue.invokeLater(() -> new VistaMedico().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonPrescripcion;
    private javax.swing.JButton BotonReagendar;
    private javax.swing.JComboBox<Paciente> SeleccionPaciente;
    private javax.swing.JTable TablaCitas;
    private javax.swing.JTable TablaConsultas1;
    private javax.swing.JTable TablaMedicamentos1;
    private javax.swing.JTable TablaPruebas;
    private javax.swing.JTable TablaVacunas;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    // End of variables declaration//GEN-END:variables
}

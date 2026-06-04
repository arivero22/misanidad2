/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package misanidad2;

import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author alvar
 */
public class VistaAdministrador extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaAdministrador.class.getName());

    private Sistema sistema;
    
    /**
     * Creates new form VistaAdministrador
     */
    public VistaAdministrador(Sistema sistema) {
        this.sistema = sistema;
        initComponents();
        cargarTablaPacientes(sistema);
        cargarTablaEmpleados(sistema);
        cargarTablaCitas(sistema);
    }
    
    public void cargarTablaCitas(Sistema sistema){
        String[] columnas = {"Paciente", "Médico", "Estado", "Fecha", "Hora", "Motivo", "Fecha solicitud", "Centro", "Telefonica", "Motivo cancelación", "Fecha cancelación"};
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Cita c : sistema.getTodasCitas()){
            Object[] fila = {
                c.getPaciente().getNombre(),
                c.getMedico().getNombre(),
                c.getEstado(),
                c.getFechaHora().toLocalDate(),
                c.getFechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                c.getMotivo(),
                c.getFechaSolicitud().toLocalDate()+" "+c.getFechaSolicitud().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                c.getCentro(),
                c.isTelefonica() ? "Si" : "No",
                c.getEstado()==EstadoCita.CANCELADA ? c.getMotivoCancelacion() : "---",
                c.getEstado()==EstadoCita.CANCELADA ? c.getFechaCancelacion().toLocalDate()+" "+c.getFechaCancelacion().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "---"
            };
            modelo.addRow(fila);
            
            TablaCitas.setModel(modelo);
            TablaCitas.setAutoCreateRowSorter(true);
        }
    }
    
    public void cargarTablaPacientes(Sistema sistema){
        String[] columnas = {"Nombre", "DNI", "CIPA", "Teléfono", "Dirección"};
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Paciente p : sistema.getTodosPacientes()){
            Object[] fila = {p.getNombre(), p.getDni(), p.getCipa(), p.getTelefono(), p.getDireccion()};
            modelo.addRow(fila);
            
            TablaUsuarios.setModel(modelo);
            TablaUsuarios.setAutoCreateRowSorter(true);
        }
    }
    
    public void cargarTablaEmpleados(Sistema sistema){
        String[] columnas = {"Nombre", "DNI", "Tipo", "Especialidad"};
        
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Usuario e : sistema.getTodosEmpleados()){
            Object[] fila = {e.getNombre(), e.getDni(), e.getTipo(), "---"};
            
            if(e.isMedico()){
                Medico m = (Medico) e;
                fila[3] = m.getEspecialidad();
            }
            
            modelo.addRow(fila);
            
            TablaEmpleados.setModel(modelo);
            TablaEmpleados.setAutoCreateRowSorter(true);
        }
    }

    
    private void crearNuevoUsuario(){
        //tipo
        String[] tipos = {"Paciente", "Medico", "Administrador App", "Administrador C. Salud"};
        String tipoSeleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione el tipo de usuario:","Nuevo Usuario", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);
        if (tipoSeleccionado == null) return;

        //nombre
        String nombre = JOptionPane.showInputDialog(this, "Nombre completo:", "Nombre", JOptionPane.QUESTION_MESSAGE);
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //dni
        String dni = JOptionPane.showInputDialog(this, "DNI:", "DNI", JOptionPane.QUESTION_MESSAGE);
        if (dni == null || dni.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "El DNI no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (sistema.existeUsuario(dni)){
            JOptionPane.showMessageDialog(this, "Ya existe un usuario con ese DNI", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //crear
        if (tipoSeleccionado.equals("Paciente")) {
            String telefono = JOptionPane.showInputDialog(this, "Teléfono:", "Teléfono", JOptionPane.QUESTION_MESSAGE);
            if (telefono == null || telefono.trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "El telefono no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String direccion = JOptionPane.showInputDialog(this, "Dirección:", "Dirección", JOptionPane.QUESTION_MESSAGE);
            if (direccion == null || direccion.trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "La direccion no puede estar vacia", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String cipa = JOptionPane.showInputDialog(this, "CIPA (Código de Identificación del Paciente):", "CIPA", JOptionPane.QUESTION_MESSAGE);
            if (cipa == null || cipa.trim().isEmpty()){
                JOptionPane.showMessageDialog(this, "El CIPA no puede estar vacio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (sistema.existeUsuario(cipa)){
                JOptionPane.showMessageDialog(this, "Ya existe un paciente con ese CIPA", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                Paciente nuevoPaciente = new Paciente(nombre, dni, cipa, telefono, direccion);
                sistema.registrarUsuario(nuevoPaciente);

                JOptionPane.showMessageDialog(this, 
                    "Paciente creado correctamente:\n" +
                    "Nombre: " + nombre + "\n" +
                    "DNI: " + dni + "\n" +
                    "CIPA: " + cipa + "\n" +
                    "Teléfono: " + telefono + "\n" +
                    "Dirección: " + direccion,
                    "Paciente Creado", 
                    JOptionPane.INFORMATION_MESSAGE);

                cargarTablaPacientes(sistema);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear paciente: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (tipoSeleccionado.equals("Medico")){
            Especialidad[] especialidades = Especialidad.values();

            Especialidad especialidad = (Especialidad) JOptionPane.showInputDialog(this, 
                    "Seleccione la especialidad del médico:", 
                    "Especialidad", 
                    JOptionPane.QUESTION_MESSAGE, 
                    null, 
                    especialidades, 
                    especialidades[0]);

            if (especialidad == null) return;
            try {
                Medico nuevo = new Medico(nombre, dni, especialidad, sistema);
                sistema.registrarUsuario(nuevo);

                String mensaje = "Medico creado correctamente:\n" +
                    "Nombre: " + nombre + "\n" +
                    "DNI: " + dni + "\n" +
                    "Especialidad: " + especialidad;

                JOptionPane.showMessageDialog(this, mensaje, "Medico Creado", JOptionPane.INFORMATION_MESSAGE);

                cargarTablaEmpleados(sistema);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear medico: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (tipoSeleccionado.equals("Administrador App")){
            try {
                Administrador nuevo = new Administrador(nombre, dni);
                sistema.registrarUsuario(nuevo);

                String mensaje = "Administrador de la App creado correctamente:\n" +
                    "Nombre: " + nombre + "\n" +
                    "DNI: " + dni + "\n";

                JOptionPane.showMessageDialog(this, mensaje, "Administrador Creado", JOptionPane.INFORMATION_MESSAGE);

                cargarTablaEmpleados(sistema);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear admin: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (tipoSeleccionado.equals("Administrador C. Salud")){
            try {
                PersonalAdministracion nuevo = new PersonalAdministracion(nombre, dni);
                sistema.registrarUsuario(nuevo);

                String mensaje = "Personal de administracion creado correctamente:\n" +
                    "Nombre: " + nombre + "\n" +
                    "DNI: " + dni + "\n";

                JOptionPane.showMessageDialog(this, mensaje, "P. Administracion Creado", JOptionPane.INFORMATION_MESSAGE);

                cargarTablaEmpleados(sistema);

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al crear p. administracion: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private Cita getCitaSeleccionada(){
        int fila = TablaCitas.getSelectedRow(); 
        if(fila == -1){
            JOptionPane.showMessageDialog(this,"Selecione una cita", "Error", JOptionPane.WARNING_MESSAGE);
        }
        
        int filaSel = TablaCitas.convertRowIndexToModel(fila); 
        List<Cita> lista = sistema.getTodasCitas(); 
        if (filaSel >= lista.size()) return null; 
        
        return lista.get(filaSel); 
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jDialog1 = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaUsuarios = new javax.swing.JTable();
        BotonBorrarPaciente = new javax.swing.JButton();
        BotonEditarPaciente = new javax.swing.JButton();
        BotonNuevoPaciente = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaEmpleados = new javax.swing.JTable();
        BotonBorrarEmpleado = new javax.swing.JButton();
        BotonEditarEmpleado = new javax.swing.JButton();
        BotonNuevoEmpleado = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaCitas = new javax.swing.JTable();
        BotonBorrarCita = new javax.swing.JButton();
        BotonEditarCita = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        jLabel1.setText("jLabel1");

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TablaUsuarios.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(TablaUsuarios);

        BotonBorrarPaciente.setText("Borrar");
        BotonBorrarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBorrarPacienteActionPerformed(evt);
            }
        });

        BotonEditarPaciente.setText("Editar");
        BotonEditarPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEditarPacienteActionPerformed(evt);
            }
        });

        BotonNuevoPaciente.setText("Nuevo");
        BotonNuevoPaciente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonNuevoPacienteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(BotonBorrarPaciente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonEditarPaciente)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                        .addComponent(BotonNuevoPaciente)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonBorrarPaciente)
                    .addComponent(BotonEditarPaciente)
                    .addComponent(BotonNuevoPaciente))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pacientes", jPanel1);

        TablaEmpleados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(TablaEmpleados);

        BotonBorrarEmpleado.setText("Borrar");
        BotonBorrarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonBorrarEmpleadoActionPerformed(evt);
            }
        });

        BotonEditarEmpleado.setText("Editar");
        BotonEditarEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEditarEmpleadoActionPerformed(evt);
            }
        });

        BotonNuevoEmpleado.setText("Nuevo");
        BotonNuevoEmpleado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonNuevoEmpleadoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(BotonBorrarEmpleado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonEditarEmpleado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 166, Short.MAX_VALUE)
                        .addComponent(BotonNuevoEmpleado)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonBorrarEmpleado)
                    .addComponent(BotonEditarEmpleado)
                    .addComponent(BotonNuevoEmpleado))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Empleados", jPanel2);

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

        BotonBorrarCita.setText("Borrar");

        BotonEditarCita.setText("Editar");
        BotonEditarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonEditarCitaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(BotonBorrarCita)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonEditarCita)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 291, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonBorrarCita)
                    .addComponent(BotonEditarCita))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Citas", jPanel3);

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
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenu1ActionPerformed
        //
    }//GEN-LAST:event_jMenu1ActionPerformed

    private void jMenu1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jMenu1MouseClicked
        this.dispose();
        new VistaInicioSesion(sistema).setVisible(true);
    }//GEN-LAST:event_jMenu1MouseClicked

    private void BotonNuevoPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonNuevoPacienteActionPerformed
        // TODO add your handling code here:
        crearNuevoUsuario();
    }//GEN-LAST:event_BotonNuevoPacienteActionPerformed

    private void BotonNuevoEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonNuevoEmpleadoActionPerformed
        // TODO add your handling code here:
        crearNuevoUsuario();
    }//GEN-LAST:event_BotonNuevoEmpleadoActionPerformed

    private void BotonBorrarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBorrarPacienteActionPerformed
        // TODO add your handling code here:
        int filaVista = TablaUsuarios.getSelectedRow();
        
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un paciente", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = TablaUsuarios.convertRowIndexToModel(filaVista);
        Usuario usr = sistema.getTodosPacientes().get(filaModelo);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este paciente? (" + usr.getNombre() +")", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.eliminarUsuario(usr);
            cargarTablaPacientes(sistema);
        }
    }//GEN-LAST:event_BotonBorrarPacienteActionPerformed

    private void BotonBorrarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonBorrarEmpleadoActionPerformed
        int filaVista = TablaEmpleados.getSelectedRow();
        
        if (filaVista == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int filaModelo = TablaEmpleados.convertRowIndexToModel(filaVista);
        Usuario usr = sistema.getTodosEmpleados().get(filaModelo);

        int confirmacion = JOptionPane.showConfirmDialog(this, "¿Está seguro de que desea eliminar este usuario? (" + usr.getNombre() +")", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            sistema.eliminarUsuario(usr);
            cargarTablaEmpleados(sistema);
        }
    }//GEN-LAST:event_BotonBorrarEmpleadoActionPerformed

    private void BotonEditarPacienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEditarPacienteActionPerformed
        // TODO add your handling code here:
        new VistaEditarPaciente(sistema, getPacienteSeleccionado(),this).setVisible(true);
    }//GEN-LAST:event_BotonEditarPacienteActionPerformed

    private Paciente getPacienteSeleccionado(){
        int filaVista = TablaUsuarios.getSelectedRow();
        
        if (filaVista == -1){
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        int filaModelo = TablaUsuarios.convertRowIndexToModel(filaVista);
        return sistema.getTodosPacientes().get(filaModelo);
    }
    
    private Usuario getEmpleadoSeleccionado(){
        int filaVista = TablaEmpleados.getSelectedRow();
        
        if (filaVista == -1){
            JOptionPane.showMessageDialog(this, "Seleccione un usuario", "Aviso", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        
        int filaModelo = TablaEmpleados.convertRowIndexToModel(filaVista);
        return sistema.getTodosEmpleados().get(filaModelo);
    }
    
    private void BotonEditarEmpleadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEditarEmpleadoActionPerformed
        // TODO add your handling code here:
        new VistaEditarEmpleado(this, sistema, getEmpleadoSeleccionado()).setVisible(true);
    }//GEN-LAST:event_BotonEditarEmpleadoActionPerformed

    private void BotonEditarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonEditarCitaActionPerformed
        // TODO add your handling code here:
        Cita cita = getCitaSeleccionada(); 
        if(cita == null) return; 
        
        if(cita.getEstado() == EstadoCita.COMPLETADA){
            JOptionPane.showMessageDialog(this, "No se puede editar una cita ya completada.", "Error", JOptionPane.ERROR_MESSAGE);
            return; 
        }
        
        if(cita.getEstado() == EstadoCita.CANCELADA){
            JOptionPane.showMessageDialog(this, "No se puede editar una cita cancelada.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        VistaEditarCita ventana = new VistaEditarCita(sistema, cita); 
        ventana.setVisible(true); 
        
    }//GEN-LAST:event_BotonEditarCitaActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonBorrarCita;
    private javax.swing.JButton BotonBorrarEmpleado;
    private javax.swing.JButton BotonBorrarPaciente;
    private javax.swing.JButton BotonEditarCita;
    private javax.swing.JButton BotonEditarEmpleado;
    private javax.swing.JButton BotonEditarPaciente;
    private javax.swing.JButton BotonNuevoEmpleado;
    private javax.swing.JButton BotonNuevoPaciente;
    private javax.swing.JTable TablaCitas;
    private javax.swing.JTable TablaEmpleados;
    private javax.swing.JTable TablaUsuarios;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

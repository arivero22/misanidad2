/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package misanidad2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author alvar
 */
public class VistaPaciente extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(VistaPaciente.class.getName());

    private Sistema sistema;
    private Paciente paciente;
    
    /**
     * Creates new form VistaPaciente
     */
    public VistaPaciente(Sistema sistema, Paciente paciente){
        this.sistema = sistema;
        this.paciente = paciente;
        initComponents();
        LabelNombre.setText("Hola, " + paciente.getNombre());
        LabelTelefono.setText("Teléfono: " + paciente.getTelefono());
        LabelCIPA.setText("CIPA: " + paciente.getCipa());
        LabelDireccion.setText("Dirección: " + paciente.getDireccion());
        LabelDNI.setText("DNI: " + paciente.getDni());
        cargarTablaCitas(paciente);
    }
    
    private List<Especialidad> obtenerEspecialidadesDisponibles(){
        List<Especialidad> especialidades = new ArrayList<>();

        // Añadir todas las especialidades del enum
        for (Especialidad e : Especialidad.values()) {
            especialidades.add(e);
        }

        return especialidades;
    }

    
    private void solicitarCita(){
        //obtener especialidades disponibles
        List<Especialidad> especialidades = obtenerEspecialidadesDisponibles();

        //seleccionar especialidad
        Especialidad especialidad = (Especialidad) JOptionPane.showInputDialog(this, "Selecciona especialidad:", "Nueva cita", JOptionPane.QUESTION_MESSAGE, null, especialidades.toArray(), especialidades.get(0));
        if (especialidad == null) return;

        //pedir centro
        String[] centros = {"Centro de Salud Norte", "Centro de Salud Sur", "Hospital Central", "Hospital Universitario", "Centro de Especialidades"};

        String centro = (String) JOptionPane.showInputDialog(this,"Selecciona centro:", "Centro médico", JOptionPane.QUESTION_MESSAGE, null, centros, centros[0]);
        if (centro == null) return;

        //fecha
        String fechaStr = JOptionPane.showInputDialog(this, "Fecha (dd/MM/yyyy):", LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        if (fechaStr == null) return;

        LocalDate fecha;
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            fecha = LocalDate.parse(fechaStr, formatter);

            if (fecha.isBefore(LocalDate.now())){
                JOptionPane.showMessageDialog(this, "La fecha no puede ser anterior a hoy", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Formato incorrecto. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //buscar médicos
        List<Medico> medicos_disponibles = new ArrayList<>();
        List<LocalDateTime> horarios_disponibles = new ArrayList<>();
        List<String> opciones_horarios = new ArrayList<>();

        for (Medico m : sistema.getTodosMedicos()){
            if (m.getEspecialidad() == especialidad){
                List<LocalDateTime> horarios = m.getAgenda().getHorariosDisponibles(fecha);

                for (LocalDateTime hora : horarios){
                    medicos_disponibles.add(m);
                    horarios_disponibles.add(hora);
                    opciones_horarios.add(m.getNombre() + " - " + hora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
                }
            }
        }

        if (opciones_horarios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay horarios disponibles para " + especialidad.getNombre() + " en ese día","Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        //seleccionar horario
        String horarioSeleccionado = (String) JOptionPane.showInputDialog(this, "Horarios disponibles:", "Selecciona hora", JOptionPane.QUESTION_MESSAGE, null, opciones_horarios.toArray(), opciones_horarios.get(0));
        if (horarioSeleccionado == null) return;

        int indice = opciones_horarios.indexOf(horarioSeleccionado);
        Medico medico_elegido = medicos_disponibles.get(indice);
        LocalDateTime fecha_hora_elegida = horarios_disponibles.get(indice);

        //motivo
        String motivo = JOptionPane.showInputDialog(this, "Motivo de la consulta:");
        if (motivo == null || motivo.trim().isEmpty()){
            JOptionPane.showMessageDialog(this, "El motivo no puede estar vacío");
            return;
        }

        //crear xita
        Cita cita = new Cita(paciente, medico_elegido, fecha_hora_elegida, motivo, centro);

        //telefonica?
        if (especialidad == Especialidad.GENERAL || especialidad == Especialidad.ENFERMERIA){
            int opcion = JOptionPane.showConfirmDialog(this, "¿Desea cita telefónica?", "Tipo de cita", JOptionPane.YES_NO_OPTION);
            cita.setTelefonica(opcion == JOptionPane.YES_OPTION);
        } else {
            cita.setTelefonica(false);
        }

        sistema.registrarCita(cita);
        cargarTablaCitas(paciente);

        String tipoCita = cita.isTelefonica() ? "Telefónica" : "Presencial";
        JOptionPane.showMessageDialog(this, "Especialidad: "+especialidad.getNombre()+"\n"+"Médico: "+medico_elegido.getNombre()+"\n"+"Centro: "+centro + "\n"+"Fecha: "+fecha_hora_elegida.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))+"\n"+"Tipo: "+tipoCita+"\n"+"Motivo: "+motivo,"Cita solicitada correctamente", JOptionPane.INFORMATION_MESSAGE);
    }

    
    private Cita getCitaSeleccionada() {
        int filaSeleccionada = TablaCitas.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una cita", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<Cita> citas_paciente = sistema.getCitasParaPaciente(paciente);

        if (filaSeleccionada >= citas_paciente.size()) {
            return null;
        }

        return citas_paciente.get(filaSeleccionada);
    }

    
    private void cargarTablaCitas(Paciente p){
        String[] columnas = {"Fecha", "Hora", "Especialidad","Profesional", "Centro", "Estado", "Tipo"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Cita c : sistema.getCitasParaPaciente(p)) {
            Object[] fila = {
                c.getFechaHora().toLocalDate(),
                c.getFechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                c.getEspecialidad().getNombre(),
                c.getMedico().getNombre(),
                c.getCentro(),
                c.getEstado().getNombre(),
                c.isTelefonica() ? "Telefónica" : "Presencial"
            };
            modelo.addRow(fila);
        }

        TablaCitas.setModel(modelo);
        TablaCitas.setAutoCreateRowSorter(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        LabelNombre = new javax.swing.JLabel();
        LabelTelefono = new javax.swing.JLabel();
        LabelCIPA = new javax.swing.JLabel();
        LabelDireccion = new javax.swing.JLabel();
        LabelDNI = new javax.swing.JLabel();
        BotonCambiarTelefono = new javax.swing.JButton();
        BotonCerrarSesion = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCitas = new javax.swing.JTable();
        BotonCancelarCita = new javax.swing.JButton();
        BotonReagendarCita = new javax.swing.JButton();
        BotonSolicitarCita = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        LabelNombre.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        LabelNombre.setText("Hola, nombre");

        LabelTelefono.setText("Telefono");

        LabelCIPA.setText("CIPA");

        LabelDireccion.setText("Direccion");

        LabelDNI.setText("DNI");

        BotonCambiarTelefono.setText("Cambiar teléfono");
        BotonCambiarTelefono.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCambiarTelefonoActionPerformed(evt);
            }
        });

        BotonCerrarSesion.setText("Cerrar sesión");
        BotonCerrarSesion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCerrarSesionActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(BotonCambiarTelefono)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 168, Short.MAX_VALUE)
                        .addComponent(BotonCerrarSesion))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LabelNombre)
                            .addComponent(LabelDNI)
                            .addComponent(LabelTelefono)
                            .addComponent(LabelCIPA)
                            .addComponent(LabelDireccion))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LabelNombre)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelDNI)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelTelefono)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelCIPA)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LabelDireccion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 146, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonCambiarTelefono)
                    .addComponent(BotonCerrarSesion))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Inicio", jPanel1);

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
        jScrollPane1.setViewportView(TablaCitas);

        BotonCancelarCita.setText("Cancelar");
        BotonCancelarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarCitaActionPerformed(evt);
            }
        });

        BotonReagendarCita.setText("Reagendar");

        BotonSolicitarCita.setText("Solicitar");
        BotonSolicitarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonSolicitarCitaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(BotonCancelarCita)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonReagendarCita)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BotonSolicitarCita)
                        .addGap(0, 142, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonCancelarCita)
                    .addComponent(BotonReagendarCita)
                    .addComponent(BotonSolicitarCita))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Citas", jPanel2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Consultas", jPanel3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Medicación", jPanel4);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 294, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Vacunas", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BotonCerrarSesionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCerrarSesionActionPerformed
        this.dispose();
        new VistaInicioSesion(sistema).setVisible(true);
    }//GEN-LAST:event_BotonCerrarSesionActionPerformed

    private void BotonCambiarTelefonoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCambiarTelefonoActionPerformed
        String nuevoTelefono = JOptionPane.showInputDialog(this, "Nuevo número de teléfono:", paciente.getTelefono());
    
        if (nuevoTelefono != null && !nuevoTelefono.trim().isEmpty()) {
            paciente.setTelefono(nuevoTelefono.trim());
            LabelTelefono.setText("Teléfono: " + nuevoTelefono.trim());
        }
    }//GEN-LAST:event_BotonCambiarTelefonoActionPerformed

    private void BotonCancelarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonCancelarCitaActionPerformed
        String motivo = JOptionPane.showInputDialog(this, "Motivo de cancelación:", "");
        if(!motivo.trim().equals("")){
            sistema.cancelarCita(getCitaSeleccionada(), motivo);
            cargarTablaCitas(paciente);
        } else{
            JOptionPane.showMessageDialog(this, "Introduzca un motivo de cancelación.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_BotonCancelarCitaActionPerformed

    private void BotonSolicitarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonSolicitarCitaActionPerformed
        solicitarCita();
    }//GEN-LAST:event_BotonSolicitarCitaActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new VistaPaciente().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BotonCambiarTelefono;
    private javax.swing.JButton BotonCancelarCita;
    private javax.swing.JButton BotonCerrarSesion;
    private javax.swing.JButton BotonReagendarCita;
    private javax.swing.JButton BotonSolicitarCita;
    private javax.swing.JLabel LabelCIPA;
    private javax.swing.JLabel LabelDNI;
    private javax.swing.JLabel LabelDireccion;
    private javax.swing.JLabel LabelNombre;
    private javax.swing.JLabel LabelTelefono;
    private javax.swing.JTable TablaCitas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

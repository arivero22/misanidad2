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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
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
        cargarTablaMedicamentosActivos(paciente);
        cargarTablaMedicamentosPasados(paciente);
        cargarTablaVacunas(paciente);
        cargarTablaConsultas(paciente);
        cargarTablaPruebas(paciente);
    }
    
    private void solicitarCita(){
        //obtener especialidades disponibles
        Especialidad[] especialidades = Especialidad.values();

        //seleccionar especialidad
        Especialidad especialidad = (Especialidad) JOptionPane.showInputDialog(this, "Selecciona especialidad:", "Nueva cita", JOptionPane.QUESTION_MESSAGE, null, especialidades, especialidades[0]);
        if (especialidad == null) return;

        //pedir centro
        String[] centros = {"Hospital de Arganda", "Hospital Infanta Leonor", "Hospital 12 de Octubre", "Santa Mónica", "Gregorio Marañón"};

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

    
    private Cita getCitaSeleccionada(){
        int filaSeleccionada = TablaCitas.getSelectedRow();

        if (filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una cita", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<Cita> citas_paciente = sistema.getCitasParaPaciente(paciente);

        if (filaSeleccionada >= citas_paciente.size()){
            return null;
        }

        return citas_paciente.get(filaSeleccionada);
    }
    
    private PruebaLaboratorio getPruebaSeleccionada(){
        int filaSeleccionada = TablaPruebas.getSelectedRow();

        if (filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una prueba", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<PruebaLaboratorio> pruebas_paciente = paciente.getPruebas();

        if (filaSeleccionada >= pruebas_paciente.size()){
            return null;
        }

        return pruebas_paciente.get(filaSeleccionada);
    }
    
    private Consulta getConsultaSeleccionada(){
        int filaSeleccionada = TablaConsultas.getSelectedRow();

        if (filaSeleccionada == -1){
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una consulta", "Error", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        List<Consulta> consultas_paciente = paciente.getHistorialClinico();

        if (filaSeleccionada >= consultas_paciente.size()) {
            return null;
        }

        return consultas_paciente.get(filaSeleccionada);
    }

    
    private void cargarTablaCitas(Paciente p){
        String[] columnas = {"Fecha", "Hora", "Especialidad","Profesional","Motivo", "Centro", "Estado", "Tipo"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for (Cita c : sistema.getCitasParaPaciente(p)){
            Object[] fila = {
                c.getFechaHora().toLocalDate(),
                c.getFechaHora().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                c.getEspecialidad().getNombre(),
                c.getMedico().getNombre(),
                c.getMotivo(),
                c.getCentro(),
                c.getEstado().getNombre(),
                c.isTelefonica() ? "Telefónica" : "Presencial"
            };
            modelo.addRow(fila);
        }

        TablaCitas.setModel(modelo);
        TablaCitas.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaPruebas(Paciente p){
        String[] columnas = {"Fecha", "Prueba", "Centro", "Informe"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for (PruebaLaboratorio prue : p.getPruebas()){
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
    
    private void cargarTablaVacunas(Paciente p){
        String[] columnas = {"Nombre", "Dosis", "Fecha de administración","Fecha siguiente dosis"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for (Vacuna v : p.getVacunas()) {
            Object[] fila = {
                v.getNombre(),
                v.getDosis(),
                v.getFechaAdministracion(),
                v.getFechaSiguienteDosis()};
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
        
        for (Consulta c : p.getHistorialClinico()){
            Object[] fila = {
                c.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                c.getMotivo(),
                c.getMedico().getNombre(),
                c.getRecomendaciones()};
            modelo.addRow(fila);
        }

        TablaConsultas.setModel(modelo);
        TablaConsultas.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaMedicamentosActivos(Paciente p){
        String[] columnas = {"Nombre", "Dosis", "Fecha de fin","Fecha de receta"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Medicamento m : p.getMedicamentos()){
            if(m.getFechaFin().isAfter(LocalDate.now())){
                Object[] fila = {m.getNombre(),m.getDosis() + "/" + m.getFrecuencia(),m.getFechaFin(),m.getFechaReceta()};
                modelo.addRow(fila);
            }
        }

        TablaMedicamentosActivos.setModel(modelo);
        TablaMedicamentosActivos.setAutoCreateRowSorter(true);
    }
    
    private void cargarTablaMedicamentosPasados(Paciente p){
        String[] columnas = {"Nombre", "Dosis", "Fecha de fin","Fecha de receta"};

        DefaultTableModel modelo = new DefaultTableModel(columnas, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        for(Medicamento m : p.getMedicamentos()){
            if(!m.getFechaFin().isAfter(LocalDate.now())){
                Object[] fila = {m.getNombre(),m.getDosis() + "/" + m.getFrecuencia(),m.getFechaFin(),m.getFechaReceta()};
                modelo.addRow(fila);
            }
        }

        TablaMedicamentosPasados.setModel(modelo);
        TablaMedicamentosPasados.setAutoCreateRowSorter(true);
    }
    
    private void reagendarCita(){
        Cita cita = getCitaSeleccionada();

        if (cita == null){
            return;
        }

        if (cita.getEstado() == EstadoCita.CANCELADA){
            JOptionPane.showMessageDialog(this, "No se puede reagendar una cita cancelada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cita.getEstado() == EstadoCita.COMPLETADA){
            JOptionPane.showMessageDialog(this, "No se puede reagendar una cita completada", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Medico medico = cita.getMedico();

        String fecha_str = JOptionPane.showInputDialog(this, 
            "Nueva fecha (dd/MM/yyyy):\n(La cita actual es el " + cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ")",
            LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        if (fecha_str == null) return;

        LocalDate fecha;
        try{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            fecha = LocalDate.parse(fecha_str, formatter);

            if (fecha.isBefore(LocalDate.now())){
                JOptionPane.showMessageDialog(this, "La fecha no puede ser anterior a hoy", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception e){
            JOptionPane.showMessageDialog(this, "Formato incorrecto. Use dd/MM/yyyy", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<LocalDateTime> horarios = medico.getAgenda().getHorariosDisponibles(fecha);

        if (horarios.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay horarios disponibles para " + medico.getNombre() + " en esa fecha", "Sin disponibilidad", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<String> opciones_horarios = new ArrayList<>();
        for (LocalDateTime hora : horarios){
            opciones_horarios.add(hora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        String horarioSeleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione nuevo horario para " + fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ":", "Reagendar cita", JOptionPane.QUESTION_MESSAGE,null, opciones_horarios.toArray(), opciones_horarios.get(0));

        if (horarioSeleccionado == null) return;

        int indice = opciones_horarios.indexOf(horarioSeleccionado);
        LocalDateTime nuevaFechaHora = horarios.get(indice);

        String motivoReagendamiento = JOptionPane.showInputDialog(this, "Motivo del reagendamiento:", "");

        if (motivoReagendamiento == null || motivoReagendamiento.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un motivo para reagendar la cita", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sistema.cancelarCita(cita, "Reagendada: " + motivoReagendamiento);

        Cita nueva = new Cita(paciente, medico, nuevaFechaHora, cita.getMotivo()+" (Reagendada)", cita.getCentro());
        nueva.setTelefonica(cita.isTelefonica());

        sistema.registrarCita(nueva);

        cargarTablaCitas(paciente);

        JOptionPane.showMessageDialog(this, "Cita reagendada:\n" + "Original: " + cita.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + "Nueva:" + nuevaFechaHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" + "Médico: " + medico.getNombre() + "\n" + "Centro: " + cita.getCentro() + "\n" + "Motivo: " + motivoReagendamiento,"Cita Reagendada",JOptionPane.INFORMATION_MESSAGE);
    }
    
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
        jScrollPane5 = new javax.swing.JScrollPane();
        TablaConsultas = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TablaMedicamentosActivos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TablaMedicamentosPasados = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        TablaVacunas = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        TablaPruebas = new javax.swing.JTable();

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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 152, Short.MAX_VALUE)
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
        TablaCitas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaCitasMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TablaCitasMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(TablaCitas);

        BotonCancelarCita.setText("Cancelar");
        BotonCancelarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonCancelarCitaActionPerformed(evt);
            }
        });

        BotonReagendarCita.setText("Reagendar");
        BotonReagendarCita.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BotonReagendarCitaActionPerformed(evt);
            }
        });

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BotonCancelarCita)
                    .addComponent(BotonReagendarCita)
                    .addComponent(BotonSolicitarCita))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Citas", jPanel2);

        TablaConsultas.setModel(new javax.swing.table.DefaultTableModel(
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
        TablaConsultas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                TablaConsultasMousePressed(evt);
            }
        });
        jScrollPane5.setViewportView(TablaConsultas);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Consultas", jPanel3);

        TablaMedicamentosActivos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(TablaMedicamentosActivos);

        jLabel1.setText("Medicamentos activos");

        jLabel2.setText("Medicamentos pasados");

        TablaMedicamentosPasados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(TablaMedicamentosPasados);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Medicación", jPanel4);

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
        jScrollPane4.setViewportView(TablaVacunas);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Vacunas", jPanel5);

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
        jScrollPane6.setViewportView(TablaPruebas);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 288, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Pruebas", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
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

    private void BotonReagendarCitaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BotonReagendarCitaActionPerformed
        reagendarCita();
    }//GEN-LAST:event_BotonReagendarCitaActionPerformed

    private void TablaCitasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCitasMouseClicked
        // TODO add your handling code here:
        
        //AQUII
    }//GEN-LAST:event_TablaCitasMouseClicked

    private void TablaConsultasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaConsultasMousePressed
        // TODO add your handling code here:        
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
    }//GEN-LAST:event_TablaConsultasMousePressed

    private void TablaCitasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaCitasMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_TablaCitasMousePressed

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
    private javax.swing.JTable TablaConsultas;
    private javax.swing.JTable TablaMedicamentosActivos;
    private javax.swing.JTable TablaMedicamentosPasados;
    private javax.swing.JTable TablaPruebas;
    private javax.swing.JTable TablaVacunas;
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
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    // End of variables declaration//GEN-END:variables
}

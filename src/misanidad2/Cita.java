/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.*;

/**
 *
 * @author alvar
 */
public class Cita implements Serializable {
    private static int id = 0;
    private LocalDateTime fechaHora;
    private Paciente paciente;
    private Medico medico;
    private String motivo;
    private EstadoCita estado;
    private String centro;
    private boolean telefonica;
    private String motivo_cancelacion;
    private LocalDateTime fecha_cancelacion;
    private LocalDateTime fecha_solicitud;
    
    public Cita(Paciente paciente, Medico medico, LocalDateTime fechaHora, String motivo, String centro){
        id++;
        this.paciente = paciente;
        this.medico = medico;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.centro = centro;
        this.estado = EstadoCita.PENDIENTE;
        this.telefonica = false;
        this.fecha_solicitud = LocalDateTime.now();
    }
    
    public int getId(){ return id; }
    public LocalDateTime getFechaHora(){ return fechaHora; }
    public Paciente getPaciente(){ return paciente; }
    public Medico getMedico(){ return medico; }
    public Especialidad getEspecialidad(){ return medico.getEspecialidad(); }
    public String getMotivo(){ return motivo; }
    public EstadoCita getEstado(){ return estado; }
    public String getCentro(){ return centro; }
    public boolean isTelefonica(){ return telefonica; }
    public String getMotivoCancelacion(){ return motivo_cancelacion; }
    public LocalDateTime getFechaCancelacion(){ return fecha_cancelacion; }
    public LocalDateTime getFechaSolicitud(){ return fecha_solicitud; }
    
    public void setEstado(EstadoCita estado){ this.estado = estado; }
    public void setMotivoCancelacion(String motivo){ motivo_cancelacion = motivo; }
    public void setFechaCancelacion(LocalDateTime fecha_hora){ fecha_cancelacion = fecha_hora; }
    public void setTelefonica(boolean telefonica){ this.telefonica = telefonica; }
}

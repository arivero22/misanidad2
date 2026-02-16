/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alvar
 */
public class Agenda implements Serializable {
    private Medico medico;
    private LocalTime hora_inicio;
    private LocalTime hora_fin;
    private int duracion; //mins
    private Sistema sistema;
    
    
    public Agenda(Medico medico, Sistema sistema){
        this.medico = medico;
        this.hora_inicio = LocalTime.of(9, 0);
        this.hora_fin = LocalTime.of(17, 0);
        this.duracion = 30;
        this.sistema = sistema;
    }
    
    public Agenda(Medico medico, Sistema sistema, LocalTime hora_inicio, LocalTime hora_fin, int duracion){
        this.medico = medico;
        this.hora_inicio = hora_inicio;
        this.hora_fin = hora_fin;
        this.duracion = duracion;
        this.sistema = sistema;
    }
    
    public List<Cita> getCitasDelDia(LocalDate fecha){
        List<Cita> citas_dia = new ArrayList<>();
        
        for(Cita c : sistema.getTodasCitas()){
            if(c.getMedico().equals(this.medico) && c.getFechaHora().toLocalDate().equals(fecha) && c.getEstado() == EstadoCita.PENDIENTE){
                citas_dia.add(c);
            }
        }
        
        return citas_dia;
    }
    
    public boolean estaDisponible(LocalDateTime fecha_hora){
        LocalDate fecha = fecha_hora.toLocalDate();
        LocalTime hora = fecha_hora.toLocalTime();

        if (hora.isBefore(hora_inicio) || hora.isAfter(hora_fin)) {
            return false;
        }
        
        for(Cita c : sistema.getTodasCitas()){
            if(c.getMedico().equals(medico) && c.getEstado() == EstadoCita.PENDIENTE && c.getFechaHora().equals(fecha_hora)){
                return false;
            }
        }
        
        return true;
    }
    
    public List<LocalDateTime> getHorariosDisponibles(LocalDate fecha){
        List<LocalDateTime> disponibles = new ArrayList<>();
        
        LocalTime hora = hora_inicio;
        while(hora.isBefore(hora_fin)){ //igual hay que hacer hora + duracion
            LocalDateTime hueco = LocalDateTime.of(fecha, hora);
            if(estaDisponible(hueco)){
                disponibles.add(hueco);
            }
            hora = hora.plusMinutes(duracion);
        }
        
        return disponibles;
    }
    
    public List<LocalDateTime> getHorariosDisponiblesProximos(int dias){
        List<LocalDateTime> disponibles = new ArrayList<>();
        
        for(int i = 0; i < dias; i++){
            LocalDate fecha = LocalDate.now().plusDays(i);
            disponibles.addAll(getHorariosDisponibles(fecha));
        }
        
        return disponibles;
    }
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import static java.lang.Math.abs;
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
            if(c.getMedico().equals(this.medico) && c.getFechaHora().toLocalDate().equals(fecha) && c.getEstado() == EstadoCita.CONFIRMADA){
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
    
    public int reagendarDia(LocalDate fecha){
        List<Cita> citas_dia = getCitasDelDia(fecha);
        int contador =0;
        
        if(citas_dia.isEmpty()) return 0;
        
        citas_dia.sort((c1, c2) -> c1.getFechaHora().compareTo(c2.getFechaHora()));
        
        for(int i= 1; i<=30; i++){
            LocalDate fecha_nueva = fecha.plusDays(i);
            List<LocalDateTime> horarios_libres = getHorariosDisponibles(fecha_nueva);
            
            if(horarios_libres.size() >= citas_dia.size()){
                LocalTime hora_primera_cita = citas_dia.get(0).getFechaHora().toLocalTime();
                int indice_inicio = 0;
                int menor_diff = Integer.MAX_VALUE;
                
                for(int j = 0; j < horarios_libres.size(); j++){
                    int diff = Math.abs(horarios_libres.get(j).toLocalTime().getHour() - hora_primera_cita.getHour());
                    if(diff < menor_diff){
                        menor_diff = diff;
                        indice_inicio = j;
                    }
                }
                
                for(int j = 0; j < citas_dia.size() && (indice_inicio+j) < horarios_libres.size(); j++){
                    Cita c = citas_dia.get(j);
                    LocalDateTime nueva_fecha_hora = horarios_libres.get(indice_inicio+j);

                    c.setEstado(EstadoCita.CANCELADA);
                    c.setMotivoCancelacion("Reagendada por el personal");
                    c.setFechaCancelacion(LocalDateTime.now());

                    Cita nueva = new Cita(c.getPaciente(), c.getMedico(), nueva_fecha_hora,c.getMotivo() + " (Reagendada)", c.getCentro());
                    nueva.setTelefonica(c.isTelefonica());
                    nueva.setEstado(EstadoCita.CONFIRMADA);

                    sistema.registrarCita(nueva);
                    contador++;
                }
                break;
            }
        }
        return contador;
    }
    
    private LocalDateTime buscarProxFechaDisp(LocalDate fecha_original){
        for(int i=1; i<=30; i++){
            LocalDate fecha_busq = fecha_original.plusDays(i);
            List<LocalDateTime> horarios = getHorariosDisponibles(fecha_busq);
            
            if(!horarios.isEmpty()){
                return horarios.get(0);
            }
        }
        return null;
    }
}

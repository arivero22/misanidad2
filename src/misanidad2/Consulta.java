/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author alvar
 */
public class Consulta implements Serializable {
    private Medico medico;
    private LocalDateTime fecha_hora;
    private String motivo, recomendaciones;
    
    public Consulta(Cita cita, String recomendaciones){
        this.medico = cita.getMedico();
        this.fecha_hora = cita.getFechaHora();
        this.motivo = cita.getMotivo();
        this.recomendaciones = recomendaciones;
    }
    
    public LocalDateTime getFechaHora(){ return fecha_hora; }
    public Medico getMedico(){ return medico; }
    public String getMotivo(){ return motivo; }
    public String getRecomendaciones(){ return recomendaciones; }
}


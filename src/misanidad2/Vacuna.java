/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.time.LocalDate;

/**
 *
 * @author alvar
 */
public class Vacuna extends Medicamento {
    private LocalDate fecha_sig_dosis, fecha_administracion;
    
    public Vacuna(String nombre, String dosis, LocalDate fecha_administracion, LocalDate fecha_sig_dosis){
        super(nombre, dosis, "", null);
        this.fecha_administracion = fecha_administracion;
        this.fecha_sig_dosis = fecha_sig_dosis;
    }
    
    public Vacuna(String nombre, String dosis, LocalDate fecha_administracion){
        super(nombre, dosis, "", null);
        this.fecha_administracion = fecha_administracion;
        this.fecha_sig_dosis = null;
    }
    
    public LocalDate getFechaSiguienteDosis(){ return fecha_sig_dosis; }
    public LocalDate getFechaAdministracion(){ return fecha_administracion; }
}

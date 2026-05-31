/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author alvar
 */
public class Medicamento implements Serializable {
    private String nombre, dosis, frecuencia;
    private boolean cronico;
    private LocalDate fecha_fin, fecha_receta;
    
    public Medicamento(String nombre, String dosis, String frecuencia, LocalDate fecha_fin){
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.cronico = false;
        this.fecha_fin = fecha_fin;
        this.fecha_receta = LocalDate.now();
    }
    
    public Medicamento(String nombre, String dosis, String frecuencia){
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.cronico = true;
        this.fecha_fin = LocalDate.now().plusYears(10);
        this.fecha_receta = LocalDate.now();
    }
    
    public String getNombre(){ return nombre; }
    public String getDosis(){ return dosis; }
    public String getFrecuencia(){ return frecuencia; }
    public boolean isCronico(){ return cronico; }
    public LocalDate getFechaFin(){ return fecha_fin; }
    public LocalDate getFechaReceta(){ return fecha_receta; }
}

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
public class PruebaLaboratorio implements Serializable {
    private LocalDate fecha;
    private String centro, informe, nombre;
    
    public PruebaLaboratorio(String nombre, LocalDate fecha, String centro, String informe){
        this.nombre = nombre;
        this.fecha = fecha;
        this.centro = centro;
        this.informe = informe;
    }
    
    public String getNombre(){ return nombre; }
    public LocalDate getFecha(){ return fecha; }
    public String getCentro(){ return centro; }
    public String getInforme(){ return informe; }
}

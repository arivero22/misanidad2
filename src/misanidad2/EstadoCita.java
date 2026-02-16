/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public enum EstadoCita {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada"),
    COMPLETADA("Completada"),
    REAGENDADA("Reagendada");
    
    private final String nombre;
    
    EstadoCita(String nombre){
        this.nombre = nombre;
    }
    
    public String getNombre(){ return nombre; }
    
    @Override
    public String toString(){
        return nombre;
    }
}

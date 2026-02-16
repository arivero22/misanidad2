/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public enum Especialidad {
    GENERAL("General"),
    ENFERMERIA("Enfermería"),
    CARDIOLOGIA("Cardiología"),
    UROLOGIA("Urología"),
    OFTALMOLOGIA("Oftalmología");
    
    private final String nombre;
    
    Especialidad(String nombre){
        this.nombre = nombre;
    }
    
    public String getNombre(){ return nombre; }
    
    @Override
    public String toString(){
        return nombre;
    }
}

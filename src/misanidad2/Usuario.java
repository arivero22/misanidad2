/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.util.*;

/**
 *
 * @author alvar
 */
public abstract class Usuario implements Serializable {
    private String nombre, dni;
    private TipoUsuario tipo;
    private boolean activo;
    
    public Usuario(String nombre, String dni, TipoUsuario tipo){
        this.nombre = nombre;
        this.dni = dni;
        this.tipo = tipo;
        activo = true;
    }
    
    public String getDni(){ return dni; }
    public TipoUsuario getTipo(){ return tipo; }
    public String getNombre(){ return nombre; }
    public boolean isActivo(){ return activo; }
    
    public boolean isMedico(){
        if(tipo == TipoUsuario.MEDICO){
            return true;
        } else{
            return false;
        }
    }
    
    @Override
    public String toString(){
        return nombre;
    }
    
    public void setNombre(String nombre){
       this.nombre = nombre; 
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    public void setDni(String dni){
        this.dni = dni;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == this) return true;
        if(obj == null || this.getClass() != obj.getClass()) return false;
        
        Usuario that = (Usuario) obj;
        return that.getDni().equals(this.getDni());
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }

}

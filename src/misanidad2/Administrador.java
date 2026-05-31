/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public class Administrador extends Usuario {
    public Administrador(String nombre, String dni){
        super(nombre, dni, TipoUsuario.ADMINISTRADOR);
    }
}

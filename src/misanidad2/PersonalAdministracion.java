/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public class PersonalAdministracion extends Usuario {
    public PersonalAdministracion(String nombre, String dni){
        super(nombre, dni, TipoUsuario.PERSONAL_ADMINISTRACION);
    }
}

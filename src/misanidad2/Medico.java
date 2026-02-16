/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public class Medico extends Usuario {
    private Especialidad especialidad;
    private Agenda agenda;
    
    public Medico(String nombre, String dni, Especialidad especialidad, Sistema sistema){
        super(nombre, dni, TipoUsuario.MEDICO);
        this.especialidad = especialidad;
        this.agenda = new Agenda(this, sistema);
    }
    
    public Especialidad getEspecialidad(){ return especialidad; }
    public Agenda getAgenda(){ return agenda; }
}

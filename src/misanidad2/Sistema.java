/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @author alvar
 */
public class Sistema implements Serializable {
    private List<Usuario> usuarios;
    private List<Cita> citas;
    
    public Sistema(){
        usuarios = new ArrayList<>();
        citas = new ArrayList<>();
    }
    
    public List<Usuario> getTodosUsuarios(){ return usuarios; }
    public List<Cita> getTodasCitas(){ return citas; }
    
    public List<Medico> getTodosMedicos(){
        List<Medico> medicos = new ArrayList<>();
        
        for(Usuario u : usuarios){
            if(u.getTipo() == TipoUsuario.MEDICO){
                medicos.add((Medico) u);
            }
        }
        
        return medicos;
    }
    
    public void registrarUsuario(Usuario u){
        usuarios.add(u);
    }
    
    public void eliminarUsuario(Usuario u){
        usuarios.remove(u);
    }
    
    public Usuario buscarUsuario(String id){
        //buscar por cipa
        for(Usuario u : usuarios){
            if(u.getTipo() == TipoUsuario.PACIENTE){
                Paciente p = (Paciente) u;
                if(p.getCipa().equals(id)){
                    return p;
                }
            }
        }
        
        //buscar por dni
        for(Usuario u : usuarios){
            if(u.getDni().equalsIgnoreCase(id)){
                return u;
            }
        }
        
        return null;
    }
    
    public void registrarCita(Cita c){
        citas.add(c);
    }
    
    public void eliminarCita(Usuario c){
        usuarios.remove(c);
    }
    
    public void cancelarCita(Cita c, String motivo){
        c.setEstado(EstadoCita.CANCELADA);
        c.setMotivoCancelacion(motivo);
        c.setFechaCancelacion(LocalDateTime.now());
    }
    
    public List<Cita> getCitasParaPaciente(Paciente p){
        List<Cita> citas_paciente = new ArrayList<>();
        
        for(Cita c : citas){
            if(c.getPaciente().equals(p)){
                citas_paciente.add(c);
            }
        }
        
        return citas_paciente;
    }
    
    public List<Cita> getCitasParaMedico(Medico m){
        List<Cita> citas_medico = new ArrayList<>();
        
        for(Cita c : citas){
            if(c.getMedico().equals(m)){
                citas_medico.add(c);
            }
        }
        
        return citas_medico;
    }
    
    public void datosPrueba(Sistema sistema){
        Medico m1 = new Medico("Chopper", "11111111A", Especialidad.GENERAL, sistema);
        Medico m2 = new Medico("Juan Cuesta", "22222222B", Especialidad.GENERAL, sistema);
        Medico m3 = new Medico("Andres Guerra", "33333333C", Especialidad.CARDIOLOGIA, sistema);
        Medico m4 = new Medico("Mariano Delgado", "44444444D", Especialidad.ENFERMERIA, sistema);
        Paciente p1 = new Paciente("Eren Jagger", "55555555E", "1234567890", "1234565789", "Shiganshina");
        Paciente p2 = new Paciente("Walter White", "66666666F", "9876545321", "951783654", "Nuevo Mexico");
        Paciente p3 = new Paciente("Jesse Pinkman", "77777777G", "6451563218", "321951756", "Nuevo Mexico");
        Paciente p4 = new Paciente("James McGill", "88888888H", "3219514382", "651429381", "Nuevo Mexico");
        Paciente p5 = new Paciente("Reiner Braun", "99999999I", "9511673819", "321917386", "Marley");
        
        registrarUsuario(m1);
        registrarUsuario(m2);
        registrarUsuario(m3);
        registrarUsuario(m4);
        registrarUsuario(p1);
        registrarUsuario(p2);
        registrarUsuario(p3);
        registrarUsuario(p4);
        registrarUsuario(p5);
        
        Cita c1 = new Cita(p4, m1, LocalDateTime.now(), "Dolor de cabeza", "Santa Monica");
        registrarCita(c1);
    }
}

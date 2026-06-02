/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDate;
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
    
    public void actualizarUsuario(Paciente paciente, String nombre, String telefono, String direccion){
        paciente.setNombre(nombre);
        paciente.setTelefono(telefono);
        paciente.setDireccion(direccion);
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
    
    public List<Paciente> getTodosPacientes(){
        List<Paciente> pacientes = new ArrayList<>();
        
        for(Usuario u : usuarios){
            if(u.getTipo() == TipoUsuario.PACIENTE){
                pacientes.add((Paciente) u);
            }
        }
        
        return pacientes;
    }
    
    public List<Usuario> getTodosEmpleados(){
        List<Usuario> empleados = new ArrayList<>();
        
        for(Usuario u : usuarios){
            if(u.getTipo() != TipoUsuario.PACIENTE){
                empleados.add(u);
            }
        }
        
        return empleados;
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
    
    public boolean existeUsuario(String id){
        Usuario usr = buscarUsuario(id);
        if(usr != null){
            return true;
        } else{
            return false;
        }
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
    
    public List<Paciente> getPacientesParaMedico(Medico m){
        List<Paciente> pacientes = new ArrayList<>();
        
        for(Cita c : citas){
            if(c.getMedico().equals(m) && c.getEstado() != EstadoCita.CANCELADA){
                Paciente p = c.getPaciente();
                
                if(!pacientes.contains(p)){
                    pacientes.add(p);
                }
            }
        }
        
        return pacientes;
    }
    
    public void datosPrueba(Sistema sistema){
        Medico m1 = new Medico("Steve Rogers", "11111111A", Especialidad.GENERAL, sistema);
        Medico m2 = new Medico("Juan Cuesta", "22222222B", Especialidad.GENERAL, sistema);
        Medico m3 = new Medico("Andres Guerra", "33333333C", Especialidad.CARDIOLOGIA, sistema);
        Medico m4 = new Medico("Mariano Delgado", "44444444D", Especialidad.ENFERMERIA, sistema);
        Paciente p1 = new Paciente("Eren Jagger", "55555555E", "1234567890", "1234565789", "Shiganshina");
        Paciente p2 = new Paciente("Walter White", "66666666F", "9876545321", "951783654", "Nuevo Mexico");
        Paciente p3 = new Paciente("Jesse Pinkman", "77777777G", "6451563218", "321951756", "Nuevo Mexico");
        Paciente p4 = new Paciente("James McGill", "88888888H", "3219514382", "651429381", "Nuevo Mexico");
        Paciente p5 = new Paciente("Reiner Braun", "99999999I", "9511673819", "321917386", "Marley");
        PersonalAdministracion pers1 = new PersonalAdministracion("Conan Doyle", "12121212J");
        Administrador admin = new Administrador("Admin", "00000000X");
        
        registrarUsuario(m1);
        registrarUsuario(m2);
        registrarUsuario(m3);
        registrarUsuario(m4);
        registrarUsuario(p1);
        registrarUsuario(p2);
        registrarUsuario(p3);
        registrarUsuario(p4);
        registrarUsuario(p5);
        registrarUsuario(pers1);
        registrarUsuario(admin);
        
        Cita c1 = new Cita(p1, m1, LocalDateTime.now().minusDays(1), "Dolor de cabeza", "Santa Monica");
        c1.setEstado(EstadoCita.COMPLETADA);
        Cita c2 = new Cita(p1, m4, LocalDateTime.now().plusDays(3).plusHours(1), "Mareos", "Gregorio Marañón");
        Cita c3 = new Cita(p2, m3, LocalDateTime.now().minusDays(3).plusHours(2), "Taquicardias", "Hospital de Arganda");
        c3.setEstado(EstadoCita.COMPLETADA);
        Cita c4 = new Cita(p2, m2, LocalDateTime.now().plusDays(1), "Fiebre", "Santa Monica");
        Cita c5 = new Cita(p3, m1, LocalDateTime.now().plusDays(3), "Infección de oído", "Santa Monica");
        Cita c6 = new Cita(p3, m2, LocalDateTime.now().minusDays(2), "Esguince", "Hospital Infanta Leonor");
        c6.setEstado(EstadoCita.COMPLETADA);
        Cita c7 = new Cita(p4, m2, LocalDateTime.now().minusDays(1), "Náuseas", "Hospital 12 de Octubre");
        c7.setEstado(EstadoCita.COMPLETADA);
        Cita c8 = new Cita(p4, m4, LocalDateTime.now().plusDays(2), "Tos", "Gregorio Marañón");
        Cita c9 = new Cita(p5, m2, LocalDateTime.now().plusDays(4), "Insomnio", "Hospital de Arganda");
        Cita c10 = new Cita(p5, m1, LocalDateTime.now().minusDays(7), "Dolor de espalda", "Hospital de Arganda");
        c10.setEstado(EstadoCita.COMPLETADA);

        registrarCita(c1);
        registrarCita(c2);
        registrarCita(c3);
        registrarCita(c4);
        registrarCita(c5);
        registrarCita(c6);
        registrarCita(c7);
        registrarCita(c8);
        registrarCita(c9);
        registrarCita(c10);
        
        Consulta con1 = new Consulta(c1, "El paciente presenta cefalea de intensidad leve-moderada, sin datos clínicos de alarma en la valoración actual. Se recomienda reposo relativo, adecuada hidratación y seguimiento de las medidas terapéuticas indicadas. En caso de persistencia, empeoramiento del cuadro o aparición de síntomas asociados de relevancia clínica, se aconseja nueva valoración médica.");
        Consulta con2 = new Consulta(c3, "El paciente presenta episodios de taquicardia, caracterizados por un aumento de la frecuencia cardíaca. En la valoración actual no se objetivan signos de inestabilidad clínica. Se recomienda seguimiento médico, evitar factores desencadenantes como el exceso de cafeína o estimulantes y mantener una adecuada hidratación. En caso de empeoramiento de los síntomas o aparición de dolor torácico, dificultad respiratoria o pérdida de conocimiento, se aconseja atención médica inmediata.");
        Consulta con3 = new Consulta(c6, "El paciente presenta un esguince, compatible con una lesión de los ligamentos de la articulación afectada. Se recomienda reposo relativo, aplicación de frío local, elevación de la extremidad y seguimiento de las medidas terapéuticas prescritas. Se aconseja evitar esfuerzos o actividades que puedan agravar la lesión hasta su completa recuperación. En caso de aumento del dolor, inflamación importante o limitación funcional progresiva, se recomienda nueva valoración médica.");
        Consulta con4 = new Consulta(c7, "El paciente presenta un cuadro de náuseas, sin signos de alarma en la valoración actual. Se recomienda mantener una adecuada hidratación, realizar una alimentación ligera y fraccionada, y seguir las medidas terapéuticas indicadas. En caso de persistencia de los síntomas, vómitos repetidos, signos de deshidratación o empeoramiento del estado general, se aconseja nueva valoración médica.");
        Consulta con5 = new Consulta(c10, "El paciente presenta dolor de espalda de características mecánicas, sin evidencia de signos de alarma en la valoración actual. Se recomienda reposo relativo, evitar sobreesfuerzos y mantener las medidas terapéuticas prescritas para el control de la sintomatología. Se aconseja una reincorporación progresiva a la actividad habitual según evolución clínica. En caso de persistencia, agravamiento del dolor o aparición de síntomas neurológicos asociados, se recomienda nueva valoración médica.");
        p1.agregarConsulta(con1);
        p2.agregarConsulta(con2);
        p3.agregarConsulta(con3);
        p4.agregarConsulta(con4);
        p5.agregarConsulta(con5);
        
        PruebaLaboratorio prue1 = new PruebaLaboratorio("Electrocardiograma", LocalDate.now().minusDays(3), "Hospital de Arganda", "Pruebas realizadas:\n" +
"\n" +
"Electrocardiograma (ECG) de 12 derivaciones.\n" +
"Monitorización cardíaca durante 30 minutos.\n" +
"Toma seriada de constantes vitales.\n" +
"\n" +
"Resultados:\n" +
"El electrocardiograma muestra ritmo sinusal regular con frecuencia cardíaca de 122 lpm. Intervalos PR, QRS y QT dentro de los límites de normalidad. No se observan alteraciones de la repolarización ni signos sugestivos de isquemia aguda.\n" +
"\n" +
"Durante la monitorización se registran episodios breves de taquicardia sinusal, con frecuencia máxima de 128 lpm, sin aparición de arritmias complejas ni alteraciones significativas de la conducción cardíaca.\n" +
"\n" +
"Tensión arterial media: 126/80 mmHg. Saturación de oxígeno: 98-99% en aire ambiente.\n" +
"\n" +
"Conclusión:\n" +
"Hallazgos compatibles con episodios de taquicardia sinusal, sin evidencia de cardiopatía aguda ni alteraciones electrocardiográficas de relevancia clínica en las pruebas realizadas.");
        
        p2.agregarPrueba(prue1);
        Medicamento med1 = new Medicamento("Ibuprofeno", "1g", "8h", LocalDate.now().plusDays(7));
        Medicamento med2 = new Medicamento("Paracetamol", "0.75g", "8h", LocalDate.now().plusDays(7));
        p4.agregarMedicamento(med1);
        p5.agregarMedicamento(med2);
    }
}

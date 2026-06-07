/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.util.*;

/**
 *
 * @author alvar
 */
public class Paciente extends Usuario {
    private String cipa, telefono, direccion;
    private List<Medicamento> medicamentos;
    private List<Vacuna> vacunas;
    private List<Consulta> historial_clinico;
    private List<PruebaLaboratorio> pruebas;
    private List<Notificacion> notificaciones;
    
    
    public Paciente(String nombre, String dni, String cipa, String telefono, String direccion){
        super(nombre, dni, TipoUsuario.PACIENTE);
        this.cipa = cipa;
        this.telefono = telefono;
        this.direccion = direccion;
        this.medicamentos = new ArrayList<>();
        this.vacunas = new ArrayList<>();
        this.historial_clinico = new ArrayList<>();
        this.pruebas = new ArrayList<>();
        this.notificaciones = new ArrayList<>();
    }
    
    public String getCipa(){ return cipa; }
    public String getTelefono(){ return telefono; }
    public String getDireccion(){ return direccion; }
    public List<Medicamento> getMedicamentos(){ return medicamentos; }
    public List<Vacuna> getVacunas(){ return vacunas; }
    public List<Consulta> getHistorialClinico(){ return historial_clinico; }
    public List<PruebaLaboratorio> getPruebas(){ return pruebas; }
    public List<Notificacion> getNotificaciones(){ return notificaciones; }
    
    public void setTelefono(String telefono){ this.telefono = telefono; }
    
    public void setDireccion(String direccion){
        this.direccion = direccion;
    }
    
    public void setCipa(String cipa){
        this.cipa = cipa;
    }
    
    public void agregarMedicamento(Medicamento medicamento){ medicamentos.add(medicamento); }
    public void borrarMedicamento(Medicamento medicamento){ medicamentos.remove(medicamento); }
    
    public void agregarVacuna(Vacuna vacuna){ vacunas.add(vacuna); }
    public void borrarVacuna(Vacuna vacuna){ vacunas.remove(vacuna); }
    
    public void agregarConsulta(Consulta consulta){ historial_clinico.add(consulta); }
    
    public void agregarPrueba(PruebaLaboratorio prueba){ pruebas.add(prueba); }
}

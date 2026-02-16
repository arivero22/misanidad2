/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

/**
 *
 * @author alvar
 */
public class Paciente extends Usuario {
    private String cipa, telefono, direccion;
    /*private TarjetaSanitaria tarjeta_sanitaria;
    private HistorialClinico historial;*/
    
    
    public Paciente(String nombre, String dni, String cipa, String telefono, String direccion){
        super(nombre, dni, TipoUsuario.PACIENTE);
        this.cipa = cipa;
        this.telefono = telefono;
        this.direccion = direccion;
        /*tarjeta_sanitaria = new TarjetaSanitaria();
        historial = new HistorialClinico();*/
    }
    
    public String getCipa(){ return cipa; }
    public String getTelefono(){ return telefono; }
    public String getDireccion(){ return direccion; }
    
    public void setTelefono(String telefono){ this.telefono = telefono; }
}

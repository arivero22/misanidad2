/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author alvar
 */
public class Notificacion implements Serializable {
    private String mensaje;
    private LocalDateTime fecha_hora;
    
    public Notificacion(String mensaje){
        this.mensaje = mensaje;
        fecha_hora = LocalDateTime.now();
    }
    
    @Override
    public String toString(){
        return mensaje;
    }
}
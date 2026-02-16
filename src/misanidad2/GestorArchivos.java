/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.*;

/**
 *
 * @author alvar
 */
public class GestorArchivos {
    private static final String ARCHIVO = "MiSanidad.dat";
    
    public static Sistema cargarSistema(){
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("No se ha encontrado .dat");
            Sistema sistema = new Sistema();
            sistema.datosPrueba(sistema);
            return sistema;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            System.out.println("Datos cargados!! :)");
            return (Sistema) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error al cargar: " + e.getMessage());
            return new Sistema();
        }
    }
    
    public static void guardarSistema(Sistema sistema){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(sistema);
            System.out.println("Datos guardados.");
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
}

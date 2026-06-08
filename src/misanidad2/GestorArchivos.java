/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package misanidad2;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alvar
 */
public class GestorArchivos {
    private static final String ARCHIVO_DAT = "MiSanidad.dat";
    
    public static Sistema cargarSistema(){
        File archivo = new File(ARCHIVO_DAT);
        if (!archivo.exists()) {
            System.out.println("No se ha encontrado .dat");
            Sistema sistema = new Sistema();
            sistema.datosPrueba(sistema);
            return sistema;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))){
            System.out.println("Datos cargados!! :)");
            return (Sistema) ois.readObject();
        } catch (Exception e) {
            System.err.println("Error al cargar: " + e.getMessage());
            return new Sistema();
        }
    }
    
    public static void guardarSistema(Sistema sistema){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO_DAT))){
            oos.writeObject(sistema);
            System.out.println("Datos guardados.");
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }
    
    public static void exportarCitasPendientesMedicoCSV(Sistema sistema, Medico medico){
        ArrayList<Cita> citas_pendientes = new ArrayList<>();
        
        for(Cita c : sistema.getCitasParaMedico(medico)){
            if(c.getEstado() == EstadoCita.CONFIRMADA){
                citas_pendientes.add(c);
            }
        }
        
        citas_pendientes.sort((c1, c2) -> c2.getFechaHora().compareTo(c1.getFechaHora()));
        
        try (PrintWriter writer = new PrintWriter(new File("citas_"+medico.getNombre()+".csv"))){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (Cita c : citas_pendientes){
                String tipoCita = c.isTelefonica() ? "Telefónica" : "Presencial";
                writer.printf(
                    c.getPaciente().getNombre()+","+
                    c.getFechaHora().format(formatter)+","+
                    tipoCita+","+
                    c.getMotivo()+","+
                    c.getCentro()+","+
                    c.getEstado()+"\n"
                );
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("Error al exportar: " + e.getMessage());
        }
    }
    
    public static void exportarMedicosConMasCitasCSV(Sistema sistema){
        List<Medico> medicos = sistema.getTodosMedicos();
        List<Cita> citas = sistema.getTodasCitas();
        List<Long> n_citas_por_medico = new ArrayList<>();
        List<Medico> medicos_con_citas = new ArrayList<>();
        
        for (int i = 0; i < medicos.size(); i++){
            Medico medico_actual = medicos.get(i);
            long contador_citas = 0;
            
            for (int j = 0; j < citas.size(); j++){
                Cita cita_actual = citas.get(j);
                
                if (cita_actual.getMedico().equals(medico_actual)){
                    if (cita_actual.getEstado() == EstadoCita.CONFIRMADA){
                        contador_citas++;
                    }
                }
            }
            
            if (contador_citas > 0){
                medicos_con_citas.add(medico_actual);
                n_citas_por_medico.add(contador_citas);
            }
        }
        
        if (medicos_con_citas.isEmpty()){
            System.err.println("Ningun medico tiene citas");
            return;
        }
        
        for (int i = 0; i < medicos_con_citas.size()-1; i++){
            for (int j = i+1; j < medicos_con_citas.size(); j++){
                if (n_citas_por_medico.get(i) < n_citas_por_medico.get(j)){
                    Medico tempMedico = medicos_con_citas.get(i);
                    medicos_con_citas.set(i, medicos_con_citas.get(j));
                    medicos_con_citas.set(j, tempMedico);
                    
                    Long tempCitas = n_citas_por_medico.get(i);
                    n_citas_por_medico.set(i, n_citas_por_medico.get(j));
                    n_citas_por_medico.set(j, tempCitas);
                }
            }
        }
        
        
        try (PrintWriter writer = new PrintWriter(new File("medicos_con_mas_citas.csv"))){
            
            for (int i = 0; i<medicos_con_citas.size(); i++){
                Medico m = medicos_con_citas.get(i);
                long n_citas = n_citas_por_medico.get(i);
                
                writer.printf(m.getDni()+","+m.getNombre()+","+n_citas+"\n");
            }
            
        } catch (FileNotFoundException e) {
            System.err.println("Error al exportar: " + e.getMessage());
        }
    }
}

package misanidad2;

import java.io.Serializable;
import java.time.LocalDate;
import javax.swing.ImageIcon;

/**
 *
 * @author alvar
 */
public class PruebaImagen extends PruebaLaboratorio implements Serializable {
    private byte[] imagenBytes;
    private String rutaImagen;
    
    public PruebaImagen(String nombre, LocalDate fecha, String centro, String informe, byte[] imagenBytes){
        super(nombre, fecha, centro, informe);
        this.imagenBytes = imagenBytes;
        this.rutaImagen = null;
    }
    
    public PruebaImagen(String nombre, LocalDate fecha, String centro, String informe, String rutaImagen){
        super(nombre, fecha, centro, informe);
        this.rutaImagen = rutaImagen;
        this.imagenBytes = null;
    }
    
    public PruebaImagen(String nombre, LocalDate fecha, String centro, String informe){
        super(nombre, fecha, centro, informe);
        this.imagenBytes = null;
        this.rutaImagen = null;
    }
    
    public byte[] getImagenBytes() {
        return imagenBytes;
    }
    
    public void setImagenBytes(byte[] imagenBytes) {
        this.imagenBytes = imagenBytes;
    }
    
    public String getRutaImagen() {
        return rutaImagen;
    }
    
    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
    
    public boolean tieneImagen() {
        return (imagenBytes != null && imagenBytes.length > 0) || (rutaImagen != null && !rutaImagen.isEmpty());
    }
    
    public ImageIcon getImageIcon() {
        if (imagenBytes != null && imagenBytes.length > 0) {
            return new ImageIcon(imagenBytes);
        }
        return null;
    }
}
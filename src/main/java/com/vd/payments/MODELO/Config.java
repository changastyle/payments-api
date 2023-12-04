package com.vd.payments.MODELO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "configuraciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Config
{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private boolean enabled;
    private String protocolo;
    private String ip;
    private String puerto;
    private String nombreProyecto;
    private String subCarpetaImagenes;
    @Transient
    private String urlVisualizacion;
    @Transient
    private String rutaFileSystem;
    private boolean linux;
    private String carpetaWeb;
    private long nextRefresh;



    public String getUrlVisualizacion()
    {
        String strPuerto = puerto;
        if(puerto == null)
        {
            strPuerto = "";
        }
        String urlCompleta = protocolo + "" + ip + "" + strPuerto + "/" + nombreProyecto +"/upload/" ;
        return urlCompleta;
    }
    public String getRutaFileSystem()
    {
        String ruta = "";
        String os = System.getProperty("os.name");

        //+ File.separator + subCarpetaImagenes
        if(os.startsWith("Windows"))
        {
            //WINDOWS:
            ruta = "C:\\xampp\\htdocs\\" + nombreProyecto+ "\\upload\\" ;
        }
        else if(os.startsWith("Mac OS X"))
        {
            ruta = "/Applications/XAMPP/xamppfiles/htdocs/" + nombreProyecto +"/upload/";
        }
        else
        {
            //LINUX:
            ruta = "/var/www/" + nombreProyecto +"/upload/";
        }

        return ruta;
    }
}

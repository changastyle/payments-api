package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vd.payments.CONTROLLERS.ConfigWS;
import io.swagger.models.auth.In;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "documentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Documento implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String urlOriginal;
    private String urlProv;
    @OneToOne()
    @JoinColumn(name = "fkInstalacion")
    @JsonIgnore
    private Instalacion instalacion;
    @Column(columnDefinition = "BOOLEAN DEFAULT true")
    private boolean activo = true;

    public Documento(String urlProv, Instalacion instalacion)
    {
        this.urlProv = urlProv;
        this.instalacion = instalacion;
    }

    public String getFull()
    {
        String urlFull = "";
        Config config = ConfigWS.dameConfigMaster();

        if (config != null)
        {
            urlFull = config.getUrlVisualizacion();
            urlFull += urlProv;
        }


        return urlFull;
    }

    @JsonIgnore
    public String getFullFS()
    {
        String urlFull = "";
        Config config = ConfigWS.dameConfigMaster();

        if (config != null)
        {
            urlFull = config.getRutaFileSystem();
            urlFull += urlProv;
        }


        return urlFull;
    }

    public int getFKInstalacion()
    {
        int fkInstalacionAsociada = -1;

        if (instalacion != null)
        {
            fkInstalacionAsociada = instalacion.getId();
        }
        return fkInstalacionAsociada;
    }

    public String getCalcularExtension()
    {
        String extension = "";

        if (urlOriginal.contains("."))
        {
            int posLastPunto = urlOriginal.lastIndexOf(".");

            if (posLastPunto > -1)
            {
                extension = urlOriginal.substring(posLastPunto + 1);
            }
        }

        return extension;
    }

    public String getPreviewDocumento()
    {
        String preview = "";
        Config config = ConfigWS.dameConfigMaster();

        if (config != null)
        {
            preview = config.getUrlVisualizacion();

            if (urlProv.endsWith(".pdf"))
            {
                preview += "pdf.png";
            }
            else if (urlProv.endsWith(".doc"))
            {
                preview += "doc.png";
            }
            else
            {
                preview += urlProv;
            }
        }


        return preview;
    }

    @Override
    public int hashCode()
    {
//        return Objects.hash(thi, instalacion.getNombre());
        return Objects.hash(this.urlProv);
    }
}

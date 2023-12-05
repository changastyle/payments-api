package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vd.payments.CONTROLLERS.ConfigWS;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


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
    private boolean activo = true;

    public Documento(String urlProv)
    {
        this.urlProv = urlProv;
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
}

package com.vd.payments.MODELO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vd.payments.CONTROLLERS.ConfigWS;
import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "fotos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Foto implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String urlProv;


    public Foto(String urlProv)
    {
        this.urlProv = urlProv;
    }

    public String getFullFoto()
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
    public String getFullFotoFS()
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

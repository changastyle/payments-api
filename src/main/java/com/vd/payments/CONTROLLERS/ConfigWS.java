package com.vd.payments.CONTROLLERS;

import com.vd.payments.XCP.NotFoundExc;
import com.vd.payments.MODELO.Config;
import com.vd.payments.REPO.ConfigRepo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "config")
public class ConfigWS
{

    @Autowired
    public static ConfigRepo configRepo;
    private static Config masterConfig;
    public static long timestampUltimaActualizacionConfiguracion = -1;
    private static long maxTiempoRefresh = 3600 * 1 * 1000;

    @Autowired
    public ConfigWS(ConfigRepo configRepo)
    {
        ConfigWS.configRepo = configRepo;
    }




    @GetMapping("findConfiguraciones")
    public static List<Config> findConfiguraciones()
    {
        List<Config> arrConfigs = configRepo.findAll();
        if(arrConfigs == null)
        {
            throw new NotFoundExc("Config no encontrada my friend");
        }

        return arrConfigs;
    }



    @GetMapping("dameConfigMaster")
    @Operation(
            summary = "RETURN MASTER CONFIG ENABLED",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Config dameConfigMaster()
    {
        Date ahora = new Date();
        long timestampActual = ahora.getTime();

        if(masterConfig == null || timestampUltimaActualizacionConfiguracion == -1 || timestampActual > (timestampUltimaActualizacionConfiguracion + ( maxTiempoRefresh )) )
        {
            masterConfig = dameConfigActual();
            timestampUltimaActualizacionConfiguracion = timestampActual;
        }

        if(masterConfig != null)
        {
            long restante = ((timestampUltimaActualizacionConfiguracion + maxTiempoRefresh) - timestampActual ) / 1000;
            masterConfig.setNextRefresh(restante);
        }
        else
        {
            throw new NotFoundExc("Config Master NOT FOUND");
        }


        return masterConfig;
    }
    @GetMapping("forzarConfig")
    public static Config forzarConfig()
    {
        ConfigWS.timestampUltimaActualizacionConfiguracion = -1;

        return dameConfigMaster();
    }
    @GetMapping("pasarANafta")
    public static Config pasarANafta()
    {
        Config config = null;

        try
        {
            // 1 -  DAME MI IP DE LA RED (NO LOCALHOST):
            InetAddress ipOBJ = InetAddress.getLocalHost();
            String ip = ipOBJ.getHostAddress();
            System.out.println("LA IP ES: " + ip);



            config = dameConfigMaster();
            if(config.isEnabled())
            {
                if(ip != null)
                {
                    config.setIp(ip);
                }
            }

            if(config != null)
            {
                if(configRepo.save(config) != null)
                {
                    forzarConfig();
                }
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return config;
    }

    private static Config dameConfigActual()
    {
        Config configuracionActual = null;

        for(Config configLoop : findConfiguraciones())
        {
            if(configLoop.isEnabled())
            {
                configuracionActual = configLoop;
                break;
            }
        }

        return configuracionActual;
    }
}

package com.vd.payments.CONTROLLERS;

import com.mysql.cj.log.Log;
import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.FacturaREPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "pdf")
public class PdfWS
{
    @Autowired
    FacturaREPO facturaREPO;

    @GetMapping("generarFactura/{facturaID}")
    public String generarFactura(@PathVariable("facturaID") int facturaID, @RequestHeader HttpHeaders headers)
    {
        // 1 - Generar la ruta del archivo dinámicamente
        LocalDateTime fechaActual = LocalDateTime.now();
        String mes = String.format("%02d", fechaActual.getMonthValue());
        String year = String.valueOf(fechaActual.getYear());

        Config config = ConfigWS.dameConfigMaster();
        String directorioDestino = config.getRutaFileSystem() + File.separator + "pdfs" + File.separator;
        //String directorioDestino = "/var/www/uploads/pdfs/";
        String nombreArchivo = facturaID + "-" + mes + "-" + year + ".pdf";
        String rutaPDFSalida = directorioDestino + nombreArchivo;
        String rutaPlantilla = "templates/factura_template.html";

        String urlFacturaGenerada = "";
        try
        {
            // 2 - Crear directorio si no existe
            File directorio = new File(directorioDestino);
            if (!directorio.exists())
            {
                directorio.mkdirs();
            }

            // 3 - Crear un documento PDF en memoria
//            PdfWriter writer = new PdfWriter(new FileOutputStream(rutaPDFSalida));
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);

            // Leer el archivo HTML de la plantilla:
            String contenidoHtml = new String(Files.readAllBytes(Paths.get(rutaPlantilla)));

            // Reemplazar cada marcador en el HTML con los valores del mapa
            Map<String, String> arrDatos = new HashMap<>();
            arrDatos.put("nombre_cliente", "MELIPALL");
            for (Map.Entry<String, String> entrada : arrDatos.entrySet())
            {
                contenidoHtml = contenidoHtml.replace("{" + entrada.getKey() + "}", entrada.getValue());
            }

            // CONVERTIR A PDF:
            try (OutputStream os = new FileOutputStream(rutaPDFSalida))
            {
//                HtmlConverter.convertToPdf(html, os);
            }

            // 7 - Cerrar el documento
//            document.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return rutaPDFSalida;
    }

    @GetMapping("convertirHTMLAPDF/{facturaID}")
    public String convertirHTMLAPDF(@PathVariable("facturaID") int facturaID, @RequestHeader HttpHeaders headers) throws Exception
    {
        // Configuración de rutas
        Config config = ConfigWS.dameConfigMaster();
        String carpetaPDFS = config.getRutaFileSystem() + File.separator + "pdfs" + File.separator;
        String carpetaWEB = config.getUrlVisualizacion() + "pdfs/";
        String carpetaTemplatesHTML = "src/main/resources/templates/";
        String rutaPDFFS = carpetaPDFS + "factura.pdf";
        String rutaPDFWEB = carpetaWEB + "factura.pdf";
        String rutaPlantillaHTML = carpetaTemplatesHTML + "factura_template.html";

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);
        Factura facturaDB = facturaREPO.getByIDN(facturaID);

        // Mapa con datos dinámicos
        HashMap<String, String> arrDatos = new HashMap<>();
        Instalacion instalacion = operadorLogeado.getInstalacion();

        if (instalacion != null)
        {
            arrDatos.put("logo_vd", instalacion.getLogo().getFull());

        }
        arrDatos.put("nombre_proveedor", "VIEW DEVS COMPANY");
        arrDatos.put("cuit_proveedor", "20-37557878-7");
        arrDatos.put("direccion_proveedor", "Frey 468");
        arrDatos.put("telefono_proveedor", "+34 654 754543");
        arrDatos.put("email_proveedor", "viewdevscompany@gmail.com");


        if (facturaDB != null)
        {
            Empresa empresaDB = facturaDB.getEmpresa();
            if (empresaDB != null)
            {
                arrDatos.put("nombre_cliente", empresaDB.getNombre());
                arrDatos.put("cuit_cliente", "CUIT:" + empresaDB.getCuit());
                arrDatos.put("direccion_cliente", empresaDB.getDireccionContacto());
                arrDatos.put("email_cliente", empresaDB.getEmailContacto());
                arrDatos.put("telefono_cliente", empresaDB.getTelefonoContacto());
                arrDatos.put("fecha_emision_factura", facturaDB.getOnlyFechaEmisionBonita());
                arrDatos.put("periodo_factura", facturaDB.getPeriodoFactura());
            }
        }
        arrDatos.put("tabla_dets_factura", crearTablaDetsFactura(facturaDB));


        // Leer la plantilla y reemplazar marcadores
        String html = leerHtmlPlantilla(rutaPlantillaHTML);
        String htmlFull = reemplazarHTMLConValores(html, arrDatos);

        // Generación del PDF con Flying Saucer
        try (OutputStream os = new FileOutputStream(rutaPDFFS))
        {
            ITextRenderer renderer = new ITextRenderer();
            // Establecer el contenido HTML
            renderer.setDocumentFromString(htmlFull);

            // Configuración adicional (si es necesario, como fuentes externas)
            renderer.layout();
            renderer.createPDF(os);
        }

        System.out.println("PDF generado exitosamente: " + rutaPDFFS);
        return htmlFull;
//        return rutaPDFWEB;
    }

    private String leerHtmlPlantilla(String rutaPlantilla) throws IOException
    {
        String contenidoHtml = "";
        try
        {
            contenidoHtml = new String(Files.readAllBytes(Paths.get(rutaPlantilla)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return contenidoHtml;
    }

    private String reemplazarHTMLConValores(String contenidoHTML, HashMap<String, String> arrDatos)
    {
        for (Map.Entry<String, String> entrada : arrDatos.entrySet())
        {
            if (entrada.getValue() != null)
            {
                contenidoHTML = contenidoHTML.replace("{" + entrada.getKey() + "}", entrada.getValue());
            }
        }
        return contenidoHTML;
    }

    public String crearTablaDetsFactura(Factura facturaRecibida)
    {
        String html = "<table class='tabla-det-factura'>";

        if (facturaRecibida != null)
        {
            html += "<thead>";
            html += "<tr>";
            html += "<th>DESCRIPCION</th>";
            html += "<th>PRECIO.UN</th>";
            html += "<th>CANTIDAD</th>";
            html += "<th>TOTAL</th>";
            html += "</tr>";
            html += "</thead>";
            html += "<tbody>";

            for (DetFactura detLoop : facturaRecibida.getArrDetsFactura())
            {
                html += "<tr>";
                html += "<td class=''> Servicio " + detLoop.getProducto().getNombre() + " - Periodo " + facturaRecibida.getPeriodoFactura() + "</td>";
                html += "<td class='center'>$" + ((int) detLoop.getImporteDetalleUnitario()) + "</td>";
                html += "<td class='center'>x" + ((int) detLoop.getCantidad()) + " Unidades </td>";
                html += "<td class='center'>$" + ((int) detLoop.getCalcularPrecioDetalle()) + "</td>";
                html += "</tr>";
            }

            html += "</tbody>";

        }

        html += "</table>";

        return html;
    }

}

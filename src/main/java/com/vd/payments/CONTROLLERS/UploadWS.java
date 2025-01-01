package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Config;
import com.vd.payments.MODELO.Documento;
import com.vd.payments.REPO.DocREPO;
import com.vd.payments.REPO.FacturaREPO;
import com.vd.payments.UTIL.serializer.MasterUtil;
import com.vd.payments.XCP.CustomException;
import com.vd.payments.XCP.NoLogeadoExc;
import com.vd.payments.XDTO.TuplaEmpresaRolDTO;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.util.Date;

import com.vd.payments.MODELO.*;
import com.vd.payments.REPO.*;

@RestController
@RequestMapping(value = "upload")
public class UploadWS
{
    private static DocREPO docREPO;
    private static FacturaREPO facturaREPO;
    private static DeletionDocREPO deletionDocREPO;
    private static final String urlVisualizacionBackground = "up/";


    @Autowired
    public UploadWS(DocREPO docREPO, FacturaREPO facturaREPO, DeletionDocREPO deletionDocREPO)
    {
        UploadWS.docREPO = docREPO;
        UploadWS.facturaREPO = facturaREPO;
        UploadWS.deletionDocREPO = deletionDocREPO;
    }

    @RequestMapping(value = "/doc/factura/{fkFactura}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    @Operation(
            summary = "Attach document to Factura",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Documento attachDocToFactura
            (
                    @RequestHeader HttpHeaders headers,
//            @RequestParam(value = "light",defaultValue = "true") boolean light,
                    @PathVariable(value = "fkFactura") int fkFactura,
                    @RequestPart("doc") @ApiParam(value = "doc", required = true) MultipartFile multiPart
            )
    {
        boolean light = false;
        Documento docAttached = uploadOnlyDoc(headers, light, multiPart);

        if (fkFactura != -1)
        {
            Factura facturaDB = facturaREPO.getByIDN(fkFactura);

            if (facturaDB != null)
            {
                facturaDB.addDocumento(docAttached);
                facturaDB.setActivo(true);
                facturaREPO.save(facturaDB);
            }
        }
        try
        {
            Thread.sleep(2500);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return docAttached;
    }

    @DeleteMapping("/doc/factura/{fkDoc}")
    @CrossOrigin
    @Operation(
            summary = "RM document from FLAG",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static boolean rmDocToFactura
            (
                    @PathVariable(value = "fkDoc") int fkDoc,
                    @RequestHeader HttpHeaders headers
            )
    {
        boolean rm = false;

        Operador operadorLogeado = LoginWS.dameOperadorLogeado(headers);

        // 1 - COMPRUEBA QUE EL OPERADOR ESTE LOGEADO Y QUE LA FACTURA PERTENEZC
        if (operadorLogeado != null)
        {
            Documento docDB = docREPO.getByIDN(fkDoc);
            int fkInstacionDocumento = docDB.getFKInstalacion();
            int fkInstalacionOpLogeado = operadorLogeado.getFKInstalacion();
            if (fkInstalacionOpLogeado == fkInstacionDocumento)
            {
                if (docDB != null)
                {
                    docDB.setActivo(false);
                    docDB = docREPO.save(docDB);

                    if (docDB != null)
                    {
                        DeletionDoc deletionDoc = new DeletionDoc(docDB, operadorLogeado);
                        deletionDocREPO.save(deletionDoc);
                        rm = true;
                    }
                    else
                    {
                        throw new CustomException("NO SE PUDO DESACTIVAR EL DOCUMENTO: " + fkDoc);
                    }
                }
            }
            else
            {
                throw new CustomException("EL OPERADOR LOGEADO [" + fkInstalacionOpLogeado + "] NO PERTENECE A LA MISMA INSTALACION DEL DOCUMENTO [" + docDB.getId() + "]");
            }
        }
        else
        {
            throw new NoLogeadoExc("EL OPERADOR NO ESTA LOGEADO");
        }
        return rm;
    }

    @RequestMapping(value = "/doc/", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    @Operation(
            summary = "Upload Document return ID of database to attach in another process related to another Entity (NOT HERE)",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Documento uploadOnlyDoc
            (
                    @RequestHeader HttpHeaders headers,
                    @RequestParam(value = "light", defaultValue = "true") boolean light,
                    @RequestPart("doc") @ApiParam(value = "doc", required = true) MultipartFile multiPart
            )
    {
        Documento documentoRTA = null;

        String subCarpeta = "";

        // 0 - VALIDAR QUE EXISTA UN USUARIO LOGEADO Y VERIFICAR SU FK INSTALACION:
        int fkIntalacionOperadorLogeado = LoginWS.getFKInstalacionOperadorLogeado(headers);
        if (fkIntalacionOperadorLogeado != -1)
        {
            // 1 - COMPROBAR QUE VENGA JUSTAMENTE UN ARCHIVO EN EL MULTIPART QUE TENGA NOMBRE Y DEMAS VALIDACIONES:
            if (multiPart != null)
            {
                if (!multiPart.isEmpty())
                {
                    if (multiPart.getName() != null)
                    {
                        if (multiPart.getName().length() > 0)
                        {
                            // 2 - OBTENGO EL NOMBRE COMPLETO Y EL SIMPLE - MAS EL PESO DEL ARCHIVO:
                            String originalSimpleName = multiPart.getOriginalFilename();
                            String originalFullName = multiPart.getName();
                            long originalSize = multiPart.getSize();

                            System.out.println("UPLOADING DOC: " + originalFullName);

                            // 3 - ENVIO LOS DATOS PARA SUBIR EL ARCHIVO Y ME DEVUELVE LA URL DONDE SE SUBIO:
                            documentoRTA = subirArchi(headers, multiPart, subCarpeta, light);

                            // 4 - GUARDO EL DOCUMENTO EN DB:
                            documentoRTA = docREPO.save(documentoRTA);

                            System.out.println("DOC UPLOADED SUCCESS: " + originalSimpleName + " -> " + originalSize + " KB");
                        }
                    }
                }
            }
        }

        return documentoRTA;
    }


    private static Documento subirArchi(HttpHeaders headers, MultipartFile multiPart, String subCarpeta, boolean deboAchicar)
    {
        //1 - FOTO DE RESPUESTA - NO VA MAS URL
        Documento docRTA = new Documento();
        TuplaEmpresaRolDTO tuplaOpInstalacion = LoginWS.dameTuplaOperadorLogeado(headers);

        if (tuplaOpInstalacion != null)
        {
            Instalacion instalacion = tuplaOpInstalacion.getInstalacion();

            if (instalacion != null)
            {
                docRTA.setInstalacion(instalacion);
                // 2 - RUTA A GUARDAR DISCO C://
                String rutaCarpetaEnDisco = "";

                // 3 - URL DE VISUALIZACION MEDIANTE PUERTO 80:
                String urlCarpetaVisualizacion = "";
                String nombreFullArchivoOriginal = "";

                // 4 - POPULO LAS VARIABLES DE CARPETA EN DISCO Y DE VISUALIZACION PUERTO 80:
                Config config = ConfigWS.dameConfigMaster();
                if (config != null)
                {
                    if (subCarpeta != null && subCarpeta.length() > 0)
                    {
                        rutaCarpetaEnDisco = config.getRutaFileSystem() + File.separator + subCarpeta;
                        urlCarpetaVisualizacion = config.getUrlVisualizacion() + "/" + subCarpeta;
                    }
                    else
                    {
                        rutaCarpetaEnDisco = config.getRutaFileSystem();
                        urlCarpetaVisualizacion = config.getUrlVisualizacion();
                    }
                }

                // 4 - GET FK INSTALACION FROM LOGED USER:
                int fkInstalacion = LoginWS.getFKInstalacionOperadorLogeado(headers);

                if (fkInstalacion != -1)
                {
                    // 5 - COMPRUEBO QUE LA CARPETA EN DISCO EXISTA - SINO LA CREO:
                    MasterUtil.verifyFolderExist(rutaCarpetaEnDisco);

                    // 6 - CREO UN TIMESTAMP PARA PONERLE NOMBRE UNICO:
                    LocalDateTime ahora = LocalDateTime.now();

                    // 7 - SI TENGO UN ARCHIVO VALIDO PROCESO SU NOMBRE Y LA EXTENSION:
                    if (multiPart != null)
                    {
                        String nombreArchivoOriginal = multiPart.getOriginalFilename();
                        docRTA.setUrlOriginal(nombreArchivoOriginal);

                        if (nombreArchivoOriginal.contains("."))
                        {
                            int punto = nombreArchivoOriginal.lastIndexOf(".");
                            String extensionArchivo = (String) multiPart.getOriginalFilename().subSequence(punto, nombreArchivoOriginal.length());
                            nombreArchivoOriginal = nombreArchivoOriginal.substring(0, punto);

                            if (!multiPart.isEmpty())
                            {
                                try
                                {
                                    // 9 - EL NOMBRE FULL DEL ARCHIVO EN DISCO:
                                    String nuevoNombreFS = MasterUtil.formatearLDTForFiles(ahora) + "-" + MasterUtil.generarCodigoAleatorio(4) + "[" + fkInstalacion + "]" + extensionArchivo;
                                    String nuevoNombreFullFS = rutaCarpetaEnDisco + File.separator + nuevoNombreFS;


                                    // 10 - COPIO EL ARCHIVO DEL TMP A LA UBICACION EN DISCO:
                                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(nuevoNombreFullFS)));
                                    int sizeEnDisco = FileCopyUtils.copy(multiPart.getInputStream(), stream);

                                    System.out.println("rutaCarpetaEnDisco: " + rutaCarpetaEnDisco);
                                    System.out.println("nuevoNombreFullFS: " + nuevoNombreFullFS + " | " + sizeEnDisco + "KB");
                                    if (sizeEnDisco > 0)
                                    {

                                        boolean archivoEsFoto = MasterUtil.calcularSiArchivoEsFoto(nuevoNombreFullFS);
                                        if (deboAchicar && archivoEsFoto)
                                        {
                                            File archivoYaSubido = new File(nuevoNombreFullFS);
                                            if (archivoYaSubido != null)
                                            {
                                                long sizeFoto = multiPart.getSize();

                                                docRTA.setUrlProv(nuevoNombreFS);

                                                String dimensionesSTR = saberTamanoFoto(docRTA);

                                                if (dimensionesSTR != null && dimensionesSTR.length() > 0)
                                                {
                                                    System.out.println("PREV SUBIR ARCHI : " + nuevoNombreFS + " (" + dimensionesSTR + ") -> " + sizeFoto + " KB");

                                                    int largo = Integer.parseInt(MasterUtil.separatePipeByPos(dimensionesSTR, 0));
                                                    int ancho = Integer.parseInt(MasterUtil.separatePipeByPos(dimensionesSTR, 1));

                                                    Thread.sleep(2000);

                                                    String nuevoNombreDisco = achicarFoto(archivoYaSubido, ancho, largo, nuevoNombreFullFS, subCarpeta);

                                                    if (nuevoNombreDisco != null)
                                                    {
                                                        nuevoNombreDisco = nuevoNombreDisco.replace("\\", "/");
                                                        int posUltimoSlash = nuevoNombreDisco.lastIndexOf("/");
                                                        if (posUltimoSlash != -1)
                                                        {
                                                            String nombreOnlyArchivo = nuevoNombreDisco.substring((posUltimoSlash + 1));
                                                            String nuevoNombreWeb = urlVisualizacionBackground + "/" + subCarpeta + "/" + nombreOnlyArchivo;

                                                            // 11 - DEVUELVO UNA URL CON NOMBRE PROVISORIO Y NOMBRE FULL DE VISUALIZACION:
                                                            docRTA.setUrlProv(nombreOnlyArchivo);
                                                        }
                                                    }
                                                }

                                                System.out.println("SUBIDA CORRECTA: " + nuevoNombreFS);

                                                // MUEVO EL ARCHIVO HEAVY A UNA CARPETA HEAVY: si se llaman diferente: (EJEMPLO: .PNG Y .JPG)
                                                boolean condicionSeanDistintosArchivos = !nuevoNombreFullFS.endsWith(extensionArchivo);
                                                if (condicionSeanDistintosArchivos)
                                                {
                                                    String rutaCarpetaHeavy = "HEAVY";
                                                    int posLasSlash = nuevoNombreFullFS.lastIndexOf(File.separator);
                                                    if (posLasSlash != -1)
                                                    {
                                                        String rutaCarpetaHastaElMomento = nuevoNombreFullFS.substring(0, posLasSlash);
                                                        String rutaCarpetaHeavyFull = rutaCarpetaHastaElMomento + File.separator + rutaCarpetaHeavy;
                                                        File carpetaHeavy = new File(rutaCarpetaHeavyFull);

                                                        // Verificar si el archivo existe y si la carpeta destino es una carpeta
                                                        if (!carpetaHeavy.exists())
                                                        {
                                                            carpetaHeavy.mkdir();
                                                        }
                                                        File nuevoArchivo = new File(carpetaHeavy.getAbsolutePath() + File.separator + archivoYaSubido.getName());

                                                        // Mover el archivo utilizando el método renameTo()
                                                        if (archivoYaSubido.renameTo(nuevoArchivo))
                                                        {
                                                            System.out.println("ARCHIVO HEAVY MOVIDO A CARPETA : " + rutaCarpetaHeavy);
                                                        }
                                                    }
                                                }
                                            }


                                        }
                                        else
                                        {
                                            String nuevoNombreDisco = nuevoNombreFullFS;
                                            nuevoNombreDisco = nuevoNombreDisco.replace("\\", "/");
                                            nuevoNombreDisco = nuevoNombreDisco.replace("//", "/");
                                            int posUltimoSlash = nuevoNombreDisco.lastIndexOf("/");
                                            String nombreOnlyArchivo = nuevoNombreDisco.substring((posUltimoSlash + 1));
                                            docRTA.setUrlProv(nombreOnlyArchivo);
                                        }
                                        //                            System.out.println("URL SALIDA: " + fotoCargada.toString());
                                    }
                                }
                                catch (Exception e)
                                {
                                    System.out.println("ERROR: subiendo archivo " + nombreArchivoOriginal);
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                }
                else
                {
                    throw new NoLogeadoExc("NO HAY FK INSTALACION PARA EL USUARIO LOGEADO");
                }
            }
        }


        return docRTA;
    }

    public static String saberTamanoFoto(Documento doc)
    {
        String rta = "";
        try
        {
            if (doc != null)
            {
                File archi = new File(doc.getFullFS());

                if (archi != null)
                {
                    BufferedImage image = ImageIO.read(archi);

                    int largo = image.getHeight();
                    int ancho = image.getWidth();

                    // AJUSTE POR DESPROPORCIONES:
//                    int aux = 0;
//                    if(ancho > largo)
//                    {
//                        aux = ancho;
//                        ancho = largo;
//                        largo = aux;
//                    }
                    rta = largo + "|" + ancho;
                }
            }
        }
        catch (Exception e)
        {
            File archi = new File(doc.getFull());
            System.out.println("ERROR : CONOCIENDO TAMANO FOTO : " + archi.getAbsolutePath());
            e.printStackTrace();
        }
        return rta;
    }

    public static String achicarFoto(File imagen, int ancho, int alto, String nombreArchivoSaliente, String rutaCarpeta)
    {
        String nuevoNombreDisco = "";
        try
        {
            // 1 - CREO LA BUFFERED IMAGE:
            BufferedImage image = ImageIO.read(imagen);


            //"comprimidas/"
            File carpetaFotosComprimidas = new File(rutaCarpeta);
            if (carpetaFotosComprimidas != null)
            {
                if (!carpetaFotosComprimidas.exists())
                {
                    carpetaFotosComprimidas.mkdir();
                }

                if (image != null)
                {
                    Date timestamp = new Date();
//                    String nombreArchivo = carpetaFotosComprimidas.getAbsolutePath() + File.separator +  nombreArchivoSaliente;

                    int posUltimoPunto = nombreArchivoSaliente.lastIndexOf(".");

                    if (posUltimoPunto != -1)
                    {
                        nuevoNombreDisco = nombreArchivoSaliente.substring(0, posUltimoPunto) + ".jpg";
                    }

                    System.out.println("ACHICANDO ANCHO - ALTO : " + ancho + " x " + alto);

                    if (ancho > alto)
                    {
                        Thumbnails.of(image)
                                .size(ancho, alto)
                                .outputQuality(0.5)
                                .outputFormat("jpg")
                                .rotate(0)
                                .toFile(new File(nuevoNombreDisco));
                    }
                    else
                    {

                        Thumbnails.of(image)
                                .size(ancho, alto)
                                .outputQuality(0.5)
                                .outputFormat("jpg")
                                .toFile(new File(nuevoNombreDisco));
                    }

                }
            }


        }
        catch (Exception e)
        {
            System.out.println("ERROR: " + e);
        }


        return nuevoNombreDisco;
    }

    @CrossOrigin
    @PostMapping("/rotate/foto/{id}")
    @Operation(
            summary = "Rota una foto pasando su ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public static Documento rotarFoto(@PathVariable() int id)
    {
        Documento documentoDB = null;
        String rutaImagenSalida = "";
        if (id != -1)
        {
            documentoDB = docREPO.getById(id);

            if (documentoDB != null)
            {
                String fullFoto = documentoDB.getFullFS();
                int posLastSlash = fullFoto.lastIndexOf("\\");

                if (posLastSlash != -1)
                {
                    String carpetaFS = fullFoto.substring(0, posLastSlash);

                    String nuevoNombreProv = "rotada-" + documentoDB.getUrlProv();
                    String rutaImagenEntrada = carpetaFS + File.separator + documentoDB.getUrlProv();
                    rutaImagenSalida = carpetaFS + File.separator + nuevoNombreProv;

                    try
                    {
                        BufferedImage image = ImageIO.read(new File(rutaImagenEntrada));

                        double angle = 90;

                        int wAux = image.getWidth();
                        int hAux = image.getHeight();

                        int w = hAux;
                        int h = wAux;


                        BufferedImage rotated = new BufferedImage(w, h, image.getType());
                        Graphics2D graphic = rotated.createGraphics();
                        graphic.translate((hAux - wAux) / 2, (hAux - wAux) / 2);
                        graphic.rotate(Math.PI / 2, hAux / 2, wAux / 2);
                        graphic.drawImage(image, null, 0, 0);
                        graphic.dispose();


                        File outputfile = new File(rutaImagenSalida);
                        ImageIO.write(rotated, "jpg", outputfile);

                        documentoDB.setUrlProv(nuevoNombreProv);
                        documentoDB = docREPO.save(documentoDB);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        return documentoDB;
    }
}

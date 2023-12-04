package com.vd.payments.CONTROLLERS;

import com.vd.payments.MODELO.Config;
import com.vd.payments.MODELO.Foto;
import com.vd.payments.REPO.FotoRepo;
import io.swagger.annotations.ApiParam;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.awt.Graphics2D;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;


@RestController
@RequestMapping(value = "upload")
public class UploadWS
{
    private static FotoRepo fotoRepo;
    private static final String rutaFileSystem = "fs/";
    private static final String urlVisualizacionBackground = "backgrounds/";

    @Autowired
    public UploadWS(FotoRepo fotoRepo)
    {
        UploadWS.fotoRepo = fotoRepo;
    }


    @GetMapping(value = "/foto/attach/{codComp}/{idFoto}")
    @CrossOrigin
    public static Foto attach
    (
            @PathVariable() String codComp,
            @PathVariable() int idFoto
    )
    {
        Foto fotoRTA = null;

        fotoRTA = fotoRepo.getById(idFoto);
//        SubComponente subComponenteDB = subComponenteRepo.getSubComponenteByNombre(codComp);
//
//        if(fotoRTA != null)
//        {
//            if(subComponenteDB != null)
//            {
//                subComponenteDB.setFoto(fotoRTA);
//                subComponenteRepo.save(subComponenteDB);
//            }
//        }

        return fotoRTA;
    }
    @RequestMapping(value = "/foto/", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CrossOrigin
    public static Foto uploadOnlyFotoReturnURLToAttachToComponent
    (
            @RequestParam(value = "light",defaultValue = "true") boolean light,
            @RequestPart("foto") @ApiParam(value = "foto", required = true) MultipartFile fileUploaded
    )
    {
        Foto fotoRTA = null;

        String subCarpeta = "";

        if (fileUploaded != null)
        {
            if (!fileUploaded.isEmpty())
            {
                if (fileUploaded.getName() != null)
                {
                    if (fileUploaded.getName().length() > 0)
                    {
                        String nameFoto = fileUploaded.getOriginalFilename();

                        System.out.println("UPLOADING FOTO " + fileUploaded.getName());

                        //1 - SUBO EL ARCHIVO Y OBTENGO LA URL:
                        fotoRTA = subirArchi(fileUploaded, subCarpeta, light);

                        fotoRTA = fotoRepo.save(fotoRTA);

                        System.out.println("FOTO UPLOADED SUCCESS: " + nameFoto + " -> " + fileUploaded.getSize() + " KB");
                    }
                }
            }
        }
       return fotoRTA;
    }
//    @RequestMapping(value = "/foto/componente", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public static SubComponente uploadFotoToComponent
//            (
//                    @RequestParam(value = "codComponente") String codComponente,
//                    @RequestParam(value = "light",defaultValue = "true") boolean light,
//                    @RequestPart("file") @ApiParam(value = "File", required = true) MultipartFile fileUploaded
//            )
//    {
//        String subCarpeta = "";
//        Foto fotoUploaded = null;
//
//        if (codComponente != null)
//        {
//                if (fileUploaded != null)
//                {
//                    if (!fileUploaded.isEmpty())
//                    {
//                        if (fileUploaded.getName() != null)
//                        {
//                            if (fileUploaded.getName().length() > 0)
//                            {
//                                String nameFoto = fileUploaded.getOriginalFilename();
//
//                                System.out.println("UPLOADING FOTO " + fileUploaded.getName());
//
//                                //1 - SUBO EL ARCHIVO Y OBTENGO LA URL:
//                                fotoUploaded = subirArchi(fileUploaded, subCarpeta , light);
//
//                                System.out.println("FOTO UPLOADED SUCCESS: " + nameFoto + " -> " + fileUploaded.getSize() + " KB");
////                                fotoUploaded.setFkComponente(fkComponente);
//
//                                // AGREGO LA FOTO AL SITIO QUE LE CORRESPONDA POR EL FK SITIO:
//                                subComponenteDB.setFoto(fotoUploaded);
//                            }
//                        }
//                    }
//                }
//
//        }
//
//        return subComponenteGuardado;
//    }
//    @RequestMapping(value="/foto", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public static Sitio uploadFoto
//    (
//            @RequestParam(value = "fkSitio") int fkSitio,
//            @RequestParam(value = "fkComponente") int fkComponente,
//            @RequestPart("file") @ApiParam(value="File", required=true) List<MultipartFile> arrFiles
//    )
//    {
//        Sitio sitioDB = null;
//        Sitio sitioGuardado = null;
//        String subCarpeta = "";
//        Foto fotoUploaded = null;
//        Componente componenteDB = null;
//
//        if(fkComponente != -1)
//        {
////            sitioDB = sitioRepo.getById(fkSitio);
//            componenteDB = componenteRepo.getById(fkComponente);
//
//            if(componenteDB != null)
//            {
//                System.out.println("COMPONENTE DB: " + componenteDB.getNombre() );
//
//                for(MultipartFile fileLoop : arrFiles)
//                {
//                    if(fileLoop != null)
//                    {
//                        if(!fileLoop.isEmpty() )
//                        {
//                            if(fileLoop.getName()!= null)
//                            {
//                                if(fileLoop.getName().length() > 0)
//                                {
//                                    String nameFoto = fileLoop.getOriginalFilename();
//
//                                    System.out.println("UPLOADING FOTO " + fileLoop.getName());
//
//                                    //1 - SUBO EL ARCHIVO Y OBTENGO LA URL:
//                                    fotoUploaded  = subirArchi(fileLoop,subCarpeta);
//
//                                    System.out.println("FOTO UPLOADED SUCCESS: " + nameFoto + " -> " + fileLoop.getSize() + " KB");
//                                    fotoUploaded.setFkComponente(fkComponente);
//
//                                    // AGREGO LA FOTO AL SITIO QUE LE CORRESPONDA POR EL FK SITIO:
//                                    sitioDB.set(fotoUploaded);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                sitioGuardado = sitioRepo.save(sitioDB);
//            }
//        }
//
//        return sitioGuardado;
//    }


    private static Foto subirArchi(MultipartFile file, String subCarpeta , boolean deboAchicar)
    {
        //1 - FOTO DE RESPUESTA - NO VA MAS URL
        Foto fotoCargada = new Foto();

        // 2 - RUTA A GUARDAR DISCO C://
        String rutaCarpetaEnDisco = "";

        // 3 - URL DE VISUALIZACION MEDIANTE PUERTO 80:
        String urlCarpetaVisualizacion = "";

        // 4 - POPULO LAS VARIABLES DE CARPETA EN DISCO Y DE VISUALIZACION PUERTO 80:
        Config config = ConfigWS.dameConfigMaster();
        if (config != null)
        {
            rutaCarpetaEnDisco = config.getRutaFileSystem() + File.separator + subCarpeta;
            urlCarpetaVisualizacion = config.getUrlVisualizacion() + "/" + subCarpeta;
        }

        // 5 - COMPRUEBO QUE LA CARPETA EN DISCO EXISTA:
        File carpetaDondeLoGuardo = new File(rutaCarpetaEnDisco);
        if (!carpetaDondeLoGuardo.exists())
        {
            carpetaDondeLoGuardo.mkdir();
        }

        // 6 - CREO UN TIMESTAMP PARA PONERLE NOMBRE UNICO:
        Date ahora = new Date();


        // 7 - SI TENGO UN ARCHIVO VALIDO PROCESO SU NOMBRE Y LA EXTENSION:
        if (file != null)
        {
            String nombreArchivo = file.getOriginalFilename();
            if (nombreArchivo.contains("."))
            {
                int punto = nombreArchivo.lastIndexOf(".");
                String extensionArchivo = (String) file.getOriginalFilename().subSequence(punto, nombreArchivo.length());
                nombreArchivo = nombreArchivo.substring(0, punto);

                if (!file.isEmpty())
                {
                    // 8 - AGREGO ULTIMOS 4 DIGITOS DEL TIMESTAMP AL NOMBRE DEL ARCHIVO ASI ES UNICO:
                    String timestamp = "" + ahora.getTime();
                    timestamp = timestamp.substring((timestamp.length() - 4));

                    try
                    {

                        // 9 - EL NOMBRE FULL DEL ARCHIVO EN DISCO:
                        String nombreProvisorioArchivoEnDisco = nombreArchivo.toLowerCase() + "" + timestamp + extensionArchivo;
                        String nombreFullArchivoEnDisco = rutaCarpetaEnDisco + File.separator + nombreProvisorioArchivoEnDisco;


                        // 10 - COPIO EL ARCHIVO DEL TMP A LA UBICACION EN DISCO:
                        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(new File(nombreFullArchivoEnDisco)));
                        int sizeEnDisco = FileCopyUtils.copy(file.getInputStream(), stream);

                        System.out.println("carpeta en disco: " + rutaCarpetaEnDisco);
                        System.out.println("nombreFullArchivoEnDisco: " + nombreFullArchivoEnDisco + " | " + sizeEnDisco + "KB");

                        if (sizeEnDisco > 0)
                        {

                            File fotoYaCargada = new File(nombreFullArchivoEnDisco);

                            if(deboAchicar)
                            {
                                if (fotoYaCargada != null)
                                {
                                    String nameFoto = nombreProvisorioArchivoEnDisco;
                                    long sizeFoto = file.getSize();
                                    fotoCargada.setUrlProv(nameFoto);
                                    String dimensionesSTR = saberTamanoFoto(fotoCargada);

                                    if (dimensionesSTR != null && dimensionesSTR.length() > 0)
                                    {
                                        System.out.println("PREV SUBIR ARCHI : " + nameFoto + " (" + dimensionesSTR + ") -> " + sizeFoto + " KB");
                                        int posPipe = dimensionesSTR.indexOf("|");
                                        String anchoSTR = dimensionesSTR.substring(posPipe + 1);
                                        String largoSTR = dimensionesSTR.substring(0, posPipe);
                                        int ancho = Integer.parseInt(anchoSTR);
                                        int largo = Integer.parseInt(largoSTR);
                                        Thread.sleep(2000);
                                        String nuevoNombreDisco = achicarFoto(fotoYaCargada, ancho, largo, nombreFullArchivoEnDisco, subCarpeta);

                                        if (nuevoNombreDisco != null)
                                        {
                                            nuevoNombreDisco = nuevoNombreDisco.replace("\\", "/");
                                            int posUltimoSlash = nuevoNombreDisco.lastIndexOf("/");
                                            if (posUltimoSlash != -1)
                                            {
                                                String nombreOnlyArchivo = nuevoNombreDisco.substring((posUltimoSlash + 1));
                                                String nuevoNombreWeb = urlVisualizacionBackground + "/" + subCarpeta + "/" + nombreOnlyArchivo;

                                                // 11 - DEVUELVO UNA URL CON NOMBRE PROVISORIO Y NOMBRE FULL DE VISUALIZACION:
                                                fotoCargada = Foto.builder().urlProv(nombreOnlyArchivo).build();
                                            }
                                        }
                                    }


                                    System.out.println("SUBIDA CORRECTA: " + nombreProvisorioArchivoEnDisco);
                                }
                            }
                            else
                            {
                                String nuevoNombreDisco = nombreFullArchivoEnDisco;
                                nuevoNombreDisco = nuevoNombreDisco.replace("\\", "/");
                                int posUltimoSlash = nuevoNombreDisco.lastIndexOf("/");
                                String nombreOnlyArchivo = nuevoNombreDisco.substring((posUltimoSlash + 1));
                                fotoCargada = Foto.builder().urlProv(nombreOnlyArchivo).build();
                            }
//                            System.out.println("URL SALIDA: " + fotoCargada.toString());
                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("ERROR: subiendo archivo " + nombreArchivo);
                        e.printStackTrace();
                    }
                }
            }

        }
        return fotoCargada;
    }

    public static String saberTamanoFoto(Foto foto)
    {
        String rta = "";
        try
        {
            if (foto != null)
            {
                File archi = new File(foto.getFullFotoFS());

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
            File archi = new File(foto.getFullFoto());
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
                                .rotate(90)
                                .toFile(new File(nuevoNombreDisco));
                    } else
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

//    @GetMapping("/rotar/{codComponent}")
//    @CrossOrigin
//    public static Foto rotarFotoByCodComponent(@PathVariable() String codComponent)
//    {
//        Foto fotoRta = null;
//
//        SubComponente subComponenteDB = subComponenteRepo.getSubComponenteByNombre(codComponent);
//
//        if(subComponenteDB != null)
//        {
//            Foto fotoAsociadaComponent = subComponenteDB.getFoto();
//            if(fotoAsociadaComponent != null)
//            {
//                fotoRta = rotarFoto(fotoAsociadaComponent.getId());
//            }
//        }
//        return fotoRta;
//    }
    @CrossOrigin
    @PostMapping("/rotary/fotox/{id}")
    public static Foto rotarFoto(@PathVariable()int id)
    {
        Foto fotoDB = null;
        String rutaImagenSalida = "";
        if (id != -1)
        {
            fotoDB = fotoRepo.getById(id);

            if(fotoDB != null)
            {
                String fullFoto = fotoDB.getFullFotoFS();
                int posLastSlash = fullFoto.lastIndexOf("\\");

                if(posLastSlash != -1)
                {
                    String carpetaFS = fullFoto.substring(0, posLastSlash);

                    String nuevoNombreProv = "rotada-" + fotoDB.getUrlProv();
                    String rutaImagenEntrada = carpetaFS + File.separator + fotoDB.getUrlProv();
                    rutaImagenSalida = carpetaFS + File.separator + nuevoNombreProv;

                    try
                    {
                        BufferedImage image = ImageIO.read(new File(rutaImagenEntrada));

                        double angle = 90;

                        int wAux= image.getWidth();
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

                        fotoDB.setUrlProv(nuevoNombreProv);
                        fotoDB = fotoRepo.save(fotoDB);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        return fotoDB;
    }















    //    @PostMapping(name="/uploadMany")
//    public static List<Foto> uploadMany
//    (
//            @RequestParam(value="fotos") MultipartFile[] files,
//            @RequestParam(value="subCarpeta", required = false ,defaultValue = "backgrounds") String subCarpeta
//    )
//    {
//        List<Foto> listadoUrls = new ArrayList<Foto>();
//
//
//        System.out.println("recibí (" + files.length + ") archivos para subir: " + files.length);
//
//        // 1 - POR CADA MULTIPART FILE, LO SUBO Y GUARDO SU URL:
//        for(MultipartFile multipartFile : files)
//        {
//            Foto fotoLoop  = subirArchi(multipartFile , subCarpeta);
//            listadoUrls.add(fotoLoop);
//        }
//
//
//
//        return listadoUrls;
//    }

//    @RequestMapping(value="/uploadOne", method=RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public static Foto uploadOne
//            (
//                    @RequestPart("file") @ApiParam(value="File", required=true) List<MultipartFile> arrFiles
////        @RequestParam MultipartFile file
////        @RequestParam(value="subCarpeta", required = false ,defaultValue = "backgrounds") String subCarpeta
//            )
//    {
//
//        System.out.println("TEST MULTIPART MULTIPLE : " + arrFiles.size());
//
//        for(MultipartFile multiLoop: arrFiles)
//        {
//            System.out.println("ARCHII: " + multiLoop.getOriginalFilename());
//        }
//        String subCarpeta = "x";
////            System.out.println("UPLOAD " +  subCarpeta);
//        Foto fotoCargada = new Foto();
////
////            if(file != null)
////            {
////                if(!file.isEmpty() )
////                {
////                    if(file.getName()!= null)
////                    {
////                        if(file.getName().length() > 0)
////                        {
////                            System.out.println("subiendo archivo: " + file.getName());
////
////                            //1 - SUBO EL ARCHIVO Y OBTENGO LA URL:
//////                        urlSalida = subirArchi(file,subCarpeta);
////
////                            fotoCargada  = subirArchi(file,subCarpeta);
////                        }
////                    }
////                }
////            }
//
//        return fotoCargada;
//    }
}

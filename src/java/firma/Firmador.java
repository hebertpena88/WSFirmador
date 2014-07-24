/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package firma;

import CryptoServerAPI.CryptoServerException;
import CryptoServerCXI.CryptoServerCXI;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Admin
 */
@WebService(serviceName = "Firmador")
public class Firmador {
private String ip;
private String usuario;
private String password;
private String grupo;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "Firmar")
    public Booleano Firmar(@WebParam(name = "data") String data,String nombreLlave) {
        Booleano respuesta = new Booleano();
        CXI cxi = new CXI();
        try {
            
            
            String mensaje="";
            
            int timeout;
            
            ObtenerDatosDeConfguracion();
            mensaje =cxi.Conectar(usuario,password, ip);
            if(!mensaje.equals(""))
            {
                respuesta = new Booleano(mensaje);
                return respuesta;
            }
            
            CryptoServerCXI.Key llave = cxi.ObtenerLlave(nombreLlave, grupo);
            if(llave == null)
            {
                respuesta = new Booleano("No se pudo encontrar la llave para realizar el firmado,"
                        + " Por favor, verifique que existe y que se encuentra dentro del grupo del usuario"
                        + " que se encuentra logueado");
                return respuesta;
            }
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            String base64 = encoder.encode(data.getBytes());
            byte[] firma = cxi.FirmarCadena(llave, data.getBytes());
            
            String str =encoder.encode(firma);
             
            
            respuesta = new Booleano();
            respuesta.setPeticionCorrecta(true); 
            respuesta.setFirma(str);
            
            
        } 
        catch(Exception ee)
        {
             respuesta = new Booleano("Error durante el proceso: " + ee.getMessage());
             return respuesta;
        }
        finally
        {
            cxi.CerrarSesion();
        }
        return respuesta;
    }
    
    
     @WebMethod(operationName = "FirmarBulk")
    public BooleanoBulk FirmarBulk(@WebParam(name = "data") List<String> data,String nombreLlave) {
        BooleanoBulk respuesta = new BooleanoBulk();
        CXI cxi = new CXI();
        try {
            
            
            String mensaje="";
            
            int timeout;
            
            ObtenerDatosDeConfguracion();
            mensaje =cxi.Conectar(usuario,password, ip);
            List<byte[]> dataBytes = new ArrayList<>();
            List<String> firmaBase64 = new ArrayList<>();
            if(!mensaje.equals(""))
            {
                respuesta = new BooleanoBulk(mensaje);
                return respuesta;
            }
            
            CryptoServerCXI.Key llave = cxi.ObtenerLlave(nombreLlave, grupo);
            if(llave == null)
            {
                respuesta = new BooleanoBulk("No se pudo encontrar la llave para realizar el firmado,"
                        + " Por favor, verifique que existe y que se encuentra dentro del grupo del usuario"
                        + " que se encuentra logueado");
                return respuesta;
            }
            
            for(int i=0;i< data.size(); i ++)
            {
                dataBytes.add(data.get(i).getBytes());
            }
            
            byte[][] firma = cxi.FirmarCadenaBulk(llave, dataBytes);
            sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
            for(int ii=0; ii < data.size(); ii++)
            {
              firmaBase64.add(encoder.encode(firma[ii]));
            }
            
             
            
            respuesta = new BooleanoBulk();
            respuesta.setPeticionCorrecta(true); 
            respuesta.setFirma(firmaBase64);
            
            
        } 
        catch(Exception ee)
        {
             respuesta = new BooleanoBulk("Error durante el proceso: " + ee.getMessage());
             return respuesta;
        }
        finally
        {
            cxi.CerrarSesion();
        }
        return respuesta;
    }
    
    
    
    private void ObtenerDatosDeConfguracion()
    {
        try {
            DocumentBuilderFactory dbFactory ;
            DocumentBuilder dBuilder ;
            Document doc ;
            String ruta2="";
           
            ruta2= System.getProperty("user.dir") +"/config.xml";
            
            dbFactory = DocumentBuilderFactory.newInstance();
            dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(ruta2);
            
           ip= doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("ip").getNodeValue();
           usuario= doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("usuario").getNodeValue();
           password= doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("password").getNodeValue();
           grupo= doc.getElementsByTagName("Configuracion").item(0).getAttributes().getNamedItem("grupo").getNodeValue();
           
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Firmador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(Firmador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Firmador.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

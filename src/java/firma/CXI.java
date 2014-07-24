/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package firma;
import CryptoServerAPI.CryptoServerException;
import CryptoServerAPI.CryptoServerUtil;
import static CryptoServerAPI.CryptoServerUtil.concat;
import static CryptoServerAPI.CryptoServerUtil.copyOf;
import CryptoServerCXI.CryptoServerCXI;
import CryptoServerCXI.CryptoServerCXI.KeyAttributes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author dotNet
 */
public class CXI {
    private CryptoServerCXI cxi;
    private Boolean Estatus;
    private KeyAttributes[] lista;
    public String Conectar(String usuario,String password,String ip)
    {
        try {
             String mensaje = Validar(usuario, password, ip);
            if(!mensaje.equals("") )
                return mensaje;

            mensaje =Inicializar(ip);
            if(!mensaje.equals("") )
                return mensaje;
            cxi.setTimeout(20000);
            cxi.logonPassword(usuario, password);
             
            Estatus=true;
            return "";
            
            
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error en la autenticacion, verifique que el usuario y la contraseña son correctas";
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error en la autenticacion, verifique que el usuario y la contraseña son correctas";
        }
    }
public byte[] FirmarCadena(CryptoServerCXI.Key key,byte[] data)
{
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
             md.update(data, 0, data.length);
            byte [] hash = md.digest();
            int mech = CryptoServerCXI.MECH_HASH_ALGO_SHA1 |CryptoServerCXI.MECH_PAD_PKCS1 ;
            return cxi.sign(key, mech, hash);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
}

public byte[][] FirmarCadenaBulk(CryptoServerCXI.Key key,List<byte[]> data)
{
        try {
            List<byte[]> hashes = new ArrayList<>();
            MessageDigest md = MessageDigest.getInstance("SHA1");
            int mech = CryptoServerCXI.MECH_HASH_ALGO_SHA1 |CryptoServerCXI.MECH_PAD_PKCS1 ;
            byte[][] b=new byte[data.size()][];
            for(int i=0; i< data.size();i++)
            {
                    md.update(data.get(i), 0, data.get(i).length);
                    byte[] hash = md.digest();
                    b[i]= hash;
                try {
                    byte[][] res =cxi.bulkSign(key, mech, b);
                    return res;
                } catch (IOException ex) {
                    Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CryptoServerException ex) {
                    Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
                }
                    
            
            }
           
            
            
            return null;
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
}
    private String Inicializar(String ip)
    {
        try {
            cxi = new CryptoServerCXI(ip, 3000);
            getCxi().setTimeout(60000);
            return "";
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        } catch (NumberFormatException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            return "Error al inicializar, verifique si su dispositivo se encuentra disponible";
        }
    }

    private String Validar(String usuario,String password,String ip)
    {
        if(usuario == null || password == null || ip == null)
            return "Parámetros incompletos";
        else if(usuario.equals("") || password.equals("") || ip.equals(""))
            return "Parámetros incompletos";
        else
            return "";
    }

    public String GenerarLlave(String nombre,String grupo)
    {
        String mensaje="";
        if(Estatus == true)
        {
            try
            {
              CryptoServerCXI.KeyAttributes attr = new CryptoServerCXI.KeyAttributes();

              attr = new CryptoServerCXI.KeyAttributes();
              attr.setAlgo(CryptoServerCXI.KEY_ALGO_RSA);
              attr.setSize(1024);
              attr.setName(nombre);
              if(!grupo.equals(""))
                  attr.setGroup(grupo);
              attr.setExport(CryptoServerCXI.KEY_EXPORT_ALLOW);
              attr.setExponent(BigInteger.valueOf(65537L));
              attr.setGenerationDate(new Date());
              attr.setExpirationDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2020-12-21 11:55:00"));

              CryptoServerCXI.Key rsaKey = cxi.generateKey(CryptoServerCXI.FLAG_OVERWRITE, attr);
            } catch (ParseException ex) {
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                mensaje="Ocurrio un error al tratar de generar la llave";
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (CryptoServerException ex) {
                mensaje="Ocurrio un error al tratar de generar la llave";
                Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
            }
            return mensaje;
        }
        else
        return "El dispositivo no esta CONECTADO, por favor, ingrese sus credenciales";
    }
    public void CerrarSesion()
    {
        try {cxi.close();}
        catch(Exception ee)
        {}
         
    }
    public CryptoServerCXI.Key ObtenerLlave(String nombreLlave,String grupo)
    {
        try     
        {
             KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals(""))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            return rsaKey;
        }
        catch(Exception ee)
        {
            return null;
        }
        
    }
     public byte[] ObtenerFirma(byte[] data,String nombreLlave,String grupo) throws IOException, NumberFormatException, CryptoServerException {
        byte[] sign=null;
        try {
            // create instance of CryptoServerCXI (opens connection to CryptoServer)
            KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals(""))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            // hash data
            MessageDigest md = MessageDigest.getInstance("SHA-1", "SUN");
            md.update(data, 0, data.length);
            byte[] hash = md.digest();
            // RSA sign hash
            int mech = CryptoServerCXI.MECH_HASH_ALGO_SHA1 |CryptoServerCXI.MECH_PAD_PKCS1 ;
            sign = cxi.sign( rsaKey, mech, hash);

        } catch (Exception ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
            return sign;
    }

     public String ObtenerModulus(String nombreLlave,String grupo)
    {     
        try {
            KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals(""))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            KeyAttributes attr2 =cxi.getKeyAttributes(rsaKey, true);
             StringBuilder sb = new StringBuilder();
             byte[] byteArray=attr2.getModulus();

         String hex = DatatypeConverter.printHexBinary(byteArray);

            
            return hex;
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
     }
     
     public String Encriptar(String nombreLlave,String grupo, String rutaArchivoEntrada,String rutaSalida)
     {
        try {
            KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals("") && !grupo.equals("null"))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            File file = new File(rutaArchivoEntrada);
            byte[] b= new byte[(int) file.length()];
            int mech = CryptoServerCXI.MECH_MODE_ENCRYPT | CryptoServerCXI.MECH_CHAIN_CBC;   
            byte [] iv_in = null;
            byte [] iv_out = new byte[16];  
            byte[] crypto =new byte[0];
            FileInputStream input= new FileInputStream(file);
            
            input.read(b);
            
            int rlen = b.length;
            int ofs = 0;
            int len = 16;


            while (rlen > 0)
            {        
              if (rlen <= 16)
              {
                len = rlen;          
                mech |= CryptoServerCXI.MECH_PAD_PKCS5;  // apply padding on last block
              }

              byte [] chunk = copyOf(b, ofs, len);        
              
              crypto=concat(crypto, cxi.crypt(rsaKey, mech, null, iv_in, chunk, iv_out));

              iv_in = iv_out;
              rlen -= len;
              ofs += len;
            }
            
            FileOutputStream fos = new FileOutputStream(rutaSalida);
            fos.write(crypto);
            fos.close();

        } 
        catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
     }
     
     
     public String DesEncriptar(String nombreLlave,String grupo,String rutaEntrada,String rutaSalida)
     {
         try
         {
              KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals("") && !grupo.equals("null"))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            File file = new File(rutaEntrada);
            byte[] b= new byte[(int) file.length()];
            byte[] iv_in = null;
            byte[] iv_out=null;
            int mech = CryptoServerCXI.MECH_MODE_DECRYPT | CryptoServerCXI.MECH_CHAIN_CBC | CryptoServerCXI.MECH_PAD_PKCS5;
            byte [] plain = cxi.crypt(rsaKey, mech, null, iv_in, b, iv_out);
            
            FileOutputStream fos = new FileOutputStream(rutaSalida);
            fos.write(plain);
            fos.close();
         }
         catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
        
     }
     public String Encrypt(String nombreLlave,String grupo, String rutaArchivoEntrada,String rutaSalida) throws CryptoServerException
     {
        try {
          KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals("") && !grupo.equals("null"))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            
            if(rutaArchivoEntrada.equals("") || rutaSalida.equals(""))
                return "Las rutas de entrada y salida deben ser una direccion válida";
            File file = new File(rutaArchivoEntrada);
            if(!file.exists())
                return "El archivo de entrada no existe";
            
            byte[] b= new byte[(int) file.length()];
            FileInputStream str= new FileInputStream(file);
            str.read(b);
            byte [] data = b;
            int mech =  CryptoServerCXI.MECH_MODE_ENCRYPT | CryptoServerCXI.MECH_CHAIN_CBC | CryptoServerCXI.MECH_PAD_PKCS5;
            byte [] crypto = cxi.crypt(rsaKey, mech, null, data, null);
            CryptoServerUtil.xtrace("encrypted data: ", crypto); 
            
            FileOutputStream fos = new FileOutputStream(rutaSalida);
            fos.write(crypto);
            fos.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
     }
     
     public String Decrypt(String nombreLlave,String grupo, String rutaArchivoEntrada,String rutaSalida) throws CryptoServerException
             {
        try {
          KeyAttributes attr = new CryptoServerCXI.KeyAttributes();
            if(!grupo.equals("") && !grupo.equals("null"))
                attr.setGroup(grupo);
            attr.setName(nombreLlave);
            CryptoServerCXI.Key rsaKey = cxi.findKey(attr);
            
            File file = new File(rutaArchivoEntrada);
            byte[] b= new byte[(int) file.length()];
            FileInputStream str= new FileInputStream(file);
            str.read(b);
            byte [] data = b;
            int mech =  CryptoServerCXI.MECH_MODE_DECRYPT | CryptoServerCXI.MECH_CHAIN_CBC | CryptoServerCXI.MECH_PAD_PKCS5;
            byte [] crypto = cxi.crypt(rsaKey, mech, null, data, null);
            CryptoServerUtil.xtrace("Decrypted data: ", crypto); 
            
            FileOutputStream fos = new FileOutputStream(rutaSalida);
            fos.write(crypto);
            fos.close();
            
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
     }
    /**
     * @return the cxi
     */
    public CryptoServerCXI getCxi() {
        return cxi;
    }

    /**
     * @return the Estatus
     */
    public Boolean getEstatus() {
        return Estatus;
    }

    /**
     * @return the lista
     */
    public KeyAttributes[] getLista() {
        try {
            return cxi.listKeys();
        } catch (IOException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CryptoServerException ex) {
            Logger.getLogger(CXI.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}


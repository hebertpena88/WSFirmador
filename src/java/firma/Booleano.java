/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package firma;

/**
 *
 * @author Admin
 */
public class Booleano {
    private boolean peticionCorrecta ;
    private String error;
    private String firma;
    
    public Booleano()
    {
        peticionCorrecta=false;
    }
    
    public Booleano(String msg)
    {
        error=msg;
        peticionCorrecta=false;
    }

    /**
     * @return the peticionCorrecta
     */
    public boolean isPeticionCorrecta() {
        return peticionCorrecta;
    }

    /**
     * @param peticionCorrecta the peticionCorrecta to set
     */
    public void setPeticionCorrecta(boolean peticionCorrecta) {
        this.peticionCorrecta = peticionCorrecta;
    }

    /**
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * @return the firma
     */
    public String getFirma() {
        return firma;
    }

    /**
     * @param firma the firma to set
     */
    public void setFirma(String firma) {
        this.firma = firma;
    }
    
}

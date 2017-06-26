/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basica;
import java.sql.*;

/*
* Desarrollado para SoftFistTIC
* Por Agustín Aguilar Sánchez
*/

/**
* Funciones Basicas para el manejo de una Base de Datos MSSQL
* @author AAS
*/
public class Basica {
    
    protected String DB_Driver;
    
    protected Connection conn = null;
    protected Statement stmt = null;
    protected ResultSet rs = null;
    
    protected String sconn;
    protected String[] resp;
    protected String metodo;
    protected String sp;
    //protected DataTable dt;
    protected boolean verifica;
    

    // Declare the JDBC objects.
    /**
     * Constructor sin Cadena de Conexión
     */
    public Basica(){
        DB_Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        resp = new String[2];
        metodo = "";
        sp = "";
        sconn="";
        conn = null;
        stmt = null;
        rs = null;
        //par = null;
    }	
    
    /**
     * Constructor con Cadena de Conexión
     * @param sconn Cadena de Conexión para el servidor
     */
    public Basica(String sconn){
        DB_Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        resp = new String[2];
        metodo = "";
        sp = "";
        this.sconn=sconn;
        conn = null;
        stmt = null;
        rs = null;
        //par = null;
        VerificaConexion();
    }
    
    /**
     * Verifica que las Credenciales sean validas, el Servidor este disponible y el Catalogo accesible
     */
    protected void VerificaConexion(){
        verifica = false;
        try{
            if(sconn.equals(""))
                return;
            Class.forName(DB_Driver);
            conn = DriverManager.getConnection(sconn);
            verifica=conn.isValid(1);
            conn.close();
            resp[0] = "Exito";
            resp[1] = "Exito";
        }catch(Exception ex){
            verifica = false;
            System.out.println("Error: "+ex.getMessage());
            resp[0] = "Error";
            resp[1] = "Error al establecer conexión a la Base de Datos:\\r\\n\\t" + ex.getMessage();
        }
    }
    
    /**
     * Establece la conexión con el servidor con las credenciales asigandas
     * @return True si se establece la conexión
     */
    protected  boolean AbrirConexion(){
        boolean abierta=false;
        try {            
            // Establish the connection.
            Class.forName(DB_Driver);
            
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            if(conn.isValid(0))
                return true;
            conn = DriverManager.getConnection(sconn);
            abierta = conn.isValid(1);
            resp[0] = "Exito";
            resp[1] = "Exito al establecer la Conexión";
        }
        catch (SQLException e) {
                System.out.println("Error"+e.getMessage());
                //e.printStackTrace();
                abierta = false;
                resp[0] = "Error";
                resp[1] = "Error al establecer conexión con la Base de Datos, verifique estado de red y reintente en un par de minutos\n" +
                    "Detalle:\r\n\t" + e.getMessage();
        }
        return abierta;
    }
    
    /**
     * Cierra la Conexión Actual
     * @return True si se cerró
     */
    protected boolean CerrarConexion(){
        boolean cerrada=false;
        try {
            conn.close();
            cerrada = conn.isClosed();
        }
        catch (SQLException e) {
                System.out.println("Error");
                e.printStackTrace();
                cerrada = false;
        }
        finally {
                if (rs != null) try { rs.close(); } catch(Exception e) {}
                if (stmt != null) try { stmt.close(); } catch(Exception e) {}
        }
        return cerrada;
    }
    
    /**
     * Cambia el catologo que se conectó originalmente, dentro del mismo Servidor
     * @param BDNueva Nombre del nuevo catalogo
     */
    protected void CambiaBD(String BDNueva){
        try{
            if(AbrirConexion())
                conn.setCatalog(BDNueva);
                
        }catch(SQLException ex){
            GeneraError(ex,"Al Cambiar la BD");
        }
    }
    
    /**
     * Genera Error concatenado a otro
     * @param ex Recibe la excepción
     * @param Desc Agrega Descripción personalizada
     * @param Concatena True si Agrega al Final de Otro Mensaje de Error.
     */
    protected void GeneraError(SQLException ex, String Desc, boolean Concatena){
        resp[0] = "Error";
        if (Concatena)
        {
            if (resp[1].equals(""))
                resp[1] = "Favor de Comunicar con su Admon de Sistemas,";
            resp[1] = resp[1] + "\n" +
                         "Numero: " + ex.getErrorCode() + "\n" + ex.getMessage() +
                         "\nCodigo(" + Desc + "[" + (sp.equals("") ? "CONSULTA" : sp) + "]" + "):" + metodo;
        }
        else
        {
            resp[1] = "";
            resp[1] = "Favor de Comunicar con su Admon de Sistemas,\n" +
                         "Numero: " + ex.getErrorCode() + "\n" + ex.getMessage() +
                         "\nCodigo(" + Desc + "[" + (sp.equals("") ? "CONSULTA" : sp) + "]" + "): " + metodo;
        }
    }
    
    /**
     * Genera Error Sin concatenar errores Anteriores
     * @param ex Recibe la Excepcion
     * @param Desc Agrega una descripcion personalizada
     */
    protected void GeneraError(SQLException ex, String Desc){
       GeneraError(ex, Desc, false);
    }
    
    /**
     * Valida que la consulta contenga almenos un dato
     * @param rs ResultSet de la consulta
     * @return True si contiene Registros
     */
    protected boolean BValidaSalida(ResultSet rs){
        try{
            if( rs == null)
                return false;
            else if(!rs.first()) 
                return false;
            }
        catch(SQLException e){
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
}

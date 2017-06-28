/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Basica;
//import BD.Type_DatoParm;
import com.SoftfisTIC.bd.ListaE;
import java.sql.*;
import java.util.ArrayList;

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
    protected CallableStatement cs =null;
    
    protected String sconn;
    protected String[] resp;
    protected String[] spout;
    protected String[] cols;
    protected String metodo;
    protected String sp;
    protected ArrayList<String[]> arr;
    protected boolean verifica;
    protected boolean lleno;
    
    /**
     * Constructor sin Cadena de Conexión
     */
    public Basica(){
        DB_Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        
        sconn="";
        conn = null;
        stmt = null;
        rs = null;
        cs = null;
        
        resp = new String[2];
        spout = null;
        cols = null;
        arr=null;
        metodo = "";
        sp = "";
        
        verifica = false;
        lleno = false;
        //par = null;
    }	
    
    /**
     * Constructor con Cadena de Conexión
     * @param sconn Cadena de Conexión para el servidor
     */
    public Basica(String sconn){
        DB_Driver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
        
        this.sconn=sconn;
        conn = null;
        stmt = null;
        rs = null;
        cs = null;
        
        resp = new String[2];
        spout = null;
        cols = null;
        arr=null;
        metodo = "";
        sp = "";
        
        verifica = false;
        lleno = false;
        
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
     * @return True, Se establece la conexión
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
            cs= null;
            rs=null;
            stmt = null;
            arr= null;
            lleno = false;
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
     * @return True, si se cierra la conexción
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
         if (conn != null) try { conn.close(); } catch(Exception e) {}
         if (cs != null) try { cs.close(); } catch(Exception e) {}
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
     * @param Desc Agrega descripción personalizada
     * @param Concatena True, si Agrega al final de otro mensaje de error.
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
     * @param ex Recibe la excepción
     * @param Desc Agrega una descripción personalizada
     */
    protected void GeneraError(SQLException ex, String Desc){
       GeneraError(ex, Desc, false);
       System.out.println(resp[1]);
    }
    
     /**
     * Genera el Statement para ejecutar un SP
     * @param SP Nombre del SP, debe incluir esquema(No necesario en dbo)
     * @param Param Lista de Parametros
     */
    protected void GeneraParams(String SP,Object[][] Param){
        String spaux = "{ call "+SP+(Param!=null?"(":"");
        for(int i=0;Param!=null && i<Param.length;i++)
            spaux += "?"+(i<Param.length-1?",":"");
        spaux +=(Param!=null?")":"")+" }";
        try{
            sp=SP;
            cs = conn.prepareCall(spaux);
            if (Param==null)
                return;
            
            for(int i=0;i<Param.length;i++){
                if(Param[i][3].toString().equals("Output")){
                    switch(Param[i][1].toString()){
                        case "TInt":
                            cs.registerOutParameter(Param[i][0].toString(), java.sql.Types.INTEGER);
                            break;
                        case "TDecimal":
                        case "TDouble":
                            cs.registerOutParameter(Param[i][0].toString(), java.sql.Types.DECIMAL);
                            break;
                        case "TDate":   
                            cs.registerOutParameter(Param[i][0].toString(), java.sql.Types.DATE);
                            break;
                        case "Default":
                        case "TVarchar":
                            cs.registerOutParameter(Param[i][0].toString(), java.sql.Types.VARCHAR);
                            break;
                    }
                }else{
                    //System.out.println(Param[i][1].toString());
                    switch(Param[i][1].toString()){
                        case "TInt":
                            cs.setInt(Param[i][0].toString(), Integer.parseInt(Param[i][2].toString()));
                            break;
                        case "TDecimal":
                        case "TDouble":
                            cs.setDouble(Param[i][0].toString(), Double.parseDouble(Param[i][2].toString()));
                            break;
                        case "TDate":
                            cs.setDate(Param[i][0].toString(), Date.valueOf(Param[i][2].toString()));
                            break;
                        case "Default":
                        case "TVarchar":
                            cs.setString(Param[i][0].toString(), Param[i][2].toString());
                            break;
                    }
                }
            }
        }
        catch(SQLException ex){
            cs=null;
            GeneraError(ex,"GeneraParams");
        }
    }
    
    /**
     * Lee los parametros para obtener una salida
     * @param Param Lista de Parametros
     */
    protected void ObtieneSalida(Object[][] Param){
        spout = new String[Param.length];
        try{
            if (Param==null)
                return;
            
            for(int i=0;i<Param.length;i++){
                if(Param[i][3].toString().equals("Output")){
                    switch(Param[i][1].toString()){
                        case "TInt":
                            spout[i] = cs.getInt(Param[i][0].toString())+"";
                            break;
                        case "TDecimal":
                        case "TDouble":
                            spout[i]= cs.getDouble(Param[i][0].toString())+"";
                            break;
                        case "TDate":   
                            spout[i]= cs.getDate(Param[i][0].toString())+"";
                            break;
                        case "Default":
                        case "TVarchar":
                            spout[i]= cs.getString(Param[i][0].toString());
                            break;
                    }
                }
                else spout[i] = "NA";
            }
        }catch(SQLException ex){
            GeneraError(ex,"ObtieneSalida",true);
        }
    }
    
    /**
     * Genera una Lista con un arreglo de todas las columnas de un ResultSet
     * @param Cols Cantidad de Columnas
     * @return ArrayList con un arreglo de String
     */
    protected ArrayList<String[]> LOLista(int Cols){
        BValidaSalida();
        if (!lleno) return null;
        NombreColumnas(Cols);
        //ArrayList<String[]> dt=null;
        String []row;
        try{
            arr = new ArrayList<>();
            while(rs.next()){
                row = new String[Cols];
                for(int i=1;i<=Cols;i++){
                    row[i-1]=rs.getString(i);
                }
                arr.add(row);
            }
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
            arr= null;
        }
        return arr;
    }
    
    /**
     * Genera un List con los datos para usarse en un combo
     * @return ArrayList con los Datos para un Combo
     */
    protected ArrayList<ListaE> LCLista(){
        BValidaSalida();
        if (!lleno) return null;
        ArrayList<ListaE> dt=null;
        try{
            dt = new ArrayList<>();
            while(rs.next()){
                dt.add(new ListaE(rs.getString(1),rs.getString(2)));
            }
        }
        catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return dt;
    }
    
    /**
     * Valida que la consulta contenga almenos un dato
     */
    protected void BValidaSalida(){
        try{
            if( rs == null)
                lleno = false;
            else if(!rs.next()) 
                lleno = false;
            rs.beforeFirst();
            }
        catch(SQLException e){
            System.out.println(e.getMessage());
            lleno = false;
        }
        lleno = true;
    }
    
    /**
     * Para obtener los nombres de las columnas del ResultSet
     * @param Cols Cantidad de Columnas
     */
    protected void NombreColumnas(int Cols){
        cols = new String[Cols];
        try{
            for(int i=1;i<=Cols;i++){
                cols[i-1]=rs.getMetaData().getColumnName(i);
            }
        }catch(SQLException ex){
            cols = null;
            System.out.println(ex.getMessage());
        }
    }
    
}

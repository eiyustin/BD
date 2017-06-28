/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.SoftfisTIC.bd;



import java.sql.*;
import java.util.ArrayList;
import Basica.*;


//import fb.funcionesbasicas.Listas;
//* Desarrollado para SoftFistTIC
/**
* API de funciones para una Base de Datos MS SQL <p>
* @author Agustín Aguilar Sánchez
* 
*/
public class BD extends Basica {  
    
    //<editor-fold defaultstate="collapsed" desc="Propiedades de la Clase">
    /**
     * Bandera con la respuesta de la ejecución de cada proceso
     * @return Respuesta[0] Titulo, Respuesta[1] Descripción
     */
    public String[] getResp() {
        return resp;
    }
    
    /**
     * Obtiene los parametros de Salida, en caso de ser parametro Input regresa "NA".
     * @return String[], con el número de parametro.
     */
    public String[] getSPOut() {
        return spout==null?new String[]{""}:spout;
    }
    
    /**
     * Obtiene una String[] con un listado del nombre de las Columnas de la consulta
     * @return String[], con Nombre de las columnas.
     */
    public String[] getCols() {
        return cols==null?new String[]{""}:cols;
    }
    
    /**
     * Devuelve la Cadena de Conexión usada.
     * @return Cadena de Coexión usada
     */
    public String getSConn() {
        return sconn;
    }
    
    /**
     * Asigna la Cadena de Conexión para usar en la instancia
     * @param conn Cadena de Conexión
     */
    public void setSConn(String conn) {
        sconn = conn;
        VerificaConexion();
    }
    
    /**
     * Obtiene el Catalogo al cual se esta conectando
     * @return Nombre del Catalogo
     */
    public String getBaseD() {
        try{
            return conn.getMetaData().getDatabaseProductName();
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        return "";
    }
    
    /**
     * Obtiene el Metodo que lo invocó, ayuda al manejo de Errores
     * @return Nombre del Método
     */
    public String getMetodo() {
        return metodo;
    }
    
    /**
     * Asigna el método el cual lo esta invocando
     * @param metodo Nombre del Método
     */
    public void setMetodo(String metodo) {
        super.metodo = metodo;
    }
    
    /**
     * Obtiene una bandera si la consulta ejecutada obtuvo Retorno.
     * @return Bandera True, Lleno
     */
    public boolean isLleno() {
        return lleno;
    }
    
    /**
     * Verifica si la conexión actual está Disponible.
     * @return True si esta disponible
     */
    public boolean isVerifica() {
        VerificaConexion();
        return verifica;
    } 
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Constructores">
    /**
     * Constructor sin Cadena, debe Asignarse Manualmente para utilizar en la instancia
     */
    public BD(){
        super();
    }
    
    /**
     * Constructor que incluye la cadena de conexión para usar en la instancia
     * @param sconn Cadena de Conexión para usar en la instancia
     */
    public BD(String sconn){
        super(sconn);
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Para obtener resultados de Queries, incluye SP">
    /**
     * Ejecuta un Qry de consulta, devuelve un ArrayList con la respuesta
     * @param SQL Query con la Consulta
     * @return ArrayList (String[]) con la respuesta, NULL en caso de error;
     */
    public ArrayList<String[]> ALConsultaQry(String SQL){
        ArrayList<String[]> arr;
        if (!verifica)
            return null;
        if (!AbrirConexion()){
            CerrarConexion();
            return null;
        }
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(SQL);
            arr = LOLista(rs.getMetaData().getColumnCount());
            resp[0] = "Exito";
            resp[1] = "Exito al Generar el Select";
        }
        catch (SQLException ex)
        {
            GeneraError(ex, "ALConsultaQry");
            arr=null;
        }
        CerrarConexion();
        return arr;
    }
    
    /**
     * Ejecuta un SP con resultado de Consulta, puede contener parametros de retorno;
     * @param SP Nombre del SP que se ejecutará, incluir Esquema
     * @param Parametros Parametros de Entrada con Orden Especifico(Nombre,Type_DatoParam,Valor,Type_DatoDireccion)
     * @return ArrayList(String[]) con la respuesta, NULL en caso de error;
     */
    public ArrayList<String[]> ALConsultaSP(String SP,Object [][] Parametros){
        if (!verifica)
            return null;
        if (!AbrirConexion()){
            CerrarConexion();
            return null;
        }
        try
        {
            GeneraParams(SP,Parametros);
            rs = cs.executeQuery();
            arr= LOLista(rs.getMetaData().getColumnCount());
            ObtieneSalida(Parametros);
            resp[0] = "Exito";
            resp[1] = "Exito al Generar el Select";
        }
        catch (SQLException ex)
        {
            GeneraError(ex, "ALConsultaSP");
            arr=null;
        }
        CerrarConexion();
        return arr;
    }
    //</editor-fold>
       
    //<editor-fold defaultstate="collapsed" desc="Funciones Basicas para afectar la base">
    /**
     * Ejecuta conjunto de SP's sin resultado de Consulta, con Retorno de Parametros
     * @param SP Nombres de los SP que se ejecutarán, incluir Esquema
     * @param Parametros Parametros de Entrada con Orden Especifico(Nombre,Type_DatoParam,Valor,Type_DatoDireccion) estos deben estar ordenados por el SP al cula pertenecen
     * @return String[] con los parametros la respuesta, NA en caso de NO ser OUTPUT
     */
    public String[][] StrEjecutaSP(String SP[],Object [][][] Parametros){
        
        if (!verifica)
            return null;
        if (!AbrirConexion()){
            CerrarConexion();
            return null;
        }
        String [][]SPOUT = new String[SP.length][];
        try
        {
            for(int i=0;i<SP.length;i++){
                GeneraParams(SP[i],Parametros[i]);
                cs.execute();
                ObtieneSalida(Parametros[i]);
                SPOUT[i]=spout;
            }
            resp[0] = "Exito";
            resp[1] = "Exito al Generar el Select";
        }
        catch (SQLException ex)
        {
            GeneraError(ex, "StrConsultaSP");
            return  null;
        }
        CerrarConexion();
        return SPOUT;
    }
    
    /**
     * Ejecuta un Conjunto de Instrucciones SQL,devuelve una matriz con número de filas afectadas con cada instruccion
     * @param Qry Conjunto de Instrucciones
     * @return Un conjunto de int, con el numero de filas afectadas por cada instruccion
     */
    public int[] IEjecutaQry(String[] Qry){
        if (!verifica)
            return null;
        if (!AbrirConexion()){
            CerrarConexion();
            return null;
        }
        int[] afecta = new int[Qry.length];
        try
        {
            stmt = conn.createStatement();
            for(int i=0;i<Qry.length;i++)
                afecta[i] = stmt.executeUpdate(Qry[i]);
            resp[0] = "Exito";
            resp[1] = "Exito al ejecutar los Queries";
        }
        catch(SQLException ex){
            GeneraError(ex, "IEjecutaQry");
            return  afecta;
        }
        return afecta;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Funcionales extras para el trabajo con la BD">
    /**
     * Devuelve un Lista(ListaE) con los datos que llenarán un ComboBox, con un ItemValue y un DisplayMember, debe importar el tipo ListaE, los datos deben estar ordenados de manera especifica
     * @param Config Datos para generar la consulta,<p>
     * [0]("Id") El campo que será el Id del Registro <p>
     * [1]("Nombre") El campo que se Mostrará <p>
     * [2]("Tabla") La tabla del cual se obtendran los datos(Acepta Join's) <p>
     * [3]("Condicion") La condicion que debe cumplir la sentencia,en caso de nulo o "", no habrá condición <p>
     * [4]("Order") Se ordenaran los registros?,en caso de nulo o "", se tomará el orden prederminado <p>
     * [5]("Todos") Opcion Todos? True, si usar Opcion Todos <p>
     * [6]("TodosNom") El nombre que se mostrará al valor de Todos,en caso de nulo o "", se tomara el "Todos" <p>
     * @return Lista con los Datos para el Combobox
     */
    public ArrayList<ListaE> AEComboBox(Object []Config){
        if (!verifica)
            return null;
        if (!AbrirConexion()){
            CerrarConexion();
            return null;
        }
        ArrayList<ListaE> cb;
        String Qry = "Select ";
        Qry += Config[0] + ","
                + Config[1]
                + " from " + Config[2];
        Qry += (!Config[3].toString().equals("") && Config[3] != null ? " where " + Config[3].toString() : "");
        Qry += ((boolean)Config[5] ? " union select 0,'" + (Config[6]!= null && !Config[6].toString().equals("")?Config[6]:"Todos") + "'" : "");
        Qry += (Config[4] != null && !Config[4].toString().equals("") ? " order by " + Config[4].toString() : "");
        //System.out.println(Qry);
        try
        {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(Qry);
            cb= LCLista();
            resp[0] = "Exito";
            resp[1] = "Exito al Generar el Select";
        }
        catch (SQLException ex)
        {
            GeneraError(ex, "ALConsultaQry");
            cb=null;
        }
        CerrarConexion();
        
        return cb;
    }
    
    /**
     * Obtiene la respuesta de una consulta, especificando una columna.
     * @param Qry Consulta que se ejecutará
     * @param Col Indice de la columna de la cual se obtendrá el valor, siendo la primera 0, la segunda 1, etc.
     * @return Valor Encontrado, En caso de no hallar devuelve "";
     */
    public String SBuscaValor(String Qry,int Col){
        ALConsultaQry(Qry);
        return lleno?arr.get(0)[Col]  :"";
    }
    
    /**
     * Cambia el nombre de las Columnas de una consulta, de las Ultimas a las Primeras
     * @param Cols Lista con nombres de columnas
     * @return Lista con los nombres de las columnas actualizados
     */
    public String[] CambiaNombreCols(String[] Cols){
        if (cols==null) return null;
        int c = cols.length;
        if (c > 0)
            for (int i = Cols.length - 1; i > -1; i--){
                cols[c] = Cols[i];
                c -= 1;
            }
        return cols;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Funciones que son dependinetes de las anteriores">
    /**
     * Ejecuta una Instruccion de SQL, devuelve el número de filas Afectadas con la instruccion
     * @param Qry Instruccion SQL para ejecutar
     * @return Número de Filas Afectadas con la instrucción
     */
    public int IEjecutaQry(String Qry){
        int []Resp = IEjecutaQry(new String[]{Qry});
        return (Resp==null?-1:Resp[0]);
    }
    
    /**
     * Ejecuta un SP sin resultado de Consulta, con Retorno de Parametros
     * @param SP Nombre del SP que se ejecutará, incluir Esquema
     * @param Parametros Parametros de Entrada con Orden Especifico(Nombre,Type_DatoParam,Valor,Type_DatoDireccion)
     * @return String[] con los parametros la respuesta, NA en caso de NO ser OUTPUT
     */
    public String[] StrEjecutaSP(String SP,Object [][] Parametros){
        String[][] Resp = StrEjecutaSP(new String []{SP},new Object[][][]{Parametros});
        return Resp==null?new String[]{"NA"}:Resp[0];
    }
    
    /**
     * Devuelve un Lista(ListaE) con los datos que llenarán un ComboBox, con un ItemValue y un DisplayMember, debe importar el tipo ListaE, los datos deben estar ordenados de manera especifica
     * @param Tabla La tabla del cual se obtendran los datos(Acepta Join's)
     * @param Id El campo que será el Id del Registro
     * @param Nombre El campo que se Mostrará
     * @param Condicion La condición que debe cumplir la sentencia, en caso de null o "", no habrá condición
     * @param Todos Opcion Todos? True, para incluir Opción Todos
     * @param TodosNom El nombre que se mostrará al valor de Todos,en caso de nulo o "", se tomara el "Todos"
     * @param Order Se ordenaran los registros?,en caso de nulo o "", se tomará el orden prederminado
     * @return Lista con los Datos para el Combobox
     */
    public ArrayList<ListaE> AEComboBox(String Tabla, String Id, String Nombre, String Condicion, boolean Todos, String TodosNom, String Order){
        return AEComboBox(new Object[] { Id, "", Nombre, "", Tabla, Condicion, Order, Todos, TodosNom });
    }
    
    /**
     * Obtiene la respuesta de un Qry con un solo campo y un solo Registro
     * @param Qry Consulta que se ejecutará
     * @return Valor Encontrado, En caso de no hallar devuelve "";
     */
    public String SBuscaValor(String Qry){
        return SBuscaValor(Qry,0);
    }
    //</editor-fold>   

}

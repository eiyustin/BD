/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.SoftfisTIC.bd;

/**
* Desarrollado para SoftFistTIC
* Por Agustín Aguilar Sánchez
*/

/**
* Tipos de Datos que acepta el SQL
*/

public enum Type_DatoParm
{
    /**
    *Tipo integer
    */
    TInt(0),
    /**
    *Tipo double
    */
    TDouble(1),
    /**
    *Tipo varchar
    */
    TVarchar(2),
    /**
    *Tipo Date
    */
    TDate(3),
    /**
    *Tipo por defecto, Varchar
    */
    TDefault(4);

    private final int DatoParam;

    Type_DatoParm(int DatoParam){
        this.DatoParam = DatoParam;
    }

    public int getDatoParam(){
        return DatoParam;

    }
}



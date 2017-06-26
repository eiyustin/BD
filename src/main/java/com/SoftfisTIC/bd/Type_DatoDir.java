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
* Direccion del Parametro(Input,Output,ReturnValue)
*/
public enum Type_DatoDir
    {
        /**
        *Entrada
        */
        Input(0),
        /**
        *De Retorno
        */
        ReturnValue(1),
        /**
        *De salida
        */
        Output(2),
        /**
        *Por Defecto, input
        */
        Default(3);
        
        private final int DatoDir;
        Type_DatoDir(int DatoDir){
            this.DatoDir = DatoDir;
        }
        
        public int getDatoDir(){
            return DatoDir;
        }
    }

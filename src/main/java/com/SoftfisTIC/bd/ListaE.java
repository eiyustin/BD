/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.SoftfisTIC.bd;

/**
 * Entidad Básica para obtener los datos de un ComboBox
 * @author  Agustín Aguilar Sánchez<p>
 * Desarrollado para SoftFistTIC
 */
public class ListaE {
    private String Value;
    private String Display;
    
    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getDsiplay() {
        return Display;
    }

    public void setDsiplay(String display) {
        Display = display;
    }
    
    @Override
    public String toString() {
        return Display;
    }
    public ListaE(String Value,String Display){
        this.Value=Value;
        this.Display = Display;
    }
}

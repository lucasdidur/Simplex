/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.simplex.util;

/**
 *
 * @author Lucas
 */
public enum Tipo
{
    MENOR_IGUAL("<="),
    MAIOR_IGUAL(">="),
    IGUAL("=");

    String value;

    Tipo(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}

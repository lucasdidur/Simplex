/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.simplex;

import me.simplex.util.Tipo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.simplex.util.MatrixUtil;

/**
 *
 * @author Lucas
 */
@Getter
@Setter
public class Expressao
{

    private Tipo tipo;
    private List<Double> values = new ArrayList<>();
    private Double b;

    Expressao(Double... i)
    {
        values = new LinkedList(Arrays.asList(i));
    }

    Expressao(Tipo tipo, Double b, Double... i)
    {
        this.tipo = tipo;
        this.values = new LinkedList(Arrays.asList(i));
        this.b = b;
    }
    
    public void addValue(Double value)
    {
        this.values.add(value);
    }

    int size()
    {
        return values.size();
    }
    
    public double[] getValueVector()
    {
        return MatrixUtil.toPrimitive(values.toArray(new Double[values.size()]));
    }

    public Expressao clone()
    {
        return new Expressao(tipo, b, values.toArray(new Double[values.size()]));
    }

    
    
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        
        for (Double value : values)
        {
            sb.append(value).append(" ");
        }
        return sb.toString();
    }
}

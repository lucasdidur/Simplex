/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.simplex;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import lombok.Getter;
import lombok.Setter;
import static me.simplex.Simplex.printMatrix2;
import me.simplex.util.Gauss;

/**
 *
 * @author Lucas
 */
@Getter
@Setter
public class Problema
{

    String objetivo;
    
    Expressao funcao;
    Expressao funcaoPadrao;
    
    List<Expressao> restricoes = new ArrayList<>();
    List<Integer> vBasica = new ArrayList<>();
    List<Integer> vNBasica = new ArrayList<>();

    public void setFuncao(Expressao expressao)
    {
        if(objetivo.equals("max"))
        {
            for (ListIterator<Double> iterator = expressao.getValues().listIterator(); iterator.hasNext();)
            {
                Double next = iterator.next();
                
                iterator.set(next * -1);
            }
        }
        
        this.funcao = expressao;
        this.funcaoPadrao = expressao.clone();
    }   
    
    public void addRestricao(Expressao expressao)
    {
        restricoes.add(expressao);
    }

    public void addBasica(int value)
    {
        vBasica.add(value);
    }

    public void addNBasica(int value)
    {
        vNBasica.add(value);
    }

    public double[][] getMatriz()
    {
        double[][] matrix = new double[restricoes.size()][restricoes.get(0).size()];

        for (int i = 0; i < restricoes.size(); i++)
        {
            for (int j = 0; j < restricoes.get(0).size(); j++)
            {
                matrix[i][j] = restricoes.get(i).getValues().get(j);
            }
        }

        return matrix;
    }

    public double[][] getMatrizBasicas()
    {
        double[][] matrix = new double[vBasica.size()][vBasica.size()];

        for (int i = 0; i < vBasica.size(); i++)
        {
            for (int j = 0; j < vBasica.size(); j++)
            {
                matrix[i][j] = restricoes.get(i).getValues().get(vBasica.get(j) - 1);
            }
        }

        return matrix;
    }

    double[] getCustosRestricoes()
    {
        double[] custos = new double[restricoes.size()];

        for (int i = 0; i < restricoes.size(); i++)
        {
            custos[i] = restricoes.get(i).getB();
        }

        return custos;
    }

    double[] getCustosBasicas()
    {
        double[] custos = new double[vBasica.size()];

        for (int i = 0; i < vBasica.size(); i++)
        {
            custos[i] = funcaoPadrao.getValues().get(vBasica.get(i) - 1);
        }

        return custos;
    }

    public double[] getXb()
    {
        double[][] a = getMatrizBasicas();
        double[] b = getCustosRestricoes();

        double[] xB = Gauss.gauss(a, b);
        return xB;
    }

    double getCusto(int i)
    {
        return funcaoPadrao.getValues().get(i - 1);
    }

    double[] getColuna(int value)
    {
        double[][] matrix = getMatriz();
        double[] coluna = new double[matrix.length];

        for (int i = 0; i < matrix.length; i++)
        {
            coluna[i] = matrix[i][value - 1];
        }

        return coluna;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.simplex;

import me.simplex.util.Tipo;
import java.util.ArrayList;
import java.util.List;
import me.simplex.util.Gauss;
import me.simplex.util.MatrixUtil;

/**
 *
 * @author Lucas
 */
public class Simplex
{

    Problema problema = new Problema();
    private double[] xB;
    private double[] lambda;
    private int k;
    private double min;
    private double[] custos;
    private double[] y;
    private int e;

    void calcula()
    {
        formaPadrao();
        printFuncao();

        int i = 1;
        System.out.println("\nIteração " + i);
        System.out.println("");

        System.out.println("Matriz Base");
        printMatrix(problema.getMatrizBasicas());
        System.out.println("");

        calculaXb();
        acharLambda();

        calculaCustoRelativo();

        while (!checaParadaCusto())
        {
            calculaDirecaoSimplex();
            calculaCandidato();
            atualizaVariaveis();

            i++;
            System.out.println("\nIteracao " + i);
            System.out.println("");
            
            System.out.println("Matriz Base");
            printMatrix(problema.getMatrizBasicas());
            System.out.println("");

            calculaXb();
            acharLambda();

            calculaCustoRelativo();
        }
    }

    public void formaPadrao()
    {
        problema.setObjetivo("max");
        problema.setFuncao(new Expressao(2d, 5d));

        problema.addRestricao(new Expressao(Tipo.MENOR_IGUAL, 600d, 3d, 10d));
        problema.addRestricao(new Expressao(Tipo.MENOR_IGUAL, 162d, 1d, 2d));

        int posI = problema.getRestricoes().get(0).getValues().size();
        if (problema.getVBasica().isEmpty())
        {
            for (int i = 0; i < problema.getRestricoes().size(); i++)
            {
                for (int j = 0; j < problema.getRestricoes().size(); j++)
                {
                    if (i == j)
                    {
                        problema.getRestricoes().get(i).addValue(1d);
                        problema.addBasica(posI + j + 1);
                    }
                    else
                    {
                        problema.getRestricoes().get(i).addValue(0d);
                    }
                }
            }

            for (int i = 0; i < posI; i++)
            {
                problema.addNBasica(i + 1);
            }

            for (Expressao e : problema.getRestricoes())
            {
                problema.getFuncaoPadrao().addValue(0d);
            }
        }
    }

    /**
     * Passo 1
     */
    public void calculaXb()
    {
        xB = problema.getXb();
        System.out.println("Valor Xb");
        printMatrix2(xB);
        System.out.println("");
    }

    /**
     * Passo 2.1
     */
    public void acharLambda()
    {
        double[][] transposto = MatrixUtil.calcularTransposta(problema.getMatrizBasicas());

        double[] b = problema.getCustosBasicas();

        lambda = Gauss.gauss(transposto, b);
        System.out.println("Valor Lambda");
        printMatrix2(lambda);
        System.out.println("");
    }

    /**
     * Passo 2.2 - Calcula o custo relativo das variaveis não básicas
     */
    public void calculaCustoRelativo()
    {
        System.out.println("");
        custos = new double[problema.vNBasica.size()];
        min = Double.MAX_VALUE;

        // 2.2
        for (int i = 0; i < problema.vNBasica.size(); i++)
        {
            int vb = problema.vNBasica.get(i);
            
            custos[i] = problema.getCusto(vb) - (MatrixUtil.multiplicarVetores(lambda, problema.getColuna(vb)));

            // 2.3
            if (custos[i] < min)
            {
                k = i + 1;
                min = custos[i];
            }

            System.out.println("Custo relativo da variavel nao basica " + (i + 1) + " (" + custos[i] + ")");
        }

        System.out.println("Candidata a sair da base: k=" + k);
        System.out.println("");
    }

    /**
     * Passo 3 - Teste de otimalidade
     */
    public boolean checaParadaCusto()
    {
        boolean todosPositivos = true;

        for (double custo : custos)
        {
            if (custo < 0)
            {
                todosPositivos = false;
            }
        }

        if (todosPositivos)
        {
            System.out.println("Esta na solução otima");

            System.out.println("");
            System.out.println("Solução: ");

            double[] xBFinal = new double[problema.getFuncao().size()];
            double[] x = new double[problema.getFuncaoPadrao().size()];

            for (int i = 0; i < problema.getVBasica().size(); i++)
            {
                x[problema.getVBasica().get(i) - 1] = xB[i];
            }

            for (int i = 0; i < xBFinal.length; i++)
            {
                xBFinal[i] = x[i];
            }

            double fx = MatrixUtil.multiplicarVetores(problema.getFuncao().getValueVector(), xBFinal);

            if(problema.getObjetivo().equals("max"))
            {
                fx *= -1;
            }
            
            System.out.println("f(x) = " + fx);
            printMatrix2(xBFinal);
        }
        else
        {
            System.out.println("Não esta na soluçao otima, possui custos negativos");
        }

        return todosPositivos;
    }

    /**
     * Passo 4 - Calcula a direção simplex
     */
    public void calculaDirecaoSimplex()
    {
        System.out.println("");    
        
        System.out.println("Calculando y");
        printMatrix(problema.getMatrizBasicas());
        System.out.println("x");
        printMatrix2(problema.getColuna(problema.getVNBasica().get(k-1)));
        
        
        y = Gauss.gauss(problema.getMatrizBasicas(), problema.getColuna(problema.getVNBasica().get(k-1)));

        System.out.println("");
        System.out.println("Valor do y");
        printMatrix2(y);
        System.out.println("");
    }

    /**
     * Passo 5 - Calcular candidato a sair da base
     */
    public void calculaCandidato()
    {
        if (!checaYPositivo())
        {
            System.out.println("O problema possui infinitas solucoes");
            System.exit(0);
        }

        double emin = Double.MAX_VALUE;
        e = 0;
        double min;

        for (int i = 0; i < y.length; i++)
        {
            if (y[i] > 0)
            {
                min = problema.getXb()[i] / y[i];
                if (min < emin)
                {
                    emin = min;
                    e = i + 1;
                }
            }
        }

        System.out.println("Menor e = " + e + " (" + emin + ") ");
    }

    /**
     * Passo 6 - Atualiza as variaveis
     */
    public void atualizaVariaveis()
    {
        //printVariaveis();

        System.out.println("Sai da base: " + e);
        System.out.println("Entra na base: " + k);

        Integer saiBase = problema.getVBasica().get(e - 1);
        Integer entraBase = problema.getVNBasica().get(k - 1);

        problema.getVBasica().set(e - 1, entraBase);
        problema.getVNBasica().set(k - 1, saiBase);

        System.out.println("Novas variaveis");
        printVariaveis();
    }

    public boolean checaYPositivo()
    {
        boolean positivo = false;

        for (int i = 0; i < y.length; i++)
        {
            if (y[i] > 0)
            {
                positivo = true;
            }
        }

        return positivo;
    }

    public void printFuncao()
    {
        int i = 1;
        for (Double v : problema.getFuncaoPadrao().getValues())
        {
            System.out.print(v + "x" + i + "\t");
            i++;
        }
        System.out.println("");

        for (Expressao e : problema.getRestricoes())
        {
            i = 1;
            for (Double v : e.getValues())
            {
                System.out.print(v + "x" + i + "\t");
                i++;
            }

            System.out.print(e.getTipo().getValue() + "\t");

            System.out.println(e.getB());
        }

        i = 1;
        for (int b : problema.getVBasica())
        {
            System.out.println("B" + i + ": " + b + " ");
            i++;
        }

        i = 1;
        for (int b : problema.getVNBasica())
        {
            System.out.println("N" + i + ": " + b + " ");
            i++;
        }
    }

    public static void printMatrix(double[][] matrix)
    {
        for (int i = 0; i < matrix.length; i++)
        {
            for (int j = 0; j < matrix[i].length; j++)
            {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println("");
        }
    }

    public static void printMatrix(double[] matrix)
    {
        for (int i = 0; i < matrix.length; i++)
        {
            System.out.print(matrix[i] + "\t");
        }
        System.out.println("");

    }

    public static void printMatrix2(double[] matrix)
    {
        for (int i = 0; i < matrix.length; i++)
        {
            System.out.println(matrix[i]);
        }
    }

    public void printVariaveis()
    {
        int i = 1;
        for (int b : problema.getVBasica())
        {
            System.out.print("B" + i + ": " + b + " ");
            i++;
        }

        System.out.println("");
        i = 1;
        for (int b : problema.getVNBasica())
        {
            System.out.print("N" + i + ": " + b + " ");
            i++;
        }

        System.out.println("");
    }
}

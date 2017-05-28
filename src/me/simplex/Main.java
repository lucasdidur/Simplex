/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.simplex;

import java.io.File;
import java.io.PrintStream;
import me.simplex.util.Gauss;

/**
 *
 * @author Lucas
 */
public class Main
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {

        int n = 3;
        double[][] A =
        {
            {
                1, 0, 0
            },
            {
                0, 1, 0
            },
            {
                0, 0, 1
            }
        };
        double[] b =
        {
            6, 4, 4
        };
        double[] x = Gauss.gauss(A, b);

        // print results
        for (int i = 0; i < n; i++)
        {
            //System.out.println(x[i]);
        }

        Simplex simplex = new Simplex();
        simplex.calcula();
    }

}

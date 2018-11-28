/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab09;

import java.util.Scanner;

/**
 *
 * @author Hoai Tran
 */
public class Mip_nopipeline {
    public static void main(String[] args){
        System.out.println("NO PIPELINE:");
        Scanner sc = new Scanner(System.in);
        System.out.println("Nhap r-type:");
        double r_type = sc.nextDouble();
        System.out.println("Nhap lw:");
        double lw = sc.nextDouble();
        System.out.println("Nhap sw:");
        double sw = sc.nextDouble();
        System.out.println("Nhap re nhanh:");
        double re_nhanh = sc.nextDouble();
        System.out.println("Nhap jump:");
        double jump = sc.nextDouble();
        System.out.println("Nhap tan so CPU - GHZ:");
        double CPU = sc.nextDouble();
        double cpi = r_type*4+lw*5+sw*4+re_nhanh*3+jump*3;
        System.out.println();
        System.out.println("CPI =: "+cpi);
        double mips = CPU*Math.pow(10, 9)/(cpi*Math.pow(10, 6));
        System.out.println("MIPS =: "+mips);
    }
   
}

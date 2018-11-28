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
public class mip_pipeline {
    public static void main(String[] args){
        System.out.println("PIPELINE:");
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
        System.out.println("Lenh doc phu thuoc dl - ton chu ky:");
        double ptdl = sc.nextDouble();
        int ck1 = sc.nextInt();
        double hs_lw = ptdl*ck1 + (1-ptdl)*1;
        System.out.println("Lenh re nhanh du doan sai - ton chu ky:");
        double doansai = sc.nextDouble();
        int ck2 = sc.nextInt();
        double hs_rn = doansai*ck2 + (1-doansai)*1;
        System.out.println("ck lenh nhay khong dk:");
        int ck3 = sc.nextInt();
        
        double cpi = r_type*1+lw*hs_lw+sw*1+re_nhanh*hs_rn+jump*ck3;
        System.out.println();
        System.out.println("CPI =: "+cpi);
        double mips = CPU*Math.pow(10, 9)/(cpi*Math.pow(10, 6));
        System.out.println("MIPS =: "+mips);
}
}

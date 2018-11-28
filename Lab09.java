/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab09;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class Lab09 {
    public static int[][] tiles;
    public static int width,height;
    public static void readContentFromFile(String path) {
        //String s="";
        try{
        FileInputStream fis = new FileInputStream(path);
        InputStreamReader isr = new InputStreamReader(fis);
	BufferedReader br = new BufferedReader(isr);
	String line = br.readLine();
        String[] arr = line.split(" ");
        height = Integer.parseInt(arr[0]);
        width = Integer.parseInt(arr[1]);
        tiles = new int[height+1][width+1];
        int row = 0;
        while(line != null){
            char[] arrchar = line.toCharArray();
            for(int i=0; i <arrchar.length; i++){
                if(arrchar[i] == '#') tiles[row][i] = 1;
                if(arrchar[i] == '*') tiles[row][i] = 2;
                if(arrchar[i] == ' ') tiles[row][i] = 0;
            }
            row++;
            line = br.readLine();
        }
        fis.close();
        isr.close();
        br.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
   
    
    
    public static void main(String[] args) {
        readContentFromFile("map1.txt");
        System.out.println(width+" "+height);
        for(int i=1; i<height+1; i++){
            for(int j=0; j<width; j++){
                System.out.print(tiles[i][j]+" ");
            }
            System.out.println();
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab09;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Phuc Tran
 */
public class MergeSort {
    public static int[] mergeSort(int[] arr){
        int n = arr.length;
        if(n == 1) return arr; // mang co 1 phan tu
        int len = n/2;
        int[] l1 = new int[len];
        for(int i=0; i<len; i++){
            l1[i] = arr[i];
        }
        int[] l2 = new int[n - len];
        for(int i=0; i<l2.length; i++){
            l2[i] = arr[i+len];
        }
        l1 = mergeSort(l1);
        l2 = mergeSort(l2);//de qui
        return merge(l1,l2);
    }
    public static int[] merge(int[]a, int[] b){
        //int len =a.length + b.length;
        List<Integer> list = new ArrayList<>();
        int i=0, j=0;
        while(i<a.length && j<b.length){
            if(a[i]<b[j]){
                list.add(a[i]);
                i++;
            }
            else{
                list.add(b[j]);
                j++;
            }
        }
        if(i == a.length){
            for(;j<b.length; j++){
                list.add(b[j]);
            }
        }
        else{
            for(;i<a.length; i++){
                list.add(a[i]);
            }
        }
        int[] c = new int[list.size()];
        for(int z=0; z<list.size(); z++){
            c[z] = list.get(z);
        }
        return c;
    }
    public static void main(String[]args){
        int[]a = {2,4,7,9};
        int[]c = {-20, -3916237, -357920, -3620601, 7374819, -7330761, 30, 6246457, -6461594, 266854, -520, -470 };
        int[]b = mergeSort(c);
        for(int i=0; i<c.length; i++){
            System.out.print(b[i]+" ");
        }
    }
}

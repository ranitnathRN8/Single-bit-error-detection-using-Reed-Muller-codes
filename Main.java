package com.wn;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        Encoding encoding = new Encoding();
        Decoding decoding = new Decoding();
        ReedMullerHelper helper = new ReedMullerHelper();
        int[] A = {0,1,1,0,1,1,1};
        ArrayList<Integer> Msg = new ArrayList<>();

        for (int i = 0; i < A.length; i++){
            Msg.add(Integer.valueOf(A[i]));
        }
        ArrayList<Integer> message = encoding.encodedMsg(A);
        print1D(message);

        int[] B = {0,0,1,1,0,1,1,1,1,0,1,1};
        decoding.decodeMsg(B);
    }

    static void print2D(ArrayList<ArrayList<Integer>> X){
        for(int i=0; i<X.size(); i++){
            for(int j=0; j<X.get(i).size(); j++){
                System.out.print(X.get(i).get(j) + " - ");
            }
            System.out.println();
        }
    }

    static void print1D(ArrayList<Integer> X){
        for(int i=0; i<X.size(); i++){
            System.out.print(X.get(i) + " ");
        }
        System.out.println("");
    }
}

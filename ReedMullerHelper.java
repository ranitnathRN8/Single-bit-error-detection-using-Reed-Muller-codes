package com.wn;

import java.util.ArrayList;

public class ReedMullerHelper {
    public int[][] generateReedMuller(int m) {
        int[][] A = {{1, 0}, {1, 1}};
        if (m == 1) {
            return A;
        }
        int x = (int) Math.pow(2, m);
        int[][] B = new int[x][x];
        int[][] C = new int[x][x];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[i].length; j++) {
                B[i][j] = A[i][j];
                C[i][j] = A[i][j];
            }
        }
        int rowa = 2, rowb = 2, cola = 2, colb = 2;
        while (m != 1) {
            //            copy C to B for next iteration
            B = C;

            for (int i = 0; i < rowa; i++) {
                // k loops till rowb
                for (int k = 0; k < rowb; k++) {
                    // j loops till cola
                    for (int j = 0; j < cola; j++) {
                        // l loops till colb
                        for (int l = 0; l < colb; l++) {
                            C[i * rowb + k][j * colb + l] = A[i][j] * B[k][l];
                        }
                    }
                }
            }
            m--;
            rowb = colb = 2 * rowb;
        }
        return C;
    }

    public ArrayList<ArrayList<Integer>> generateReedMullerMatrix(int m){
        int[][] matrix = generateReedMuller(m);
        ArrayList<ArrayList<Integer>> RMmatrix = new ArrayList<>();

        int n = (int) Math.pow(2, m);
        for (int i =0; i<n; i++){
            ArrayList<Integer> section = new ArrayList<>();
            for(int j=0; j<n; j++){
                section.add(Integer.valueOf(matrix[i][j]));
            }
            RMmatrix.add(section);
        }

        return RMmatrix;
    }
}

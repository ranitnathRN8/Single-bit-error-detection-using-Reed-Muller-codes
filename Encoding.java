package com.wn;

import java.util.*;

public class Encoding {
    ReedMullerHelper RMhelper = new ReedMullerHelper();

    // Method to find the next power of 2 for a given message size
    private int nextPowerOf2(int n)
    {
        int p = 1;
        if (n > 0 && (n & (n - 1)) == 0)
            return n;

        while (p < n)
            p <<= 1;

        return p;
    }

    // Method to find the exponent of 2 required to form the Reed-Muller matrix
    private int[] findM(ArrayList<Integer> A){
        int length = A.size();
        int x = nextPowerOf2(length);
        int m =0, y = x;

        while(y!=1){
            y = y/2;
            m++;
        }

        int res[] = {x,m};
        return res;
    }

    // Method to add 0's to the MSB of the message to change the size to 2^m
    private ArrayList<Integer> fixInput(ArrayList<Integer> A, int m, int x){
        // Decide on number of zeros to add at the MSB
        int diff = x - A.size();

        // Add 0's to MSB
        while(diff!=0){
            A.add(0, 0);
            diff--;
        }

        return A;
    }

    //Method for Kronecker process of multiplication
    private ArrayList<Integer> kronecker(ArrayList<ArrayList<Integer>> RM, ArrayList<Integer> msg, int n, int m){
        // Initialize a hashmap for storing the databits with value 1
        HashMap<Integer, ArrayList<Integer>> dMap = new HashMap<>();

        // Reverse the msg for multiplication
        Collections.reverse(msg);

        // Initialize a result arraylist
        ArrayList<Integer> res = new ArrayList<>();

        // Loop through to fill up the result list
        for(int i=0; i<n; i++){
            for(int j=0; j<n; j++){
                int x = RM.get(i).get(j) * msg.get(j);

                // Check if res is not empty. Then get the xor of current value and new value
                if(res.size()-1>=i){
                    res.set(i, res.get(i) ^ x);
                }
                // If empty, then insert new value only
                else
                    res.add(x);

                // if value of multiplication is 1 then that databit needs to be stored in the hashmap
                if(RM.get(i).get(j)==1){
                    ArrayList<Integer> checkList = dMap.get(i);
                    if(checkList == null){
                        checkList = new ArrayList<>();
                        checkList.add(j);
                        dMap.put(i, checkList);
                    }
                    else{
                        checkList.add(j);
                        dMap.put(i, checkList);
                    }
                }
            }
        }

        // Initialize a hashset for comparison of datawords
        HashSet<Integer> dSet = new HashSet<>();
        for(int i=0; i<n; i++){
            dSet.add(i);
        }

        // Initialize no of selected codewords as 1 in the result arraylist
        ArrayList<Integer> cResult = new ArrayList<>();
        // The last codeword is always selected as it contains all datawords
        cResult.add(n-1);
        int selectedCodeWords = 1;

        // Move from the end of the hashmap to check next codeword
        for(int key=n-2; key>=0; key--){
            int selectedDatawords =0;
            // Continue check only if the hashset of datawords is not empty
            if(!dSet.isEmpty()){
                ArrayList<Integer> dCheck = dMap.get(key);
                for (int j=0; j < dCheck.size(); j++){
                    if (dSet.contains(dCheck.get(j))){
                        dSet.remove(dCheck.get(j));
                        selectedDatawords++;
                    }
                }
                if (selectedDatawords!=0){
                    cResult.add(key);
                    selectedCodeWords++;
                }
                if(selectedCodeWords==(m+1))
                    break;
            }
        }

        // Reverse the msg for final message formation
        Collections.reverse(msg);

        // Get the value of the selected codewords from res list
        for(int k=0; k<cResult.size(); k++){
            cResult.set(k, res.get(cResult.get(k)));
        }
        Collections.reverse(cResult);

        // Insert codewords at the end of the message bits
        for(int k=0; k<cResult.size(); k++){
            msg.add(cResult.get(k));
        }
        return msg;
    }

    // Main method to encode the input message
    public ArrayList<Integer> encodedMsg(int[] msg){
        //Declare a arraylist to return as the encoded message
        ArrayList<Integer> Msg = new ArrayList<>();
        for (int i = 0; i < msg.length; i++){
            // Construct the message arraylist
            Msg.add(Integer.valueOf(msg[i]));
        }

        int[] xm = findM(Msg);
        //Store the values of:
        // m-> The power of 2
        // x-> The actual size of the received message
        int orgSize = xm[0];
        int m = xm[1];

        // Add 0's to the MSB to change the size of the message to orgSize
        Msg = fixInput(Msg, m, orgSize);

        // Generate Reed-Muller matrix with size (2^m * 2^m)
        ArrayList<ArrayList<Integer>> RMmatrix = RMhelper.generateReedMullerMatrix(m);

        // Kronecker operation to generate the codewords and final manipulation to get the encoded message
        int n = (int) Math.pow(2, m);
        Msg = kronecker(RMmatrix, Msg, n, m);

        // Return the encoded message
        return Msg;
    }
}

package com.wn;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

public class Decoding {
    ReedMullerHelper RMhelper = new ReedMullerHelper();

    // method to find the previous power of 2 for a number
    public int previousPowerOf2(int n)
    {
        while ((n & n - 1) != 0) {
            n = n & n - 1;
        }

        return n;
    }

    // Method to find the exponent of 2 required to form the Reed-Muller matrix
    private int[] findM(ArrayList<Integer> A){
        int length = A.size();
        int x = previousPowerOf2(length);
        int m =0, y = x;

        while(y!=1){
            y = y/2;
            m++;
        }

        int res[] = {x,m};
        return res;
    }

    // Method to separate the code section from the message section
    private ArrayList<Integer> seperateCodeSection (ArrayList<Integer> msg, int n){
        ArrayList<Integer> codeSection = new ArrayList<>();

        for(int i=n; i<msg.size(); i++){
            codeSection.add(msg.get(i));
        }

        return codeSection;
    }

    // Method to separate the message section from the code section
    private ArrayList<Integer> seperateMsgSection (ArrayList<Integer> msg, int n){
        ArrayList<Integer> msgSection = new ArrayList<>();

        for(int i=0; i<n; i++){
            msgSection.add(msg.get(i));
        }

        return msgSection;
    }

    // Determine codebits for new databits
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

        return cResult;
    }

    // XOR of old and new code sections
    private ArrayList<Integer> XOR(ArrayList<Integer> cOld, ArrayList<Integer> cNew, int n){
        ArrayList<Integer> xorRes = new ArrayList<>();

        for(int i=0; i<=n; i++){
            xorRes.add(cOld.get(i) ^ cNew.get(i));
        }
        Collections.reverse(xorRes);

        return xorRes;
    }

    // Determine section containing error
    private int errorSection(ArrayList<Integer> syndrome){
        int count =0;
        for (int i =0; i<syndrome.size(); i++){
            if(syndrome.get(i)==1)
                count++;
        }
        if(count == 0)
            return 0;
        else if(count == 1)
            return 1;
        else if(count == syndrome.size())
            return 2;
        else
            return 3;
    }

    // Detect error in codebits
    private int detectCodeError(ArrayList<Integer> syndrome){
        int code = -1;
        Collections.reverse(syndrome);
        for (int i=0; i<syndrome.size(); i++){
            if (syndrome.get(i) == 1)
                code = i;
        }
        return code;
    }

    // Remove error in codebits
    private ArrayList<Integer> removeCodeError(ArrayList<Integer> codeSection, int error){
        if (codeSection.get(error) == 1)
            codeSection.set(error, 0);
        else
            codeSection.set(error, 1);

        return codeSection;
    }

    // Detect error in the databits
    private int detectDataError(ArrayList<Integer> syndrome){
        int corruptedBit = 0;
        for (int i =0; i<syndrome.size(); i++){
            if(syndrome.get(i) == 0){
                if(i==0)
                    corruptedBit += 0;
                else
                    corruptedBit += (int) Math.pow(2, i-1);
            }
        }
        return corruptedBit;
    }

    // Process the message to remove detect and remove error
    private ArrayList<Integer> correctedMsg(ArrayList<Integer> msgSection, int error){
        Collections.reverse(msgSection);
        if(msgSection.get(error) == 1)
            msgSection.set(error, 0);
        else
            msgSection.set(error, 1);
        Collections.reverse(msgSection);
        return msgSection;
    }

    // main method to decode the message by detecting any error in it
    public void decodeMsg(int[] msg){
        //Declare a arraylist to return as the decoded message
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

        // Separate the code section and the message section
        ArrayList<Integer> msgSection = seperateMsgSection(Msg, orgSize);
        ArrayList<Integer> codeSection = seperateCodeSection(Msg, orgSize);

        // Generate Reed-Muller matrix with size (2^m * 2^m)
        ArrayList<ArrayList<Integer>> RMmatrix = RMhelper.generateReedMullerMatrix(m);

        // Kronecker operation to generate the new codewords
        int n = (int) Math.pow(2, m);
        ArrayList<Integer> codeSectionNew = kronecker(RMmatrix, msgSection, n, m);

        // Detect error in the transmitted message
        ArrayList<Integer> syndrome = XOR(codeSection, codeSectionNew, m);
        int category = errorSection(syndrome);

        if(category == 0){
            System.out.println("No errors in the transmitted message");
        }
        else if (category == 1){
            int error = detectCodeError(syndrome);
            System.out.println("Error in codebit "+error);
            codeSection = removeCodeError(codeSection, error);
        }
        else if (category == 2){
            System.out.println("Error in message bit 0");
            if(msgSection.get(n-1) == 1)
                msgSection.set(n-1, 0);
            else
                msgSection.set(n-1, 1);
        }
        else{
            int error = detectDataError(syndrome);
            System.out.println("Error in message bit "+error);
            msgSection = correctedMsg(msgSection, error);
        }

        System.out.println("Updated message: ");
        for (int i =0; i<msgSection.size(); i++){
            System.out.print(msgSection.get(i)+" ");
        }
        System.out.println("");

        System.out.println("Updated code section: ");
        for (int i =0; i<codeSection.size(); i++){
            System.out.print(codeSection.get(i)+" ");
        }
    }
}

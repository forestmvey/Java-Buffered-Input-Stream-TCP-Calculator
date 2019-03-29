package DoubleImplementationCalculator;

/**
 *
 * @author Forest Vey
 */

import java.io.*;
import java.net.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;



public class ClientB {
   
    public static byte twoBitPacker(int a, int b){
        byte c = (byte)(b | (a << 4));
        return c;
    }
    public static void main(String[] args) throws IOException{
        String host = args[0];
        String port = args[1];
        String operatorS = args[2];
        
        // if needing to escape * because using powershell change it to a regular asterisk
        if(operatorS.equals("* ")){
            operatorS ="*";
        }
        
        int[] values = new int[args.length - 3];
        byte passedValues = (byte)(args.length - 3);
        byte[] packet = new byte[(((passedValues + 1)/2) + 2)];
        byte operator = (byte)0;

        try{
            // pack all command args into an array
            for(int i = 0; i < args.length-3; i++){
                values[i] = Integer.parseInt(args[i + 3]);
            }
        }catch(NumberFormatException nError){
            System.out.println(nError);
        }

        if (operatorS.equals("+")){
            operator = (byte)1;
        }else if(operatorS.equals("-")){
            operator = (byte)2;
        }else if(operatorS.equals("*")){
            operator = (byte)4;
        }else{
            System.out.println("please enter a valid operator");
        }

        packet[0] = operator;
        packet[1] = passedValues;
       

        int toPack = 2;
        int x = 3;
       

        while(x < passedValues + 3){
            if(x == passedValues + 2){
                packet[toPack] = (byte)((values[x-3] << 4) & (0xff));
                toPack += 1;
                x += 1;
            }else{
                packet[toPack] = twoBitPacker(values[x-3], values[x-2]);
                toPack += 1;
                x += 2;
            }
        }
        try{
        
        Socket serverSock = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        BufferedReader bR = null;
        DataInputStream dIS = null;
       
        if(host.equals("localhost")){
            serverSock = new Socket("127.0.0.1", Integer.parseInt(port));
        }else{
            serverSock = new Socket(host, Integer.parseInt(port));
        }
        in = new BufferedInputStream(serverSock.getInputStream());
        out = new BufferedOutputStream(serverSock.getOutputStream());
        bR = new BufferedReader(new InputStreamReader(serverSock.getInputStream()));
        dIS = new DataInputStream(new DataInputStream(serverSock.getInputStream()));

        // recieve ready from the server before sending packet for calculation
        String ready = bR.readLine();
       
        out.write(packet);
        out.flush();

        // read in the resulting integer value
        int result = dIS.readInt();


        System.out.println(result);
        in.close();
        out.close();
        serverSock.close();
    
        }catch(IOException err){
            System.out.println(err);
        }
        
    } 
}

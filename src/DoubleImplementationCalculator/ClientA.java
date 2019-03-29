package DoubleImplementationCalculator;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Forest Vey
 */
public class ClientA {
        
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
        byte[] readyBytes = new byte[7];

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
       
        if(host.equals("localhost")){
            serverSock = new Socket("127.0.0.1", Integer.parseInt(port));
        }else{
            serverSock = new Socket(host, Integer.parseInt(port));
        }
        in = new BufferedInputStream(serverSock.getInputStream());
        out = new BufferedOutputStream(serverSock.getOutputStream());

        // recieve ready from the server before sending packet for calculation
        in.read(readyBytes);
        String ready = new String(readyBytes, "UTF-8");
       
        out.write(packet);
        out.flush();
        

        byte[] result = new byte[4];
        for(int i = 0; i < result.length; i ++){
            result[i] = (byte)in.read();
        }

        // unpack the byte array to a meaningful value.
        // each integer represented in 4 bits, first bit tells if positive/negative integer
        int finalVal = result[0] << 24 | (result[1] << 16 & (0xff0000)) | (result[2] << 8 & (0xff00)) | (result[3] & (0xff)); 
        
        // if byte array is signed, value is negative, handle by unsigning and giving negative value
        if((finalVal >> 31 & 1) == 1){
            finalVal = finalVal ^ (0x80000000);
            finalVal *= -1;
        }
        System.out.println(finalVal);
        in.close();
        out.close();
        serverSock.close();
    
        }catch(IOException err){
            System.out.println(err);
        }
        
    }
}

package DoubleImplementationCalculator;

/**
 *
 * @author forest Vey
 */

import java.io.*;
import java.net.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

public class ServerB {
  // unpack the first number in first 4 bits in byte
    public static int firstByteNum(byte x){
        x = (byte)(x & 240);
        int a = ((x & 240) >> 4);
        return a;
    }
    // unpack the second number in last 4 bits in byte
    public static int secondByteNum(byte x){
        x = (byte)(x & 15);
        int a = (x & 15);
        return a;
    }
    public static void main(String[] args) throws IOException{

        boolean loop = true;
        ServerSocket serverSock = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        PrintWriter pWOut = null;
        DataOutputStream dOS = null;


        while(loop){
            try{
                String port = args[0];
                serverSock = new ServerSocket(Integer.parseInt(port));
                Socket clientSock = serverSock.accept();
                String ready = "READY\n";
                
                
                // send a ready message after initial connection
                in = new BufferedInputStream(clientSock.getInputStream());
                out = new BufferedOutputStream(clientSock.getOutputStream());
                pWOut = new PrintWriter(clientSock.getOutputStream());
                dOS = new DataOutputStream(clientSock.getOutputStream());

                pWOut.append(ready);
                pWOut.flush();

                byte[] packet = new byte[7];
                in.read(packet);
                int operator = packet[0];
                int numVals = packet[1];
                int operand = 2;
                int result = 0;

                // if bit 0 - add operands
                if((byte)(operator & 1) == 1){
                    for(int x = 0; x < ((numVals + 1) / 2); x ++){
                        if((x == ((numVals + 1) / 2)) & (numVals % 2 != 0)){
                            result += firstByteNum(packet[operand]);
                            operand += 1;
                        }else{
                            result += firstByteNum(packet[operand]) + secondByteNum(packet[operand]);
                            operand += 1;
                        }
                    }
                    dOS.writeInt(result);
                    dOS.flush();
                }
                // operation is minus
                else if((byte)(operator & 2) == 2){
                    result = firstByteNum(packet[operand]);
                    if(packet.length > 2){
                        result -= secondByteNum(packet[operand]);
                    }
                    operand += 1;

                    for(int x = 0; x < ((numVals - 1)/2); x++){
                        if(x == ((numVals-1) /2)){
                            result -= firstByteNum(packet[operand]);
                        }else{
                            result -= firstByteNum(packet[operand]);
                            result -= secondByteNum(packet[operand]);
                            operand += 1;
                        }
                    }
                    // if result is negative, sign the integer before building array
                    dOS.writeInt(result);
                    dOS.flush();
                }
                // operation is multiplication
                else if((byte)(operator & 4) == 4){
                    result = 1;
                    for(int x = 0; x < ((numVals + 1)/2); x++){
                        if((x == ((numVals + 1)/2 - 1)) & (numVals % 2 != 0)){
                        result *= firstByteNum(packet[operand]);
                        operand += 1;
                        }else{
                            result *= firstByteNum(packet[operand]);
                            result *= secondByteNum(packet[operand]);
                            operand += 1;
                        }
                    }
                    dOS.writeInt(result);
                    dOS.flush();
                }
            
                in.close();
                out.close();
                clientSock.close();
                serverSock.close();
            }catch(IOException err){
                System.out.println("catch error = " + err);
            }
            
        }
    }
}

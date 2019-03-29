package DoubleImplementationCalculator;

/**
 *
 * @author forest Vey
 */
import java.io.*;
import java.net.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;


public class ServerA {
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
    public static byte[] packPacket(int a, boolean negative){
        byte[] retPacket = new byte[4];
        
        if(negative){
            // if packet is negative sign the first byte in array
            retPacket[0] = (byte)(((a >> 24) & 255) | (0x80));
        }else{
            retPacket[0] = (byte)((a >> 24) & 255);
        }
        retPacket[1] = (byte)((a >> 16) & 255);
        retPacket[2] = (byte)((a >> 8) & 255);
        retPacket[3] = (byte)(a & 255);
        return retPacket;
    }
    public static void main(String[] args) throws IOException{
        try{
        boolean loop = true;
        ServerSocket serverSock = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        
        String port = args[0];
        serverSock = new ServerSocket(Integer.parseInt(port));
     
        String ready = "READY\n";
        byte[] readyMes = ready.getBytes("UTF-8");


        while(loop){
                Socket clientSock = serverSock.accept();

                // send a ready message after initial connection
                in = new BufferedInputStream(clientSock.getInputStream());
                out = new BufferedOutputStream(clientSock.getOutputStream());

                out.write(readyMes);
                out.flush();
                

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
                    byte[] toSend = packPacket(result, false);
                    out.write(toSend);
                    out.flush();
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
                    byte[] toSend;
                    if(result < 0){
                        result = Math.abs(result);
                        toSend = packPacket(result, true);
                    }else{
                        toSend = packPacket(result, false);
                    }
                    out.write(toSend);
                    out.flush();
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
                    byte[] toSend = packPacket(result, false);
                    out.write(toSend);
                    out.flush();
                }
            
                in.close();
                out.close();
                clientSock.close();
            
        }
        serverSock.close();
             }catch(IOException err){
                System.out.println("catch error = " + err);
            }
    }
}

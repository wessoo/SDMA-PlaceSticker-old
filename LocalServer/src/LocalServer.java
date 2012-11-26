import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class LocalServer {

    private static String imgIndexFile = "../imgIndex.txt";

    public static void main(String[] args){
        ServerSocket serverSocket = null;
        Socket socket = null;
        DataInputStream dataInputStream = null;

        ArrayList<String> index = new ArrayList<String>();

        try {
        serverSocket = new ServerSocket(8800);
        System.out.println("Listening :8800");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println( "Fail Open Port 8888" );
        }
	
        while(true){
            try {
                socket = serverSocket.accept();
                dataInputStream = new DataInputStream(socket.getInputStream());
                //System.out.println("ip: " + socket.getInetAddress());
        
                String input = dataInputStream.readUTF();
                System.out.println(input + " ");

                if( input.equals( "create" ) ) {
                    clearFile();
                    for( int i = 0; i<index.size(); i++ ) {
                        try{
                            //Write to file
                            FileWriter fw = new FileWriter( imgIndexFile,true );
                            BufferedWriter out = new BufferedWriter( fw );
                            out.write(index.get(i) + " ");
                            //Close the output stream
                            out.close();
                        }catch (Exception e){
                            System.err.println("Error: " + e.getMessage());
                        }
                    }
                    index.clear();
                    continue;
                }
                if( input.equals( "0" ) ) {
                    index.clear();
                    clearFile();
                    continue;
                }
                index.add( input );
    
            } catch (IOException e) {
                System.err.println("Error: " + e.getMessage());
            }
            finally{
                if( socket!= null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
     
                if( dataInputStream!= null){
                    try {
                        dataInputStream.close();
                    } catch (IOException e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            }//finally
        }//while true
    }//main

    private static void clearFile() {
        try {
            FileWriter fw = new FileWriter( imgIndexFile );
            BufferedWriter out = new BufferedWriter( fw );
            out.write("");
        } catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}//EOF

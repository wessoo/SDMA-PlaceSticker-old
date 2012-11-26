import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
 
public class AndroidServer
{
    public static ArrayList<String> authorArr = new ArrayList<String>();
    public static ArrayList<String> titleArr  = new ArrayList<String>();
    public static ArrayList<String> descArr   = new ArrayList<String>();
    public static ArrayList<String> imgUrlArr = new ArrayList<String>();
    public static ArrayList<String> vidUrlArr = new ArrayList<String>();
    public static ArrayList<String> roomArr  = new ArrayList<String>();
    
    public static void main(String[] args)
    {
        readXML( "artworkInfo.xml" );
        new AndroidServer();
    }
     
    public AndroidServer()
    {
        try {
            ServerSocket sSocket = new ServerSocket(8880);
            System.out.println("Server started at 8880"); 
            
            //foreverloop the incoming user
            while(true) {
                //Wait for a client to connect
                Socket sockets = sSocket.accept();
                //thread for each user
                ClientThread cT = new ClientThread(sockets);
                //Start the thread!
                new Thread(cT).start();     
            }
        } catch(IOException exception) {
            System.out.println("Error: " + exception);
        }
    }
     
    //recreate a thread
    class ClientThread implements Runnable
    {
        Socket threadSocket;
        

        public ClientThread(Socket socketin) 
        {
            threadSocket = socketin;
        }
        
        public void run()
        {
        	try {
                //streams
                PrintWriter output = new PrintWriter(threadSocket.getOutputStream(), true);
                BufferedReader input = new BufferedReader(new InputStreamReader(threadSocket.getInputStream()));

                String inputString = input.readLine();
                
                //read imgIndex from file
                if( inputString.equals( "connected" ) ) {
                    
                    ArrayList<String> updatedTitleArr  = new ArrayList<String>();
                    ArrayList<String> updatedAuthorArr = new ArrayList<String>();
                    ArrayList<String> updatedDescArr   = new ArrayList<String>();
                    ArrayList<String> updatedImgUrlArr = new ArrayList<String>();
                    ArrayList<String> updatedVidUrlArr = new ArrayList<String>();
                    ArrayList<String> chosenArr        = new ArrayList<String>();
                    ArrayList<String> updatedRoomArr  = new ArrayList<String>();
                    
                    try{
                        FileInputStream fstream = new FileInputStream("../imgIndex.txt");
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;

                        if ((strLine = br.readLine()) != null) {

                            String imgIndex = "";
                            for( int i = 0; i<strLine.length(); i++ ) {
                                char c = strLine.charAt(i);
                                if( c != ' ' ) {
                                    imgIndex += c;
                                } else {
                                    chosenArr.add( imgIndex );
                                    imgIndex = "";
                                }
                            }
                            for( int i=0; i<chosenArr.size(); i++ ) {
                                int chosenImg = Integer.parseInt(chosenArr.get(i)) - 1;
                                updatedDescArr.add( descArr.get( chosenImg ) );
                                updatedTitleArr.add( titleArr.get( chosenImg ) );
                                updatedAuthorArr.add( authorArr.get( chosenImg ) );
                                updatedImgUrlArr.add( imgUrlArr.get( chosenImg ) );
                                updatedVidUrlArr.add( vidUrlArr.get( chosenImg ) );
                                updatedRoomArr.add( roomArr.get( chosenImg ) );
                            }
                        }
                        
                        //Close the input stream
                        in.close();
                        
                        for( int i = 0; i<chosenArr.size(); i++ ) {
                            output.println( chosenArr.get(i) );  
                        }
                        output.println( "token sent" );

                        for( int i = 0; i<updatedTitleArr.size(); i++ ) {
                            output.println( updatedTitleArr.get(i) );
                        }
                        output.println( "titles sent" );
                        
                        for( int i = 0; i<updatedAuthorArr.size(); i++ ) {
                            output.println( updatedAuthorArr.get(i) );
                        }
                        output.println( "authors sent" );
                        
                        for( int i = 0; i<updatedDescArr.size(); i++ ) {
                            output.println( updatedDescArr.get(i) );
                        }
                        output.println( "desc sent" );
                        
                        for( int i = 0; i<updatedImgUrlArr.size(); i++ ) {
                            output.println( updatedImgUrlArr.get(i) );
                        }
                        output.println( "imgurl sent" );
                        
                        for( int i = 0; i<updatedVidUrlArr.size(); i++ ) {
                            output.println( updatedVidUrlArr.get(i) );
                        }
                        output.println( "vidurl sent" );
                        
                        for( int i = 0; i<updatedRoomArr.size(); i++ ) {
                            output.println( updatedRoomArr.get(i) );
                        }
                        output.println( "room sent" );

                    }catch (Exception e){//Catch exception if any
                        System.err.println("Error: " + e.getMessage());
                    }
                }
            } catch(NumberFormatException e) {
                System.err.println("imgIndex cannot be parsed: " + e);
            } catch( IOException e ) {
                System.err.println("Socket error: " + e);
            }
        }//run
    }//ClientThread
    
    private static void readXML( String xmlPath )
    {
        try {
            
            File fXmlFile = new File( xmlPath );
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
     
            NodeList nList = doc.getElementsByTagName("artwork");
     
            for (int temp = 0; temp < nList.getLength(); temp++) {
     
                Node nNode = nList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
     
                    titleArr.add( getTagValue("title", eElement) );
                    authorArr.add( getTagValue("author", eElement) );
                    descArr.add( getTagValue("description", eElement) + "\n//end" );
                    imgUrlArr.add( getTagValue("imgurl", eElement) );
                    vidUrlArr.add( getTagValue("vidurl", eElement) );
                    roomArr.add( getTagValue( "room", eElement) );
                }
            }//for
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//readXML
    
    private static String getTagValue(String sTag, Element eElement) 
    {
        NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        return nValue.getNodeValue();
    }//getTagValue

}//AndroidServer

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;


public class imageServer {
    public static void main(String[] args) throws IOException{
    	if(args.length >0){
        	for(int i = 0; i < args.length; i++){
            	String dirPath = "wineimage.png"; 
            	int port = Integer.parseInt(args[i]);
            	ServerSocket imageServer = new ServerSocket(port);
            	System.out.println("image server on");
            	Socket csock = imageServer.accept();
            	
            
            	try {
        			System.out.println("image server ready");
            		ObjectInputStream ois = new ObjectInputStream(csock.getInputStream());
					byte[] ba = (byte[]) ois.readObject();
					ois.close();
					FileOutputStream fos = new FileOutputStream(dirPath);
					fos.write(ba);
					fos.flush();											
            		System.out.println("image server got image");
            		fos.close();
					
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	
            	
            		
            
            	
            	csock.close();
            	imageServer.close();
        	}
    	}
    }
}

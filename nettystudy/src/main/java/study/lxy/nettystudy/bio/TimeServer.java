package study.lxy.nettystudy.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TimeServer {
	
	public static void main(String[] args) {
		
		int port = 8080;
		ServerSocket ss = null;
		
		try{
			ss = new ServerSocket(port);
			System.out.println("time server start in port : " + port);
			Socket socket =  null;
			while(true){
				
				socket = ss.accept();
				new Thread(new TimeServerHandle(socket)).start();
				
			}
			
			
		} catch(Exception ex){
			ex.printStackTrace();
		}finally{
			
			if(ss != null){
				try {
					ss.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}
		
	}

}

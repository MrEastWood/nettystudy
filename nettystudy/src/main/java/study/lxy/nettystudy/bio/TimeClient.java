package study.lxy.nettystudy.bio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TimeClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Socket socket = null;
		int port = 8080;
		String dest = "127.0.0.1";
		
		BufferedReader in = null;
		PrintWriter out = null;
		
		try{
			socket = new Socket(dest, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			
			System.out.println("send to server : " + "QUERY TIME");
			out.println("QUERY TIME");
			System.out.println("receive from server : " + in.readLine());
			
			System.out.println("send to server : " + "aaa");
			out.println("aaa");
			System.out.println("receive from server : " + in.readLine());
			out.println("");
			
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			try{
				
				if(in != null){
					in.close();
				}
				
				if(out != null){
					out.close();
				}
				
				if(socket != null){
					socket.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
	}

}

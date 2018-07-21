package study.lxy.nettystudy.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

public class TimeServerHandle implements Runnable {

	Socket socket;
	
	public TimeServerHandle(Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(),true);
			
			String message = null;
			String currentTime = null;
			while(true){
				message = in.readLine();
				
				if(message == null || "".equals(message)){
					System.out.println("break");
					break;
				}
				currentTime = "QUERY TIME".equals(message) ? new Date().toString() : "BAD ORDER";
				out.println(currentTime);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				if (in != null) {
					in.close();
				}
				
				if(out != null){
					out.close();
				}
				
				if(socket != null){
					socket.close();
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	

}

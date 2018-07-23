package study.lxy.nettystudy.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

public class AIOTimeClient {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new TimeClientHandler("127.0.0.1", 8080)).start();
	}

}

class TimeClientHandler implements Runnable{
	
	String host;
	int port;
	AsynchronousSocketChannel channel;
	CountDownLatch latch;
	
	public TimeClientHandler(String host,int port) {
		// TODO Auto-generated constructor stub
		try {
			this.host = host;
			this.port = port;
			channel = AsynchronousSocketChannel.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		latch = new CountDownLatch(1);
		channel.connect(new InetSocketAddress(host, port), this, new ConnectCompletionHandler() );
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

class ConnectCompletionHandler implements CompletionHandler<Void, TimeClientHandler>{

	@Override
	public void completed(Void result, TimeClientHandler attachment) {
		// TODO Auto-generated method stub
		byte[] bytes = "GET TIME".getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		attachment.channel.write(buffer, buffer, new WriteCompletionHandler(attachment));
		System.out.println("SEND : " + "GET TIME");
	}

	@Override
	public void failed(Throwable exc, TimeClientHandler attachment) {
		// TODO Auto-generated method stub
		System.out.println("error");
		exc.printStackTrace();
		attachment.latch.countDown();
		
	}
	
}

class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer>{
	
	AsynchronousSocketChannel channel;
	CountDownLatch latch;
	
	public WriteCompletionHandler(TimeClientHandler handler){
		
		this.channel = handler.channel;
		this.latch = handler.latch;
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		// TODO Auto-generated method stub
		if(attachment.hasRemaining()){
			channel.write(attachment, attachment, this);
		}else{
			ByteBuffer buffer = ByteBuffer.allocate(1024);
			channel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>(){

				@Override
				public void completed(Integer result, ByteBuffer attachment) {
					// TODO Auto-generated method stub
					attachment.flip();
					byte[] bytes = new byte[attachment.remaining()];
					attachment.get(bytes);
					try {
						String resp = new String(bytes,"UTF-8");
						System.out.println("RECEIVED : " + resp);
						latch.countDown();
						
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

				@Override
				public void failed(Throwable exc, ByteBuffer attachment) {
					// TODO Auto-generated method stub
					try {
						channel.close();
						exc.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
			
			
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		// TODO Auto-generated method stub
		try {
			channel.close();
			exc.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}


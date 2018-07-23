package study.lxy.nettystudy.aio;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

public class AIOTimeServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new AsyncServerHandler(8080)).start();
	}

}


class AsyncServerHandler implements Runnable{
	
	private int port;
	AsynchronousServerSocketChannel asynchronousServerSocketChannel;
	CountDownLatch latch;
	
	public AsyncServerHandler(int port){
		
		this.port = port;
		
		try {
			asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
			asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		latch = new CountDownLatch(1);
		doAccept();
		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
	private void doAccept(){
		
		asynchronousServerSocketChannel.accept(this, new AcceptCompletionHandler());
		
	}
	
}

class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel,AsyncServerHandler>{

	@Override
	public void completed(AsynchronousSocketChannel result,
			AsyncServerHandler attachment) {
		// TODO Auto-generated method stub
		attachment.asynchronousServerSocketChannel.accept(attachment,this);
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		result.read(buffer, buffer, new ReadCompletionHandler(result));
	}

	@Override
	public void failed(Throwable exc, AsyncServerHandler attachment) {
		// TODO Auto-generated method stub
		attachment.latch.countDown();
	}
}

class ReadCompletionHandler implements CompletionHandler<Integer,ByteBuffer>{
	
	AsynchronousSocketChannel asynchronousSocketChannel;
	
	public ReadCompletionHandler(AsynchronousSocketChannel asynchronousSocketChannel){
		
		this.asynchronousSocketChannel = asynchronousSocketChannel;
		
	}

	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		// TODO Auto-generated method stub
		attachment.flip();
		byte[] bytes = new byte[attachment.remaining()];
		
		attachment.get(bytes);
		String msg = "";
		try {
			msg = new String(bytes,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("received : " + msg);
		doWrite(new Date().toString());
		
		
	}
	
	private void doWrite(String resp){
		
		System.out.println("RESP : " + resp);
		byte[] bytes = resp.getBytes();
		
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		
		asynchronousSocketChannel.write(buffer,buffer,new CompletionHandler<Integer, ByteBuffer>() {

			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				// TODO Auto-generated method stub
				if(buffer.hasRemaining()){
					asynchronousSocketChannel.write(buffer, buffer, this);
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				// TODO Auto-generated method stub
				try {
					asynchronousSocketChannel.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		// TODO Auto-generated method stub
		try {
			asynchronousSocketChannel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

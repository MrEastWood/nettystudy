package study.lxy.nettystudy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Date;


public class TimeServerRunnerNIO implements Runnable {

	private ServerSocketChannel channel;
	private Selector selector;
	
	public TimeServerRunnerNIO(){
		
		try{
			selector = Selector.open();
			channel = ServerSocketChannel.open();
			channel.configureBlocking(false);
			channel.socket().bind(new InetSocketAddress(8080),1024);
			channel.register(selector,SelectionKey.OP_ACCEPT);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			
			try {
				selector.select(1000);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			Set<SelectionKey> keySet = selector.selectedKeys();
			Iterator<SelectionKey> it = keySet.iterator();
			
			while(it.hasNext()){
				SelectionKey key = it.next();
				it.remove();
				try{
					handleKey(key);
				}catch(Exception ex){
					key.cancel();
					try {
						key.channel().close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private void handleKey(SelectionKey key) throws Exception{
		
		if(key.isValid()){
			if(key.isAcceptable()){
				ServerSocketChannel channel = (ServerSocketChannel)key.channel();
				SocketChannel clientChannel = channel.accept();
				clientChannel.configureBlocking(false);
				clientChannel.register(selector, SelectionKey.OP_READ);
				System.out.println("RECEIVE A CONNECTION");
			}
			
			if(key.isReadable()){
				
				SocketChannel channel = (SocketChannel)key.channel();
				ByteBuffer buffer = ByteBuffer.allocate(1024);
				int size = channel.read(buffer);
				
				if(size > 0){
					buffer.flip();
					byte[] bytes = new byte[buffer.remaining()];
					buffer.get(bytes);
					
					String command = new String(bytes,"UTF-8");
					System.out.println("RECEIVE : " + command);
					
					String resp = "GET TIME".equals(command) ? new Date().toString() : "BAD COMMAND";
					if("GET TIME".equals(command)){
						doWrite(channel,resp);
					}
					
					
				}else if(size < 0){
					key.cancel();
					key.channel().close();
				}
				
				
			}
		}
	}
	
	private void doWrite(SocketChannel channel,String resp) throws Exception{
		
		byte[] bytes = resp.getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		channel.write(buffer);
		System.out.println("RESPONSE : " + resp);
	}

}

package study.lxy.nettystudy.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TimeClientRunnerNIO implements Runnable{

	private Selector selector;
	private SocketChannel channel;
	private boolean finished = false;
	
	public TimeClientRunnerNIO(String host,int port){
		
		try {
			selector = Selector.open();
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			channel.register(selector, SelectionKey.OP_CONNECT);
			channel.connect(new InetSocketAddress(host, port));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(!finished){
			try {
				selector.select(1000);
				Set<SelectionKey> keySet = selector.selectedKeys();
				Iterator<SelectionKey> it = keySet.iterator();
				
				while(it.hasNext()){
					SelectionKey key = it.next();
					it.remove();
					handleKey(key);
				}
				
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void handleKey(SelectionKey key) throws IOException{
		
		if(key.isValid()){
			SocketChannel channel = (SocketChannel) key.channel();
			if(key.isConnectable()){
				if(channel.finishConnect()){
					doWrite(channel);
					channel.register(selector, SelectionKey.OP_READ);
				}
			}
			
			if(key.isReadable()){
				
				doRead(channel);
				key.cancel();
				channel.close();
				finished = true;
			}
			
		}
		
	}
	
	private void doWrite(SocketChannel channel) throws IOException{
		
		byte[] bytes = "GET TIME".getBytes();
		ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
		buffer.put(bytes);
		buffer.flip();
		channel.write(buffer);
		System.out.println("SEND : " + "GET TIME");
	}
	
	private void doRead(SocketChannel channel) throws IOException{
		
		ByteBuffer buffer = ByteBuffer.allocate(0124);
		channel.read(buffer);
		buffer.flip();
		
		byte[] bytes = new byte[buffer.remaining()];
		buffer.get(bytes);
		
		String resp = new String(bytes,"UTF-8");
		System.out.println("RECEIVE : " + resp);
		
		
	}
}

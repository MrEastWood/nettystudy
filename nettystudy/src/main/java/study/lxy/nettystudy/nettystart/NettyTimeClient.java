package study.lxy.nettystudy.nettystart;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyTimeClient {
	
	public static void main(String[] args) throws Exception {
		
		new NettyTimeClient().connect("127.0.0.1", 8080);
		
	}
	
	public void connect(String host,int port) throws Exception{
		
		EventLoopGroup group = new NioEventLoopGroup();
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast(new ChannelHandlerAdapter(){
						
						@Override
						public void channelActive(ChannelHandlerContext ctx){
							
							byte[] req = "GET TIME".getBytes();
							for(int i = 0;i < 100;i++){
								ByteBuf buf = Unpooled.buffer(req.length);
								buf.writeBytes(req);
								ctx.writeAndFlush(buf);
								System.out.println("SEND : " + "GET TIME");
							}
							
						}

						@Override
						public void channelRead (ChannelHandlerContext ctx,
								Object msg) throws Exception {
							// TODO Auto-generated method stub
							ByteBuf buf = (ByteBuf) msg ;
							byte[] bytes = new byte[buf.readableBytes()];
							buf.readBytes(bytes);
							String resp = new String(bytes,"UTF-8");
							System.out.println("RECEIVE : " + resp);
							ctx.close();
						}

						@Override
						public void exceptionCaught(ChannelHandlerContext ctx,
								Throwable cause) throws Exception {
							// TODO Auto-generated method stub
							ctx.close();
						}
					});
				}
			});
			
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
			
		}finally{
			group.shutdownGracefully();
		}
		
	}

}

package study.lxy.nettystudy.msgpack;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClientWithMsgpack {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new NettyClientWithMsgpack().connect("127.0.0.1", 8080);
	}
	
	public void connect(String host,int port) throws InterruptedException{
		
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group);
			b.channel(NioSocketChannel.class);
			b.option(ChannelOption.TCP_NODELAY, true);
			b.handler(new ChildChannelHandler());
			
			ChannelFuture f = b.connect(host,port).sync();
			
			f.channel().closeFuture().sync();
		}finally{
			group.shutdownGracefully();
		}
		
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// TODO Auto-generated method stub
			ch.pipeline().addLast(new MsgPackEncoder());
			ch.pipeline().addLast(new MsgPackDecoder());
			ch.pipeline().addLast(new ChannelHandlerAdapter(){

				@Override
				public void exceptionCaught(ChannelHandlerContext ctx,
						Throwable cause) throws Exception {
					// TODO Auto-generated method stub
					System.out.println("EXCEPTION");
					ctx.close();
				}

				@Override
				public void channelActive(ChannelHandlerContext ctx)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("CONNECTED");
					UserInfo ui = new UserInfo();
					ui.setName("liu xing yi");
					ui.setAge(29);
					ctx.writeAndFlush(ui);
				}

				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("receive : " + msg);
				}

				
			});
			
		}

		
		
		
		
	}

}

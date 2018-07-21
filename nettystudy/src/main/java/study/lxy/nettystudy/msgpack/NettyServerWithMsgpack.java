package study.lxy.nettystudy.msgpack;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServerWithMsgpack {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new NettyServerWithMsgpack().startServer(8080);
	}

	public void startServer(int port) throws InterruptedException {
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup);
			sb.channel(NioServerSocketChannel.class);
			sb.option(ChannelOption.SO_BACKLOG, 1024);
			sb.childHandler(new ChildChannelHandler());
			ChannelFuture f = sb.bind(port).sync();
			
			f.channel().closeFuture().sync();
			
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
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
				public void channelRead(ChannelHandlerContext ctx, Object msg)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("RECEIVE MESSAGE");
					System.out.println("TYPTE OF MESSAGE : " + msg.getClass().getTypeName());
					System.out.println("VALUE OF MESSAGE : " + msg);
					UserInfo ui = (UserInfo)msg;
					System.out.println("received : " + msg);
					ui.setAge(ui.getAge() + 1);
					
					ctx.writeAndFlush(ui);
				}

				@Override
				public void channelReadComplete(ChannelHandlerContext ctx)
						throws Exception {
					// TODO Auto-generated method stub
					ctx.flush();
				}

				@Override
				public void exceptionCaught(ChannelHandlerContext ctx,
						Throwable cause) throws Exception {
					// TODO Auto-generated method stub
					ctx.close();
				}
				
			});
			
		}
		
	} 
}

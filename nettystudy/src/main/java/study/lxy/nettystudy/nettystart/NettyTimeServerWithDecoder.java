package study.lxy.nettystudy.nettystart;

import java.util.Date;

import io.netty.bootstrap.ServerBootstrap;
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
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

public class NettyTimeServerWithDecoder {
	
	public void bind(int port) throws Exception{
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.childHandler(new ChildChannelHandler());
			
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
		
	}
	
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			// TODO Auto-generated method stub
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new ChannelHandlerAdapter() {
				
				@Override
				public void channelRead(ChannelHandlerContext ctx,Object msg) throws Exception{
					
					String body = (String) msg;
					System.out.println("receive command : " + body);
					
					ByteBuf resp = Unpooled.copiedBuffer((new Date().toString()+ System.lineSeparator()).getBytes());
					ctx.writeAndFlush(resp);
					System.out.println("RESPONSE : " + resp);
					
				}
				
				@Override
				public void channelReadComplete(ChannelHandlerContext ctx) throws Exception{
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
	
	public static void main(String[] args) throws Exception {
		
		new NettyTimeServerWithDecoder().bind(8080);
		
	}
}

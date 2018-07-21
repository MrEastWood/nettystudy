package study.lxy.nettystudy.protobuf;

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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import study.lxy.nettystudy.msgpack.MsgPackDecoder;
import study.lxy.nettystudy.msgpack.MsgPackEncoder;
import study.lxy.nettystudy.msgpack.UserInfo;
import study.lxy.nettystudy.protobuf.format.MyRequestProto.MyRequest;
import study.lxy.nettystudy.protobuf.format.MyResponseProto.MyResponse;

public class NettyServerWithProto {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new NettyServerWithProto().startServer(8080);
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
			
			ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
			ch.pipeline().addLast(new ProtobufDecoder(MyRequest.getDefaultInstance()));
			ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
			ch.pipeline().addLast(new ProtobufEncoder());
			ch.pipeline().addLast(new ChannelHandlerAdapter(){

				@Override
				public void channelRead(ChannelHandlerContext ctx, Object msg)
						throws Exception {
					// TODO Auto-generated method stub
					System.out.println("RECEIVE MESSAGE");
					MyRequest req = (MyRequest)msg;
					System.out.println("received : " + msg);
					System.out.println("function : " + req.getFunction());
					System.out.println("data : " + req.getReqdata());
					
					MyResponse resp = MessageFunc.buildResponse("0000", new String("交易成功".getBytes(),"UTF-8"), new String("liuxingyi,男,29".getBytes(),"UTF-8"));
					ctx.writeAndFlush(resp);
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

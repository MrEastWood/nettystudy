package study.lxy.nettystudy.protobuf;

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
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import study.lxy.nettystudy.msgpack.UserInfo;
import study.lxy.nettystudy.protobuf.format.MyRequestProto.MyRequest;
import study.lxy.nettystudy.protobuf.format.MyResponseProto.MyResponse;

public class NettyClientWithProto {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		new NettyClientWithProto().connect("127.0.0.1", 8080);
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
			ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
			ch.pipeline().addLast(new ProtobufDecoder(MyResponse.getDefaultInstance()));
			ch.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
			ch.pipeline().addLast(new ProtobufEncoder());
			
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
					MyRequest req = MessageFunc.buildRequest("F0001", "liuxy");
					ctx.writeAndFlush(req);
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

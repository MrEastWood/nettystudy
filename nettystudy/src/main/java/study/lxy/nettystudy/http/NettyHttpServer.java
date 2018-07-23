package study.lxy.nettystudy.http;

import org.junit.runner.Request;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

public class NettyHttpServer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new NettyHttpServer().start("127.0.0.1", 8080);;
	}
	
	public void start(String host,int port){
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try{
			ServerBootstrap sb = new ServerBootstrap();
			sb.group(bossGroup, workerGroup);
			sb.channel(NioServerSocketChannel.class);
			sb.childHandler(new ChannelInitializer<SocketChannel>() {

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					// TODO Auto-generated method stub
					ch.pipeline().addLast("http-decoder",new HttpRequestDecoder());
					ch.pipeline().addLast("http-aggregator",new HttpObjectAggregator(65536));
					ch.pipeline().addLast("http-encoder",new HttpResponseEncoder());
					ch.pipeline().addLast("http-chunked",new ChunkedWriteHandler());
					ch.pipeline().addLast("http-serverHandler",new HttpServerHandler());
				}
			});
			ChannelFuture future = sb.bind(host, port).sync();
			
			future.channel().closeFuture().sync();
		}catch(Exception e){
			
		}
		
	}
	
	

}

class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest>{

	@Override
	protected void messageReceived(ChannelHandlerContext ctx,
			FullHttpRequest msg) throws Exception {
		// TODO Auto-generated method stub
		if(!msg.decoderResult().isSuccess()){
			sendError(ctx, HttpResponseStatus.BAD_REQUEST);
		}
		
		if(!msg.method().equals(HttpMethod.GET)){
			sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
		}
		// 返回消息
		sendReturn(ctx);
		
	}
	
	private static void sendError(ChannelHandlerContext ctx,HttpResponseStatus status){
		
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				status, Unpooled.copiedBuffer("Failure: " + status.toString()
					+ "\r\n", CharsetUtil.UTF_8));
			response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
			ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	private static void sendReturn(ChannelHandlerContext ctx){
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
		String msg = "this is a message from http server";
		ByteBuf buff = Unpooled.copiedBuffer(msg,CharsetUtil.UTF_8);
		response.content().writeBytes(buff);
		buff.release();
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
	}
	
	
	
}

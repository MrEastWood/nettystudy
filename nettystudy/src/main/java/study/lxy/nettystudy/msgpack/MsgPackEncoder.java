package study.lxy.nettystudy.msgpack;

import org.msgpack.MessagePack;

import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MsgPackEncoder extends MessageToByteEncoder<Object> {

	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out)
			throws Exception {
		// TODO Auto-generated method stub
		try{
		System.out.println("BEFORE ENCODE : " + msg);
		MessagePack msgPack = new MessagePack();
		byte[] bytes = msgPack.write(msg);
		System.out.println("bytes : " + bytes);
		out.writeBytes(bytes);
		System.out.println("AFTER ENCODE : " + out);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}

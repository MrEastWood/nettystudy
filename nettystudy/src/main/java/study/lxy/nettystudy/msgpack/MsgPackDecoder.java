package study.lxy.nettystudy.msgpack;

import java.util.List;

import org.msgpack.MessagePack;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf msg,
			List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		try{
		System.out.println("BEFORE DECODE : " + msg);
		MessagePack msgPack = new MessagePack();
		int length = msg.readableBytes();
		byte[] bytes = new byte[length];
		msg.getBytes(msg.readerIndex(), bytes,0, length);
		System.out.println("AFTER DECODE : " + msgPack.read(bytes));
		out.add(msgPack.read(bytes));
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}

package study.lxy.nettystudy;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

import com.google.protobuf.InvalidProtocolBufferException;

import study.lxy.nettystudy.protobuf.format.MyRequestProto;
import study.lxy.nettystudy.protobuf.format.MyRequestProto.MyRequest;

public class ProtoBufTest {

	@Test
	public void test1() throws Exception{
		MyRequest req = createReq();
		System.out.println(req.toString());
		System.out.println("DATA : " + req.getReqdata());
		byte[] bytes = encoode(req);
		System.out.println("encode : " + bytes);
		System.out.println("decode : " + decode(bytes));
		
	}
	
	private MyRequest createReq() throws Exception{
		
		MyRequest.Builder builder = MyRequest.newBuilder();
		
		builder.setFunction("F001");
		builder.setReqdata("liuxy,001,中文测试");
		return builder.build();
		
	}
	
	private byte[] encoode(MyRequest req){
		return req.toByteArray();
	}
	
	private MyRequest decode(byte[] body){
		
		try {
			return MyRequest.parseFrom(body);
		} catch (InvalidProtocolBufferException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
		
	}
}

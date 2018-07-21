package study.lxy.nettystudy.protobuf;

import study.lxy.nettystudy.protobuf.format.MyRequestProto.MyRequest;
import study.lxy.nettystudy.protobuf.format.MyResponseProto.MyResponse;

public class MessageFunc {
	
	public static MyRequest buildRequest(String func,String data){
		
		MyRequest.Builder builder = MyRequest.newBuilder();
		builder.setFunction(func);
		builder.setReqdata(data);
		return builder.build();
		
	}
	
	public static MyResponse buildResponse(String msgid,String msg,String data){
		
		MyResponse.Builder builder = MyResponse.newBuilder();
		builder.setMsg(msg);
		builder.setMsgId(msgid);
		builder.setRespdata(data);
		return builder.build();
		
	}

}

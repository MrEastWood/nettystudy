package study.lxy.nettystudy.msgpack;

import org.msgpack.annotation.Message;

@Message
public class UserInfo {
	private String name;
	private int age;
	
	
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString(){
		return "[name : " + name + ", age : " + age + "]";
	}
	

}

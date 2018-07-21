package study.lxy.nettystudy.nio;

public class TimeClientNIO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new TimeClientRunnerNIO("127.0.0.1", 8080)).start();
	}

}

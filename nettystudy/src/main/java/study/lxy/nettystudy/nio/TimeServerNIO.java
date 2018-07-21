package study.lxy.nettystudy.nio;

public class TimeServerNIO {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Thread(new TimeServerRunnerNIO()).start();
	}

}

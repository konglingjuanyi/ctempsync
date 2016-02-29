package log4j.appender.test;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Test {
	public static void main(String args[]) throws Exception {
		DatagramSocket ds = null;
		DatagramPacket dp = null;
		ds = new DatagramSocket();
		String str = "hello, woshi java client hahahah";
		dp = new DatagramPacket(str.getBytes(), str.length(), InetAddress.getByName("127.0.0.1"), 8888);
		ds.send(dp);
		ds.close();
	}
}

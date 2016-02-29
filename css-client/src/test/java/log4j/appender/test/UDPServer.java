package log4j.appender.test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPServer {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		//InetAddress address = InetAddress.getLocalHost();  
        int port = 8888;    
          
        //创建DatagramSocket对象  
        DatagramSocket socket = new DatagramSocket(port);  
          
        byte[] buf = new byte[1024];  //定义byte数组  
        DatagramPacket packet = new DatagramPacket(buf, buf.length);  //创建DatagramPacket对象  
          
        //通过套接字接收数据
        do {
        	System.out.println("dfdf");
        	socket.receive(packet);
        	System.out.println("弄得弄");
            
            String getMsg = new String(buf, 0, packet.getLength());  
            System.out.println("客户端发送的数据为：" + getMsg);  
        }
        while (true) ;
	}

}

package com.cmread.cmu.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.net.SocketNode;
import org.apache.log4j.spi.LoggingEvent;

public class UDPAppender extends AppenderSkeleton {

	/**
	 * The default port number of remote logging server (4560).
	 * 
	 * @since 1.2.15
	 */
	static public final int DEFAULT_PORT = 7890;

	/**
	 * We remember host name as String in addition to the resolved InetAddress
	 * so that it can be returned via getOption().
	 */
	String remoteHost;

	InetAddress address;
	int port = DEFAULT_PORT;
	
	boolean locationInfo = false;
	private String application;

	int counter = 0;
	DatagramSocket socket;

	public UDPAppender() throws SocketException {
		socket = new DatagramSocket();  //创建套接字  
	}

	/**
	 * Connect to the specified <b>RemoteHost</b> and <b>Port</b>.
	 */
	public void activateOptions() {
	}

	/**
	 * Close this appender.
	 *
	 * <p>
	 * This will mark the appender as closed and call then {@link #cleanUp}
	 * method.
	 */
	synchronized public void close() {
		if (closed)
			return;

		this.closed = true;
		
		if ((socket != null) && (!socket.isClosed())) {
			socket.close();
		}
	}

	public void append(LoggingEvent event) {
		if (event == null)
			return;

		if (address == null) {
			errorHandler.error("No remote host is set for SocketAppender named \"" + this.name + "\".");
			return;
		}
		
		if (locationInfo) {
			event.getLocationInformation();
		}
		if (application != null) {
			event.setProperty("application", application);
		}
		
		String formatedMsg = this.getLayout().format(event);
		sendUdpMessage(formatedMsg);
	}
	
	private void sendUdpMessage(String msg) {
        try {
        	byte[] buf = msg.getBytes("UTF-8");
    		DatagramPacket dataGramPacket = new DatagramPacket(buf, buf.length, address, port);  
            
			socket.send(dataGramPacket);
		} catch (IOException e) {
		} 
	}

	static InetAddress getAddressByName(String host) {
		try {
			return InetAddress.getByName(host);
		} catch (Exception e) {
			LogLog.error("Could not find address of [" + host + "].", e);
			return null;
		}
	}

	/**
	 * The SocketAppender does not use a layout. Hence, this method returns
	 * <code>false</code>.
	 */
	public boolean requiresLayout() {
		return true;
	}

	/**
	 * The <b>RemoteHost</b> option takes a string value which should be the
	 * host name of the server where a {@link SocketNode} is running.
	 */
	public void setRemoteHost(String host) {
		address = getAddressByName(host);
		remoteHost = host;
	}

	/**
	 * Returns value of the <b>RemoteHost</b> option.
	 */
	public String getRemoteHost() {
		return remoteHost;
	}

	/**
	 * The <b>Port</b> option takes a positive integer representing the port
	 * where the server is waiting for connections.
	 */
	public void setPort(int port) {
		this.port = port;
	}

	/**
	 * Returns value of the <b>Port</b> option.
	 */
	public int getPort() {
		return port;
	}

	/**
	 * The <b>LocationInfo</b> option takes a boolean value. If true, the
	 * information sent to the remote host will include location information. By
	 * default no location information is sent to the server.
	 */
	public void setLocationInfo(boolean locationInfo) {
		this.locationInfo = locationInfo;
	}

	/**
	 * Returns value of the <b>LocationInfo</b> option.
	 */
	public boolean getLocationInfo() {
		return locationInfo;
	}

	/**
	 * The <b>App</b> option takes a string value which should be the name of
	 * the application getting logged. If property was already set (via system
	 * property), don't set here.
	 */
	public void setApplication(String lapp) {
		this.application = lapp;
	}

	/**
	 * Returns value of the <b>Application</b> option.
	 */
	public String getApplication() {
		return application;
	}

}

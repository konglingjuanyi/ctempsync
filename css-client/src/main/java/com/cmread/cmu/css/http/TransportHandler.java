package com.cmread.cmu.css.http;

import com.cmread.cmu.css.http.client.RemoteTaskException;

/**
 * 这个类不应该这么设计的，但是主要为了可测试性做了妥协。目的是为了将客户端与服务器桥起来。
 * 
 * @author zhangtieying
 *
 */
public interface TransportHandler {

	String transInvoke(String reqUrl, String reqXmlStr, String clientIp) throws RemoteTaskException;
	
}

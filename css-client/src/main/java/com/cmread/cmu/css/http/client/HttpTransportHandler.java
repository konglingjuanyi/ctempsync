package com.cmread.cmu.css.http.client;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.http.TransportHandler;

public class HttpTransportHandler implements TransportHandler {
	
	private static Logger logger = LoggerFactory.getLogger(HttpTransportHandler.class);
	
	private int connectionTimeout;
	private int readTimeout;
	
	public HttpTransportHandler() {
		this.connectionTimeout = 3000;
		this.readTimeout = 90000;
	}
	
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}
	
	@Override
	public String transInvoke(String reqUrl, String reqXmlStr, String clientIp) throws RemoteTaskException {
		if ((reqUrl == null) || (reqUrl.length() == 0)) {
			// 当没有配置url的时候，直接跳过向服务器端发送请求（主要为测试考虑）
			return "";
		}
		
		HttpClient client = new HttpClient();
		
		// FIXME 后面逻辑需要增加对连接超时和响应超时的处理
		// 连接超时：
		client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
		// 读取超时：
		client.getHttpConnectionManager().getParams().setSoTimeout(readTimeout);
		
		// Create a method instance.
		PostMethod post = new PostMethod(reqUrl);

		try {
			RequestEntity entity = new StringRequestEntity(reqXmlStr, "text/xml", "UTF-8");
			post.setRequestEntity(entity);

			// FIXME 超时处理！！！
			logger.info("csync client invoke '{}' create new task. {}", reqUrl, reqXmlStr);
			
			// Execute the method.
			int statusCode = client.executeMethod(post);

			// 算了，不搞RESTful了 :)
			if (statusCode != HttpStatus.SC_OK) {
				// 服务器处理异常，并且没有正常创建任务，需要对任务进行持久化保存以备后期恢复；
				// 配合告警，此处统一添加错误日志
				logger.error("[csync-failed] receive response {}" + post.getStatusLine());
				return null;
			}

			// Read the response body.
			byte[] responseBody = post.getResponseBody();

			String response = new String(responseBody, "UTF-8");
			logger.info("csync client receive response {}. content : {}", statusCode, response);
			return response;
		} catch (HttpException e) {
			throw new RemoteTaskException("Fatal protocol violation", e);
		} catch (IOException e) {
			throw new RemoteTaskException("create task failed.", e);
		} finally {
			post.releaseConnection(); //必须释放 FIXME 
		}
	}
}

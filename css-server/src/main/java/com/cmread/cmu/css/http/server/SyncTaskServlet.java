package com.cmread.cmu.css.http.server;
import java.io.IOException;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.HttpRequestHandler;

import com.cmread.cmu.css.http.TransportHandler;
import com.cmread.cmu.css.http.server.utils.ServletUtils;

/**
 * spring提供的servlet入口。
 * 
 * 这个类仅作请求信息提取和响应的返回，具体处理通过TransportHandler实现；
 * 
 * 加入TransportHandler中间层主要是为了单元测试时可以直接将客户端api和服务器跳过
 * HTTP和Servlet容器而直接桥接起来，这样，可以通过模拟客户端测试包括编解码在内的
 * 大部分逻辑；
 * 
 * @author zhangtieying
 *
 */
public class SyncTaskServlet implements HttpRequestHandler {
	
	private static Logger logger = LoggerFactory.getLogger(SyncTaskServlet.class);
	
	private TransportHandler transHandler;

	public void setTransportHandler(TransportHandler handler) {
		this.transHandler = handler;
	}

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// 放在这里略显不合适，但是为了更好的日志跟踪，暂时放在这里，这个id当做taskID来用吧。
		String traceID = UUID.randomUUID().toString();
		MDC.put("traceID", traceID);
		
		try {
			// 取出消息体
			String requestBody = ServletUtils.getPostBody(request, "UTF-8");
			String clientIp = request.getRemoteAddr();

			String responseStr = transHandler.transInvoke(null, requestBody, clientIp);

			response.setContentType("text/xml;charset=UTF-8");
			response.getWriter().write(responseStr);
			response.getWriter().close();
		} catch (Exception e) {
			logger.error("process client request failed.", e);
			throw new IOException(e);
		} finally {
			MDC.clear();
		}
	}


}
package com.cmread.cmu.css.http.server.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

public abstract class ServletUtils {

	/**
	 * 读取请求消息体
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	public static String getPostBody(HttpServletRequest request, String encoding) throws IOException {
		StringBuilder sb = new StringBuilder(request.getContentLength());
		String line = null;
		request.setCharacterEncoding(encoding);
		while (null != (line = request.getReader().readLine())) {
			sb.append(line);
		}
		return sb.toString();
	}
	
}

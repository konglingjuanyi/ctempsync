package com.cmread.cmu.css.http.client;

import java.security.CodeSource;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.http.SyncTaskMessageXmlBuilder;
import com.cmread.cmu.css.http.SyncTaskRequest;
import com.cmread.cmu.css.http.SyncTaskResponse;
import com.cmread.cmu.css.http.TransportHandler;

public class CSSHttpClient implements CSSClient {
	
	private static Logger logger = LoggerFactory.getLogger(CSSHttpClient.class);
	
	private String postUrl;
	private TransportHandler transHandler;

	public void setServerURL(String url) {
		this.postUrl = url;
	}

	public void setTransportHandler(TransportHandler hand) {
		this.transHandler = hand;
	}

	@Override
	public SyncTaskResponse createTask(String taskType, Map<String, String> contentID) throws RemoteTaskException {
		try {
			// 在这里加入客户端版本
			contentID.put("client", this.getClientJarVersion());
			
			SyncTaskRequest taskMsg = new SyncTaskRequest();
			taskMsg.setTaskType(taskType);
			taskMsg.setContent(contentID);
			taskMsg.setCreateTime(new Date());

			SyncTaskMessageXmlBuilder xb = new SyncTaskMessageXmlBuilder();
			String reqXmlStr = xb.convertRequestToXmlStr(taskMsg);

			// 将消息封装为xml消息，并通过transportHandler发送；
			String responseStr = transHandler.transInvoke(postUrl, reqXmlStr, null);
			

			if ((responseStr == null) || (responseStr.length() == 0)) {
				return null;
			}
			SyncTaskResponse response = xb.convertXmlStrToResponse(responseStr);
			return response;
		} catch (Exception e) {
			//throw new RemoteTaskException("csync client create task failed.", e);
			// 配合告警，此处统一添加错误日志
			logger.error("[csync-failed]", e);
			return null;
		}
	}
	
	/**
	 * 获得客户端jar包的文件名；
	 * @return
	 */
	private String getClientJarVersion() {
		try {
			CodeSource codeSource = this.getClass().getProtectionDomain().getCodeSource();
			if (codeSource != null) {
				String jarFilePath = codeSource.getLocation().getFile();
				if ((jarFilePath != null) && jarFilePath.endsWith(".jar")) {
					//仅在jar包情况下上传版本号；
					int index = jarFilePath.lastIndexOf("/");
					if (index == -1) {
						index = jarFilePath.lastIndexOf("\\");
					}
					if (index != -1) {
						return jarFilePath.substring(index+1);
					}
				}
			}
		} catch (Exception e) {
		}
		return "";
	}

}

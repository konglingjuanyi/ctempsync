package com.cmread.cmu.css.http.server;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.cmread.cmu.css.http.SyncTaskResponse;
import com.cmread.cmu.css.http.client.CSSClient;
import com.cmread.cmu.css.http.client.RemoteTaskException;

/**
 * CSSClient的代理类，用于csync-server中内部调用css-client接口时设置MDC（备份、设置新的MDC、恢复等）
 * 
 * @author zhangtieying
 *
 */
public class CSSTraceClientStub implements CSSClient {

	private static Logger logger = LoggerFactory.getLogger(CSSTraceClientStub.class);

	private CSSClient realClient;

	public void setCSSClient(CSSClient client) {
		this.realClient = client;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public SyncTaskResponse createTask(String taskType, Map<String, String> contentID) throws RemoteTaskException {
		// 备份MDC
		Map oldMDC = MDC.getCopyOfContextMap();

		String traceID = UUID.randomUUID().toString();
		MDC.put("traceID", traceID);

		SyncTaskResponse resp = null;
		try {
			resp = realClient.createTask(taskType, contentID);
			return resp;
		} finally {
			MDC.clear();
			if (oldMDC != null) {
				MDC.setContextMap(oldMDC);
			}

			if (resp != null) {
				logger.info("create sub sync task. taskid = {}", resp.getTaskID());
			}
		}
	}

}

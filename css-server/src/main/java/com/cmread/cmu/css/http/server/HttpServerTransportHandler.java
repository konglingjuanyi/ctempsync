package com.cmread.cmu.css.http.server;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cmread.cmu.css.http.SyncTaskMessageXmlBuilder;
import com.cmread.cmu.css.http.SyncTaskRequest;
import com.cmread.cmu.css.http.SyncTaskResponse;
import com.cmread.cmu.css.http.TransportHandler;
import com.cmread.cmu.css.task.SyncTask;
import com.cmread.cmu.css.task.SyncTaskServer;
import com.cmread.cmu.css.task.TaskException;
import com.cmread.cmu.css.task.TaskSourceInfo;

public class HttpServerTransportHandler implements TransportHandler {
	
	private static Logger logger = LoggerFactory.getLogger(HttpServerTransportHandler.class);

	private SyncTaskServer taskServer;
	
	public void setSyncTaskServer(SyncTaskServer taskServer) {
		this.taskServer = taskServer;
	}
	
	@Override
	public String transInvoke(String reqUrl, String reqXmlStr, String clientIp) {
		logger.debug("receive task request (from {}) {}", clientIp, reqXmlStr);
		
		SyncTaskMessageXmlBuilder xb = new SyncTaskMessageXmlBuilder();
		SyncTaskRequest request = xb.convertXmlStrToRequest(reqXmlStr);
		
		String taskType = request.getTaskType();
		Map<String, String> content = request.getContent();
		
		TaskSourceInfo source = new TaskSourceInfo();
		source.setClientStartTime(request.getCreateTime());
		source.setMessage(reqXmlStr);
		source.setClientIp(clientIp);
		
		SyncTask task;
		try {
			task = this.taskServer.createTask(taskType, content, source);
			
			if (task != null) {
				if (task.isSync()) {
					logger.debug("wait task complete : {}", task.getTaskID());
					task.waitForComplete(); 
				}
				
				// 将task转化为response结构，并序列化为xml代码；
				SyncTaskResponse response = new SyncTaskResponse();
				response.setTaskID(task.getTaskID());
				response.setResult(task.getResuleMap());
				
				String responseStr = xb.convertResponseToXmlStr(response);
				logger.debug("return task response {}", responseStr);
				
				return responseStr;
			}
		} catch (TaskException | InterruptedException e) {
			logger.error("task process failed.", e);
		}
	
		// TODO 这里应该返回一个任务执行错误的响应，为配合测试，临时改为一个普通的响应好了；
		// 另外，响应码也需要改为合适的值；比如任务类型不存在等等；
		SyncTaskResponse response = new SyncTaskResponse();
		response.setTaskID("1");
		return xb.convertResponseToXmlStr(response);
	}

}

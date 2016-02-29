package com.cmread.cmu.css.http;

import java.util.Date;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

public class SyncTaskMessageXmlBuilderTest {

	/**
	 * 因为造出xml文本较为费劲，因此在一个测试用例中完成xml序列化和反序列化测试；
	 */
	@SuppressWarnings("serial")
	@Test
	public void testSyncTaskRequestBuilder() {
		Date current = new Date();
		SyncTaskRequest request = new SyncTaskRequest();
		request.setTaskType("test_user.insert");
		request.setContent(new HashMap<String, String>() {
			{
				put("username", "panglaoying");
			}
		});
		request.setCreateTime(current);
		
		SyncTaskMessageXmlBuilder msgBuilder = new SyncTaskMessageXmlBuilder();
		
		String xmlStr = msgBuilder.convertRequestToXmlStr(request);
		System.out.println(xmlStr);
		
		SyncTaskRequest deMsg = msgBuilder.convertXmlStrToRequest(xmlStr);
		
		Assert.assertEquals(request.getTaskType(), deMsg.getTaskType());
		Assert.assertEquals(request.getCreateTime(), deMsg.getCreateTime());
		Assert.assertEquals(request.getContent(), deMsg.getContent());
	}
	
	/**
	 * 因为造出xml文本较为费劲，因此在一个测试用例中完成xml序列化和反序列化测试；
	 */
	@Test
	public void testSyncTaskResponseBuilder() {
		SyncTaskResponse response = new SyncTaskResponse();
		response.setTaskID("123456");
		
		SyncTaskMessageXmlBuilder msgBuilder = new SyncTaskMessageXmlBuilder();

		String xmlStr = msgBuilder.convertResponseToXmlStr(response);
		System.out.println(xmlStr);
		
		SyncTaskResponse deMsg = msgBuilder.convertXmlStrToResponse(xmlStr);
		
		Assert.assertEquals(response.getTaskID(), deMsg.getTaskID());
	}
	
}

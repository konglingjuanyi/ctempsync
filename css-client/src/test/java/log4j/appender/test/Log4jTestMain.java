package log4j.appender.test;

import java.io.IOException;

import org.apache.log4j.Logger;

public class Log4jTestMain {

	public static void main(String[] args) throws IOException {        
		Logger logger = Logger.getLogger("test");
		logger.debug("开始");
		logger.debug("结束");
		logger.debug("info");
	}

}

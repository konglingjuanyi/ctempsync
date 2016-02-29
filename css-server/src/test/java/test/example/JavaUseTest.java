package test.example;

import java.util.Date;

import org.junit.Test;

public class JavaUseTest {

	@Test
	public void test() {
		long currentTime = System.currentTimeMillis();
		System.out.println("System.currentTimeMillis " + currentTime);
		
		System.out.println("new Date().getTime() " + new Date().getTime());
		System.out.println("new Date().getTime() " + String.valueOf(new Date().getTime()));
		
	}

}

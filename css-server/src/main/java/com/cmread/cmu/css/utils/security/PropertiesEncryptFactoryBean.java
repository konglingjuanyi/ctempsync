package com.cmread.cmu.css.utils.security;

import java.util.Properties;

import org.springframework.beans.factory.FactoryBean;

@SuppressWarnings("rawtypes")
public class PropertiesEncryptFactoryBean implements FactoryBean {

	private Properties properties;

	public Object getObject() throws Exception {
		return getProperties();
	}

	public Class getObjectType() {
		return java.util.Properties.class;
	}

	public boolean isSingleton() {
		return true;
	}

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties inProperties) {
		this.properties = inProperties;
		String originalPassword = properties.getProperty("password");
		
		if (originalPassword != null) {
			String newPassword = new AESDSUserAndPasswordImpl().getDecrypt(originalPassword);
			properties.put("password", newPassword);
		}
	}
}

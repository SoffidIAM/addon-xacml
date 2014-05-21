package com.soffid.iam.addons.xacml.utils;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import es.caib.seycon.ng.comu.lang.MessageFactory;

public class Messages {
	private static final String BUNDLE_NAME = "com.soffid.iam.addons.xacml.utils.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(String key) {
		try {
            return es.caib.seycon.ng.comu.lang.MessageFactory.getString(BUNDLE_NAME, key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}

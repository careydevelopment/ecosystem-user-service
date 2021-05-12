package com.careydevelopment.ecosystem.user.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class EmailIntegration {

	private EmailIntegrationType integrationType;

	public EmailIntegrationType getIntegrationType() {
		return integrationType;
	}

	public void setIntegrationType(EmailIntegrationType integrationType) {
		this.integrationType = integrationType;
	}
	
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}

package org.jamocha.service;

import java.util.ArrayList;
import java.util.List;

public class ServiceConfiguration {
	
	private List<RuleApplicationBean> applications = new ArrayList<RuleApplicationBean>();
	private String serviceName;
	
	public ServiceConfiguration() {
		super();
	}

	public List<RuleApplicationBean> getApplications() {
		return applications;
	}

	public void setApplications(List<RuleApplicationBean> applications) {
		this.applications = applications;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
}

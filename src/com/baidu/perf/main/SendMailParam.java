package com.baidu.perf.main;

public class SendMailParam {
	private String service_list;
	private String sendAddress;
	private String ccAddress;
	public String getSendAddress() {
		return sendAddress;
	}

	public void setSendAddress(String sendAddress) {
		this.sendAddress = sendAddress;
	}

	public String getCcAddress() {
		return ccAddress;
	}

	public void setCcAddress(String ccAddress) {
		this.ccAddress = ccAddress;
	}

	public String getService_list() {
		return service_list;
	}

	public void setService_list(String service_list) {
		this.service_list = service_list;
	}
}

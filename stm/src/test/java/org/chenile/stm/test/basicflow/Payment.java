package org.chenile.stm.test.basicflow;

public class Payment {
	private String payee;
	
	public Payment(String payee){
		this.payee = payee;
	}
	
	private String confirmationId;

	

	public String getPayee() {
		return payee;
	}

	public void setPayee(String payee) {
		this.payee = payee;
	}

	public String getConfirmationId() {
		return confirmationId;
	}

	public void setConfirmationId(String confirmationId) {
		this.confirmationId = confirmationId;
	}
}

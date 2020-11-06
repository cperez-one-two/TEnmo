package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {

	private Integer transferId;
	private Integer transferTypeId;
	private Integer transferStatusId;
	private Integer accountFrom;
	private Integer accountTo;
	private BigDecimal amount;
	
	public Transfer() {

	}


	public Transfer(Integer transferId,
				Integer transferTypeId, 
				Integer transferStatusId,
				Integer accountFrom,
				Integer accountTo,
				BigDecimal amount) {
		this.transferId = transferId;
		this.transferTypeId = transferTypeId;
		this.transferStatusId = transferStatusId;
		this.accountFrom = accountFrom;
		this.accountTo = accountTo;
		this.amount = amount;
	}


	public Integer getTransferId() {
		return transferId;
	}


	public void setTransferId(Integer transferId) {
		this.transferId = transferId;
	}


	public Integer getTransferTypeId() {
		return transferTypeId;
	}


	public void setTransferTypeId(Integer transferTypeId) {
		this.transferTypeId = transferTypeId;
	}


	public Integer getTransferStatusId() {
		return transferStatusId;
	}


	public void setTransferStatusId(Integer transferStatusId) {
		this.transferStatusId = transferStatusId;
	}


	public Integer getAccountFrom() {
		return accountFrom;
	}


	public void setAccountFrom(Integer accountFrom) {
		this.accountFrom = accountFrom;
	}


	public Integer getAccountTo() {
		return accountTo;
	}


	public void setAccountTo(Integer accountTo) {
		this.accountTo = accountTo;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	@Override
	public String toString() {
		return String.format("%d    $ %.02f",
						transferId, amount);
	}

}

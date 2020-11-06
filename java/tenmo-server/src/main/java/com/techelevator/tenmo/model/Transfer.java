package com.techelevator.tenmo.model;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Transfer {

	private Integer transferId;
	private Integer transferTypeId;
	private Integer transferStatusId;
	private Integer accountFrom;
	private Integer accountTo;
	private BigDecimal amount;

   public Transfer() { }



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



public Transfer(Integer transferId, Integer transferTypeId, Integer transferStatusId, Integer accountFrom,
		Integer accountTo, BigDecimal amount) {
	super();
	this.transferId = transferId;
	this.transferTypeId = transferTypeId;
	this.transferStatusId = transferStatusId;
	this.accountFrom = accountFrom;
	this.accountTo = accountTo;
	this.amount = amount;
}



@Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Transfer transfer = (Transfer) o;
      return Objects.equals(transferId, transfer.transferId) &&
              Objects.equals(transferStatusId, transfer.transferId) &&
              Objects.equals(transferTypeId, transfer.transferTypeId) &&
              Objects.equals(accountFrom, transfer.accountFrom) &&
              Objects.equals(accountTo, transfer.accountTo) &&
              Objects.equals(amount, transfer.amount);
   }

   @Override
   public String toString() {
      return "Transfer{" +
              "transferId=" + transferId +
              ", transferStatusId='" + transferStatusId + '\'' +
              ", accountFrom=" + accountFrom +
              ", accountTo=" + accountTo +
              ", amount=" + amount +
              '}';
   }
}
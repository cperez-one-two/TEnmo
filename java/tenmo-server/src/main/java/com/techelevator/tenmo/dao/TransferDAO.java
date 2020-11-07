package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	Integer getTransferTypeId(String type);

	Integer getTransferStatusId(String status);
	
	Transfer[] getTransferHistory(int id);

	public String getAccountHolderName(int id);
	
	public Transfer getTransferDetailsById(int id);

	String getTransferTypeName(int id);

	String getTransferStatusName(int id);

	boolean sendBucks(int fromId, int toId, Transfer transfer);

	boolean requestBucks(int fromId, int toId, Transfer transfer);
}

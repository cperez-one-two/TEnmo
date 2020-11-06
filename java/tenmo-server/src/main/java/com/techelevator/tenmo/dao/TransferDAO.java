package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	Integer getTransferTypeId(String type);

	Integer getTransferStatusId(String status);
	
	boolean sendBucks(Transfer transfer);
	
	Transfer[] getTransferHistory(int id);

	public String getAccountHolderName(int id);
}

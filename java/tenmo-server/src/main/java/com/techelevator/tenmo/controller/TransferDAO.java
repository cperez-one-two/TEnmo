package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.model.Transfer;

public interface TransferDAO {

	Integer getTransferTypeId(String type);

	Integer getTransferStatusId(String status);
	
	boolean sendBucks(Transfer transfer);

}

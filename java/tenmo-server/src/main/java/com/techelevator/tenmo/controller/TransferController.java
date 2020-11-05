package com.techelevator.tenmo.controller;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.model.Transfer;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

	private TransferDAO transferDAO;

	public TransferController(TransferDAO transferDAO) {
		// TODO Auto-generated constructor stub
		this.transferDAO = transferDAO;
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/send", method = RequestMethod.GET)
	public Integer getTransferTypeId() {
		return transferDAO.getTransferTypeId("Send");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/approved", method = RequestMethod.GET)
	public Integer getTransferStatusId() {
		return transferDAO.getTransferStatusId("Approved");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "/send", method = RequestMethod.POST)
	public boolean sendBucks(@Valid @RequestBody Transfer transfer) {
		return transferDAO.sendBucks(transfer);
	}

    	
    
}

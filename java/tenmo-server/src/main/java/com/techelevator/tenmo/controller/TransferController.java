package com.techelevator.tenmo.controller;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

	private TransferDAO transferDAO;

	// TODO :: Fix URLs to all be concise
	public TransferController(TransferDAO transferDAO) {
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

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/transfers/user/{id}", method = RequestMethod.GET)
	public Transfer[] getTransferHistory(@PathVariable int id) {
		return transferDAO.getTransferHistory(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/transfers/accounts/{id}", method = RequestMethod.GET)
	public String getAccountHolderName(@PathVariable int id) {
		return transferDAO.getAccountHolderName(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/transfers/{id}", method = RequestMethod.GET)
	public Transfer getTransferDetailsById(@PathVariable int id) {
		return transferDAO.getTransferDetailsById(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/transfers/type/{id}", method = RequestMethod.GET)
	public String getTransferTypeName(@PathVariable int id) {
		return transferDAO.getTransferTypeName(id);
	}
    
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/transfers/status/{id}", method = RequestMethod.GET)
	public String getTransferStatusName(@PathVariable int id) {
		return transferDAO.getTransferStatusName(id);
	}
    
}

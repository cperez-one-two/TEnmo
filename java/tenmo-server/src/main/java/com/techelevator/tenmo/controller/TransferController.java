package com.techelevator.tenmo.controller;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Transfer;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transfers")
public class TransferController {

	private TransferDAO transferDAO;

	public TransferController(TransferDAO transferDAO) {
		this.transferDAO = transferDAO;
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/sendId", method = RequestMethod.GET)
	public Integer getSendId() {
		return transferDAO.getTransferTypeId("Send");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/requestId", method = RequestMethod.GET)
	public Integer getRequestId() {
		return transferDAO.getTransferTypeId("Request");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/approvedId", method = RequestMethod.GET)
	public Integer getApprovedId() {
		return transferDAO.getTransferStatusId("Approved");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/pendingId", method = RequestMethod.GET)
	public Integer getPendingId() {
		return transferDAO.getTransferStatusId("Pending");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/rejectedId", method = RequestMethod.GET)
	public Integer getRejectedId() {
		return transferDAO.getTransferStatusId("Rejected");
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "/send/{fromId}/{toId}", method = RequestMethod.POST)
	public boolean sendBucks(@PathVariable int fromId, @PathVariable int toId, @Valid @RequestBody Transfer transfer) {
		return transferDAO.sendBucks(fromId, toId, transfer);
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
	@RequestMapping(path = "/request/{fromId}/{toId}", method = RequestMethod.POST)
	public boolean requestBucks(@PathVariable int fromId, @PathVariable int toId, @Valid @RequestBody Transfer transfer) {
		return transferDAO.sendBucks(fromId, toId, transfer);
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
	public Transfer[] getTransferHistory(@RequestParam int transfer_status_id, @PathVariable int id) {
		if (transferDAO.getTransferStatusId("Rejected") == transfer_status_id) {
			return transferDAO.getTransferHistory("Rejected", id);
		}
		if (transferDAO.getTransferStatusId("Pending") == transfer_status_id) {
			return transferDAO.getTransferHistory("Pending", id);
		}
		return transferDAO.getTransferHistory("Approved", id);
	}

	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user/pending/{id}", method = RequestMethod.GET)
	public Transfer[] getPendingTransfers(@PathVariable int id) {
		return transferDAO.getPendingTransfers(id);
	}
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/accounts/{id}", method = RequestMethod.GET)
	public String getAccountHolderName(@PathVariable int id) {
		return transferDAO.getAccountHolderName(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/accounts", method = RequestMethod.GET)
	public Integer getAccountHolderIdByName(@RequestParam String username) {
		return transferDAO.getAccountHolderIdByName(username);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/{id}", method = RequestMethod.GET)
	public Transfer getTransferDetailsById(@PathVariable int id) {
		return transferDAO.getTransferDetailsById(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/type/{id}", method = RequestMethod.GET)
	public String getTransferTypeName(@PathVariable int id) {
		return transferDAO.getTransferTypeName(id);
	}
    
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/status/{id}", method = RequestMethod.GET)
	public String getTransferStatusName(@PathVariable int id) {
		return transferDAO.getTransferStatusName(id);
	}
	
	@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/{id}/update", method = RequestMethod.PUT)
	public boolean transferUpdate(@PathVariable int id, @RequestBody Transfer transfer) {
		return transferDAO.transferUpdate(id, transfer);
	}
    
}

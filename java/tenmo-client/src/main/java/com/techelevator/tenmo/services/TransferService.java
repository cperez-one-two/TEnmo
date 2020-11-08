package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Transfer;



public class TransferService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

	public TransferService(String url) {
        this.BASE_URL = url;
	}
	
	public Integer getTransferTypeId(String type) throws TransferServiceException {
		Integer transferType = null;
		if (type.equalsIgnoreCase("Send")) {
			try{
				transferType = 
					restTemplate
						.exchange(BASE_URL + 
									"/transfers/sendId",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			} catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}
		} else if (type.equalsIgnoreCase("Request")) {
			try{
				transferType = 
					restTemplate
						.exchange(BASE_URL + 
									"/transfers/requestId",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			} catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}	
		}

		return transferType;
	}
	
	
	
	public Integer getTransferStatusId(String status) throws TransferServiceException {
		Integer transferStatus = null;
		if (status.equalsIgnoreCase("Approved")) {
			try {
				transferStatus = 
					restTemplate
						.exchange(BASE_URL + 
									"transfers/approvedId",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			} catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}
		} else if (status.equalsIgnoreCase("Pending")) {
			try{
				transferStatus = 
					restTemplate
						.exchange(BASE_URL + 
									"transfers/pendingId",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			}catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}
		}
		else if (status.equalsIgnoreCase("Rejected")) {
			try{
				transferStatus = 
					restTemplate
						.exchange(BASE_URL + 
									"transfers/rejectedId",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			}catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}
		}
		return transferStatus;
	}
	
	public String[] getTransferHistoryById(int statusId, int id) throws TransferServiceException {
		Transfer[] transferHistory = null;
		try {
			transferHistory = restTemplate
								.exchange(BASE_URL +
											"/transfers/user/" + id +
											"?transfer_status_id=" + statusId,
											HttpMethod.GET,
											makeAuthEntity(),
											Transfer[].class)
								.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return makeTransferStringArray(transferHistory);
	}

//	public String[] getPendingTransactionsById(int id) throws TransferServiceException {
//		Transfer[] transferHistory = null;
//		try {
//			transferHistory = restTemplate
//								.exchange(BASE_URL +
//											"/transfers/user/pending/" + id,
//											HttpMethod.GET,
//											makeAuthEntity(),
//											Transfer[].class)
//								.getBody();
//		} catch (RestClientResponseException ex) {
//			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
//		}
//		return makeTransferStringArray(transferHistory);
//	}
//	
	public String getAccountHolderName(int id) throws TransferServiceException {
		String name;
		try {
			name = restTemplate
						.exchange(BASE_URL +
								"/transfers/accounts/" + id,
								HttpMethod.GET,
								makeAuthEntity(),
								String.class)
						.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return name;
	}
	
	public Transfer getTransferDetailsById(int id) throws TransferServiceException {
		Transfer details;
		try {
			details = restTemplate.exchange(BASE_URL +
											"/transfers/" + id,
											HttpMethod.GET,
											makeAuthEntity(),
											Transfer.class)
									.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return details;
	}
	
	public String getTransferTypeName(int id) throws TransferServiceException {
		String name;
		try {
			name = restTemplate
						.exchange(BASE_URL +
								"/transfers/type/" + id,
								HttpMethod.GET,
								makeAuthEntity(),
								String.class)
						.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return name;
	}
	
	public String getTransferStatusName(int id) throws TransferServiceException {
		String name;
		try {
			name = restTemplate
						.exchange(BASE_URL +
								"/transfers/status/" + id,
								HttpMethod.GET,
								makeAuthEntity(),
								String.class)
						.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return name;
	}
	
	private String[] makeTransferStringArray(Transfer[] trs) {
		List<String> tempList = new ArrayList<String>();

		for (Transfer t : trs) {
			String accountFromHolder;
			String accountToHolder;
			try {
				accountFromHolder = getAccountHolderName(t.getAccountFrom());
				accountToHolder = getAccountHolderName(t.getAccountTo());
			} catch (TransferServiceException e) {
				accountFromHolder = "error";
				accountToHolder = "error";
			}
			String str = String.format("%-5d %-12s %-15s $ %.02f",
					t.getTransferId(),
					accountFromHolder,
					accountToHolder,
					t.getAmount());
			tempList.add(str);
		}
		String[] strArray = new String[tempList.size()];
		
		return tempList.toArray(strArray);

	}	

	public boolean sendBucks(int fromId, int toId, Transfer transfer) throws TransferServiceException {
		boolean hasSent = false;
		try {
		hasSent = restTemplate
					.exchange(BASE_URL +
							"/transfers/send/" + fromId + "/" + toId,
							HttpMethod.POST,
							makeTransferEntity(transfer),
							Boolean.class)
					.getBody();
		} catch(RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return hasSent;
	}

	public boolean requestBucks(int fromId, int toId, Transfer transfer) throws TransferServiceException {
		boolean hasSent = false;
		try {
		hasSent = restTemplate
					.exchange(BASE_URL +
							"/transfers/request/" + fromId + "/" + toId,
							HttpMethod.POST,
							makeTransferEntity(transfer),
							Boolean.class)
					.getBody();
		} catch(RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return hasSent;
	}
	@SuppressWarnings("rawtypes")
	private HttpEntity makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(UserService.AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }
	
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

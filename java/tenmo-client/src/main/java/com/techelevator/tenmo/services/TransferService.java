package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
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
									"/send",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			} catch (RestClientResponseException ex) {
				throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
			}
		} else if (type.equalsIgnoreCase("Request")) {
			
		}

		return transferType;
	}
	
	
	
	public Integer getTransferStatusId(String status) throws TransferServiceException {
		Integer transferStatus = null;
		if (status.equalsIgnoreCase("Approved")) {
				try{
					transferStatus = 
						restTemplate
							.exchange(BASE_URL + 
										"/approved",
										HttpMethod.GET,
										makeAuthEntity(),
										Integer.class)
							.getBody();
				}catch (RestClientResponseException ex) {
					throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
				}
			} else if (status.equalsIgnoreCase("Pending")) {
				
			}
		return transferStatus;
	}
	
	public String[] getTransferHistoryById(int id) throws TransferServiceException {
		Transfer[] transferHistory = null;
		try {
			transferHistory = restTemplate
								.exchange(BASE_URL +
											"/transfers/" + id,
											HttpMethod.GET,
											makeAuthEntity(),
											Transfer[].class)
								.getBody();
		} catch (RestClientResponseException ex) {
			throw new TransferServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return makeTransferStringArray(transferHistory);
	}
	
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
	
	private String[] makeTransferStringArray(Transfer[] trs) {
		List<String> tempList = new ArrayList<String>();

		for (Transfer t : trs) {
			String accountHolder;
			try {
				accountHolder = getAccountHolderName(t.getAccountFrom());
			} catch (TransferServiceException e) {
				accountHolder = "error";
			}
			String str = String.format("%d   From/To: %s        $ %.02f",
					t.getTransferId(),
					accountHolder,
					t.getAmount());
			tempList.add(str);
		}
		String[] strArray = new String[tempList.size()];
		
		return tempList.toArray(strArray);

	}
	
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

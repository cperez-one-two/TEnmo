package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;



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
	
	//public getTransferHistoryById() {
		
	//}
	
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class TransferService {

    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

	public TransferService(String url) {
        this.BASE_URL = url;
	}
	
	public Integer getTransferTypeId(String type) {
		Integer transferType = null;
		if (type.equalsIgnoreCase("Send")) {
			transferType = 
					restTemplate
						.exchange(BASE_URL + 
									"/send",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
		} else if (type.equalsIgnoreCase("Request")) {
			// TODO
		}

		return transferType;
	}
	
	
	// TODO :: get transfer
	public Integer getTransferStatusId(String status) {
		Integer transferStatus = null;
		if (status.equalsIgnoreCase("Approved")) {
				transferStatus = 
					restTemplate
						.exchange(BASE_URL + 
									"/approved",
									HttpMethod.GET,
									makeAuthEntity(),
									Integer.class)
						.getBody();
			} else if (status.equalsIgnoreCase("Pending")) {
				// TODO
			}
		return transferStatus;
	}
	
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(UserService.AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

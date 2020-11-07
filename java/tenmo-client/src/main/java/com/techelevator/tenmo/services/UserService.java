package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class UserService {

    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

	public UserService(String url) {
        this.BASE_URL = url;
	}

	public BigDecimal getBalance(int id) throws UserServiceException {
		
			BigDecimal balance; 
			try{
				balance = restTemplate
					.exchange(BASE_URL +
								"/user/" + id +
								"/balance",
								HttpMethod.GET,
								makeAuthEntity(),
								BigDecimal.class)
					.getBody();
		} catch (RestClientResponseException ex) {
			throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return balance;
	}

	public User[] getUsers() throws UserServiceException {
		User[] users;
		try {
			
		
		users = restTemplate
					.exchange(BASE_URL +
								"/user",
								HttpMethod.GET,
								makeAuthEntity(),
								User[].class)
					.getBody();
		}catch (RestClientResponseException ex) {
			throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
		return users;
	}

	public int getAccountId(int id) throws UserServiceException {
		int accountId;
		try {

		accountId = restTemplate
				.exchange(BASE_URL +
						"/user/" + id +
						"/account",
						HttpMethod.GET,
						makeAuthEntity(),
						Integer.class)
				.getBody();
		} catch(RestClientResponseException ex) {
			throw new UserServiceException(ex.getRawStatusCode() + " : " + ex.getResponseBodyAsString());
		}
						
		return (int) accountId;
	}
	

	
	//public boolean requestBucks() {
		
	//}

	

	// TODO :: Make a user entity
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }


}

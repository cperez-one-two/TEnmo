package com.techelevator.tenmo.services;

import java.math.BigDecimal;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
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

	// TODO :: Add try-catch for access exceptions
	public BigDecimal getBalance(int id) {
		BigDecimal balance = 
				restTemplate
					.exchange(BASE_URL +
								"/user/" + id +
								"/balance",
								HttpMethod.GET,
								makeAuthEntity(),
								BigDecimal.class)
					.getBody();
		return balance;
	}

	// TODO :: Get list of users
	public User[] getUsers() {
		User[] users = null;
		users = restTemplate
					.exchange(BASE_URL +
								"/user",
								HttpMethod.GET,
								makeAuthEntity(),
								User[].class)
					.getBody();
		return users;
	}

	public int getAccountId(int id) {
		int accountId = 0;
		accountId = restTemplate
				.exchange(BASE_URL +
						"/user/" + id +
						"/account",
						HttpMethod.GET,
						makeAuthEntity(),
						Integer.class)
				.getBody();
						
		return (int) accountId;
	}
	
	public boolean sendBucks(Transfer transfer) {
		boolean hasSent = false;
		hasSent = restTemplate
					.exchange(BASE_URL +
							"/send",
							HttpMethod.POST,
							makeTransferEntity(transfer),
							Boolean.class)
					.getBody();
		return hasSent;
	}

	

	// TODO :: Make a user entity
	@SuppressWarnings("rawtypes")
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }

	@SuppressWarnings("rawtypes")
	private HttpEntity makeTransferEntity(Transfer transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);
        return entity;
    }
}

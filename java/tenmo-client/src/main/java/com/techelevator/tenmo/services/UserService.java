package com.techelevator.tenmo.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class UserService {

    public static String AUTH_TOKEN = "";
    private String BASE_URL;
    private RestTemplate restTemplate = new RestTemplate();

	public UserService(String url) {
        this.BASE_URL = url;
	}

	public double viewBalance(int id) {
		double balance = 0;
		balance = restTemplate
				.exchange(BASE_URL +
							"/user/" + id +
							"/balance",
							HttpMethod.GET,
							makeAuthEntity(),
							Double.class)
				.getBody();
		return Double.valueOf(balance);
	}
	
	private HttpEntity makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(AUTH_TOKEN);
        HttpEntity entity = new HttpEntity<>(headers);
        return entity;
    }
}

package com.techelevator.tenmo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

	private UserDAO userDao;

	public UserController(UserDAO userDao) {
		this.userDao = userDao;
	}
	
    @PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
	public double viewBalance(@PathVariable int id) {
		return userDao.viewBalance(id);
	}
    
    //@PreAuthorize("hasRole('ROLE_ROLE_USER')")
	//@RequestMapping(path = "/user/send", method = RequestMethod.POST)
    //public void sendBucks(int fromId, int toId, int amount)
    

}

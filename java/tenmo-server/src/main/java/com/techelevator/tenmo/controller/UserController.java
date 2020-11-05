package com.techelevator.tenmo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.User;

@RestController
@PreAuthorize("isAuthenticated()")
public class UserController {

	private UserDAO userDao;

	public UserController(UserDAO userDao) {
		this.userDao = userDao;
	}

    @PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user", method = RequestMethod.GET)
	public User[] getUsers() {
    	List<User> userList = userDao.findAll();
    	User[] users = new User[userList.size()];
    	users = userList.toArray(users);
		return users;
	}
	
    @PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user/{id}/balance", method = RequestMethod.GET)
	public double getBalance(@PathVariable int id) {
		return userDao.getBalance(id);
	}
    
    @PreAuthorize("hasRole('ROLE_ROLE_USER')")
	@RequestMapping(path = "/user/{id}/account", method = RequestMethod.GET)
	public int getAccountId(@PathVariable int id) {
		return userDao.getAccountId(id);
	}
    
    

}

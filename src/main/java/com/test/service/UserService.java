package com.test.service;

import com.test.model.User;

public interface UserService 
{
	public User findUserByEmail(String email);
	public void saveUser(User user);
}
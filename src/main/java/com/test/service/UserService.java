package com.test.service;

import java.util.List;
import com.test.model.User;

public interface UserService 
{
	public User findUserByEmail(String email);
	public void saveUser(User user);
	public List<User> findAll();
	public void deleteUser(int userId);
//	public void editUser(int userId);
}
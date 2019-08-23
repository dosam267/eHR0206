package com.ehr;

import java.util.List;

public interface UserDao {
	public int update(User user);
	public List<User> retrieve(Search vo);
	public User get(String id);
	public int add(User user);
	public int deleteUser(User user);
	public int count(String uId);
	public List<User> getAll();
	
	
}

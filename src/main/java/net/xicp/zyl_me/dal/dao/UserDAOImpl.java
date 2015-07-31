package net.xicp.zyl_me.dal.dao;

import net.xicp.zyl_me.dal.entity.User;

public class UserDAOImpl implements UserDAO {

	@Override
	public void add(User u) {
		System.out.println(u + " added.");
	}
}

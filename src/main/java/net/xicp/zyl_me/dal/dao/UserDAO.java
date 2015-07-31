package net.xicp.zyl_me.dal.dao;

import java.util.List;

import net.xicp.zyl_me.dal.entity.User;

public interface UserDAO {
	public abstract void add(User u);
	public abstract List<User> list();
}

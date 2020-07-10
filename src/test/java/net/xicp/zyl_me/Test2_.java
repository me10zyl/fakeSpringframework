package net.xicp.zyl_me;

import net.xicp.zyl_me.dal.dao.RoleDAO;
import net.xicp.zyl_me.dal.dao.RoleDAOImpl;
import net.xicp.zyl_me.dal.dao.UserDAO;
import net.xicp.zyl_me.dal.entity.User;
import net.xicp.zyl_me.springframework.core.context.ClassPathXmlApplicationContext;
import org.junit.Test;

import java.io.IOException;

public class Test2_ {


	@Test
	public void test1() throws IOException {
		ClassPathXmlApplicationContext context= new ClassPathXmlApplicationContext("src/test/java/applicationContext.xml");
		UserDAO userDAO = (UserDAO) context.getBean("userDAO");
		User user = new User();
		user.setAge(23);
		user.setName("gs");
		userDAO.add(user);
		userDAO.list();
		RoleDAO roleDAO = (RoleDAO) context.getBean(RoleDAOImpl.class);
		roleDAO.list();
	}
}

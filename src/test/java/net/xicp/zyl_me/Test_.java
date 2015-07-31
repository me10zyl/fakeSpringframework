package net.xicp.zyl_me;

import java.io.IOException;

import net.xicp.zyl_me.dal.dao.UserDAO;
import net.xicp.zyl_me.dal.entity.User;
import net.xicp.zyl_me.springframework.context.ClassPathXmlApplicationContext;

import org.junit.Test;

public class Test_ {


	@Test
	public void test1() throws IOException {
		ClassPathXmlApplicationContext context= new ClassPathXmlApplicationContext("src/main/java/applicationContext.xml");
		UserDAO userDAO = (UserDAO) context.getBean("userDAO");
		User user = new User();
		user.setAge(23);
		user.setName("gs");
		userDAO.add(user);
	}
}

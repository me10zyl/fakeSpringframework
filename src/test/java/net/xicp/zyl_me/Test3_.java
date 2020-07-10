package net.xicp.zyl_me;

import net.xicp.zyl_me.dal.dao.RoleDAO;
import net.xicp.zyl_me.dal.dao.RoleDAOImpl;
import net.xicp.zyl_me.dal.dao.UserDAO;
import net.xicp.zyl_me.dal.entity.User;
import net.xicp.zyl_me.springframework.core.bean.annotation.Autowired;
import net.xicp.zyl_me.springframework.core.bean.annotation.Component;
import net.xicp.zyl_me.springframework.core.context.ClassPathXmlApplicationContext;
import org.junit.Test;

import java.io.IOException;

@Component
public class Test3_ {

	@Autowired
	private UserDAO userDAO;


	@Test
	public void test1() throws IOException {
		ClassPathXmlApplicationContext context= new ClassPathXmlApplicationContext("src/test/java/applicationContext.xml");
		User user = new User();
		user.setAge(23);
		user.setName("gs");
		Test3_ test3 = (Test3_) context.getBean(Test3_.class);
		UserDAO userDAO = test3.userDAO;
		userDAO.add(user);
		userDAO.list();
	}
}

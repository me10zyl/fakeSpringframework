package net.xicp.zyl_me.dal.tx;

public class TransactionManager {

	public void before()
	{
		System.out.println("transaction begin");
	}

	public void after()
	{
		System.out.println("transaction commit");
	}
}

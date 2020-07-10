package net.xicp.zyl_me.dal.dao;

import net.xicp.zyl_me.dal.entity.Role;
import net.xicp.zyl_me.springframework.core.bean.annotation.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RoleDAOImpl implements RoleDAO {
    @Override
    public List<Role> list() {
        System.out.println("list role");
        return new ArrayList<>();
    }
}

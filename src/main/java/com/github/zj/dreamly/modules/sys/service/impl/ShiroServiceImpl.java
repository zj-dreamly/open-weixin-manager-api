package com.github.zj.dreamly.modules.sys.service.impl;

import com.github.zj.dreamly.common.utils.Constant;
import com.github.zj.dreamly.modules.sys.dao.SysMenuDao;
import com.github.zj.dreamly.modules.sys.dao.SysUserDao;
import com.github.zj.dreamly.modules.sys.dao.SysUserTokenDao;
import com.github.zj.dreamly.modules.sys.entity.SysMenuEntity;
import com.github.zj.dreamly.modules.sys.entity.SysUserEntity;
import com.github.zj.dreamly.modules.sys.entity.SysUserTokenEntity;
import com.github.zj.dreamly.modules.sys.service.ShiroService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ShiroServiceImpl implements ShiroService {
    @Resource
    private SysMenuDao sysMenuDao;
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private SysUserTokenDao sysUserTokenDao;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if (userId == Constant.SUPER_ADMIN) {
            List<SysMenuEntity> menuList = sysMenuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for (SysMenuEntity menu : menuList) {
                permsList.add(menu.getPerms());
            }
        } else {
            permsList = sysUserDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for (String perms : permsList) {
            if (StringUtils.isBlank(perms)) {
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return sysUserTokenDao.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {
        return sysUserDao.selectById(userId);
    }
}

package com.jd.platform.hotkey.dashboard.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.Page;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.mapper.UserMapper;
import com.jd.platform.hotkey.dashboard.model.User;
import com.jd.platform.hotkey.dashboard.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: UserServiceImpl
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/16 20:37
 */
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User findByNameAndPwd(User user) {
        return userMapper.findByNameAndPwd(user);
    }

    @Override
    public int insertUser(User user) {
        user.setPwd(DigestUtils.md5DigestAsHex(user.getPwd().getBytes()));
        return userMapper.insertSelective(user);
    }

    @Override
    public int deleteByPrimaryKey(int id) {
        return userMapper.deleteByPrimaryKey(id);
    }

    @Override
    public User selectByPrimaryKey(int id) {
        return userMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateUser(User user) {
        if(!StringUtils.isEmpty(user.getPwd())){
            user.setPwd(DigestUtils.md5DigestAsHex(user.getPwd().getBytes()));
        };
        return userMapper.updateByPk(user);
    }

    @Override
    public PageInfo<User> pageUser(PageParam param, SearchDto dto) {
        PageHelper.startPage(param.getPageNum(),param.getPageSize());
        List<User> users = userMapper.listUser(dto);
        return  new PageInfo<>(users);
    }


}

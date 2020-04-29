package com.jd.platform.hotkey.dashboard.service;

import com.github.pagehelper.PageInfo;
import com.jd.platform.hotkey.dashboard.common.domain.PageParam;
import com.jd.platform.hotkey.dashboard.common.domain.SearchDto;
import com.jd.platform.hotkey.dashboard.model.User;

import java.util.List;

/**
 * @ProjectName: hotkey
 * @ClassName: UserService
 * @Description: TODO(一句话描述该类的功能)
 * @Author: liyunfeng31
 * @Date: 2020/4/16 20:37
 */
public interface UserService {

    PageInfo<User> pageUser(PageParam page, SearchDto dto);

    User findByNameAndPwd(User user);

    int insertUser(User user);

    int deleteByPrimaryKey(int id);

    User selectByPrimaryKey(int id);

    int updateUser(User user);

    List<String> listApp();
}

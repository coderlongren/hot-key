package com.jd.platform.hotkey.worker.mapper;

import com.jd.platform.hotkey.worker.model.AppInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;

/**
 * AppInfoMapper
 *
 * @author longhuashen
 * @since 2020-05-18
 */
@Mapper
public interface AppInfoMapper {

    @Options(useGeneratedKeys = true)
    int insertSelective(AppInfo appInfo);

    AppInfo findByAppName(String appName);
}
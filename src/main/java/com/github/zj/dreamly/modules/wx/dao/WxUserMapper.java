package com.github.zj.dreamly.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.zj.dreamly.modules.wx.entity.WxUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WxUserMapper extends BaseMapper<WxUser> {
}

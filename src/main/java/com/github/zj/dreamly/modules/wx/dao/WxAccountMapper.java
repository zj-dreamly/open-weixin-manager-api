package com.github.zj.dreamly.modules.wx.dao;

import com.github.zj.dreamly.modules.wx.entity.WxAccount;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号账号
 * 
 * @author niefy
 * @date 2020-06-17 13:56:51
 */
@Mapper
public interface WxAccountMapper extends BaseMapper<WxAccount> {
	
}

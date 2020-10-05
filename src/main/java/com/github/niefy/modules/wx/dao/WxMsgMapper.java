package com.github.niefy.modules.wx.dao;

import com.github.niefy.modules.wx.entity.WxMsg;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信消息
 * 
 * @author niefy
 * @date 2020-05-14 17:28:34
 */
@Mapper
public interface WxMsgMapper extends BaseMapper<WxMsg> {
	
}

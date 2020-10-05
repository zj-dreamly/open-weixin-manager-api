package com.github.niefy.modules.wx.dao;

import com.github.niefy.modules.wx.entity.WxQrCode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公众号带参二维码
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2020-01-02 11:11:55
 */
@Mapper
public interface WxQrCodeMapper extends BaseMapper<WxQrCode> {

}

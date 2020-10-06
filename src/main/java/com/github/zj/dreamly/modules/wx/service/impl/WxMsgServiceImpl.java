package com.github.zj.dreamly.modules.wx.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.Query;
import com.github.zj.dreamly.modules.wx.dao.WxMsgMapper;
import com.github.zj.dreamly.modules.wx.entity.WxMsg;
import com.github.zj.dreamly.modules.wx.service.WxMsgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Map;

@Service("wxMsgService")
public class WxMsgServiceImpl extends ServiceImpl<WxMsgMapper, WxMsg> implements WxMsgService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String msgTypes = (String) params.get("msgTypes");
        if (StrUtil.isBlank(msgTypes)) {
            msgTypes = StrUtil.EMPTY;
        }
        String startTime = (String) params.get("startTime");
        String openid = (String) params.get("openid");
        String appid = (String) params.get("appid");
        IPage<WxMsg> page = this.page(
                new Query<WxMsg>().getPage(params),
                new QueryWrapper<WxMsg>()
                        .eq(!org.springframework.util.StringUtils.isEmpty(appid), "appid", appid)
                        .in(StringUtils.isNotEmpty(msgTypes), "msg_type", Arrays.asList(msgTypes.split(",")))
                        .eq(StringUtils.isNotEmpty(openid), "openid", openid)
                        .ge(StringUtils.isNotEmpty(startTime), "create_time", startTime)
        );

        return new PageUtils(page);
    }

    /**
     * 记录msg，异步入库
     */
    @Override
    @Async
    public void addWxMsg(WxMsg msg) {
        this.baseMapper.insert(msg);
    }

}
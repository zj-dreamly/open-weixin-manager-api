package com.github.zj.dreamly.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zj.dreamly.modules.wx.util.WxMpServiceUtil;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.Query;
import com.github.zj.dreamly.modules.wx.dao.WxQrCodeMapper;
import com.github.zj.dreamly.modules.wx.entity.WxQrCode;
import com.github.zj.dreamly.modules.wx.form.WxQrCodeForm;
import com.github.zj.dreamly.modules.wx.service.WxQrCodeService;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Map;

/**
 * @author zj-dreamly
 */
@Service("wxQrCodeService")
@RequiredArgsConstructor
public class WxQrCodeServiceImpl extends ServiceImpl<WxQrCodeMapper, WxQrCode> implements WxQrCodeService {

    private final WxMpServiceUtil wxMpServiceUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String sceneStr = (String) params.get("sceneStr");
        String appid = (String) params.get("appid");
        IPage<WxQrCode> page = this.page(
                new Query<WxQrCode>().getPage(params),
                new QueryWrapper<WxQrCode>()
                        .eq(!StringUtils.isEmpty(appid), "appid", appid)
                        .like(!StringUtils.isEmpty(sceneStr), "scene_str", sceneStr)
        );

        return new PageUtils(page);
    }

    /**
     * 创建公众号带参二维码
     */
    @Override
    public WxMpQrCodeTicket createQrCode(String appid, WxQrCodeForm form) throws WxErrorException {
        WxMpQrCodeTicket ticket;
        if (form.getIsTemp()) {
            //创建临时二维码
            ticket = wxMpServiceUtil.switchoverTo(appid)
                    .getQrcodeService()
                    .qrCodeCreateTmpTicket(form.getSceneStr(), form.getExpireSeconds());

        }
        //创建永久二维码
        else {
            ticket = wxMpServiceUtil.switchoverTo(appid)
                    .getQrcodeService()
                    .qrCodeCreateLastTicket(form.getSceneStr());
        }
        WxQrCode wxQrCode = new WxQrCode(form, appid);
        wxQrCode.setTicket(ticket.getTicket());
        wxQrCode.setUrl(ticket.getUrl());
        if (form.getIsTemp()) {
            wxQrCode.setExpireTime(new Date(System.currentTimeMillis() + ticket.getExpireSeconds() * 1000L));
        }
        this.save(wxQrCode);
        return ticket;
    }

}

package com.github.zj.dreamly.modules.wx.handler;

import java.util.Map;

import com.github.zj.dreamly.modules.wx.service.WxUserService;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

import javax.annotation.Resource;

/**
 * @author Binary Wang
 */
@Component
public class UnsubscribeHandler extends AbstractHandler {
    @Resource
    WxUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        String openid = wxMessage.getFromUser();
        this.logger.info("【取消关注用户，openId】: " + openid);
        userService.unsubscribe(wxMessage.getAuthorizeAppId(),openid);
        return null;
    }

}

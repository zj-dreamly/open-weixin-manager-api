package com.github.zj.dreamly.modules.wx.handler;

import java.util.Map;

import com.github.zj.dreamly.modules.wx.service.MsgReplyService;
import com.github.zj.dreamly.modules.wx.service.WxUserService;
import org.springframework.stereotype.Component;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;

/**
 * @author Binary Wang
 */
@Component
public class SubscribeHandler extends AbstractHandler {
    @Resource
    MsgReplyService msgReplyService;
    @Resource
    WxUserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        this.logger.info("【新关注用户，openId】: " + wxMessage.getFromUser() + "，事件：" + wxMessage.getEventKey());
        String appid = wxMessage.getAuthorizeAppId();
        this.logger.info("appid:{}",appid);
        userService.refreshUserInfo(wxMessage.getFromUser(),appid);

        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());

        if (!StringUtils.isEmpty(wxMessage.getEventKey())) {
            // 处理特殊事件，如用户扫描带参二维码关注
            msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEventKey());
        }
        return null;
    }

    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    protected WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage) {
        this.logger.info("特殊请求-新关注用户 OPENID: " + wxMessage.getFromUser());
        //对关注事件和扫码事件分别处理
        String appid = wxMessage.getAuthorizeAppId();
        userService.refreshUserInfo(wxMessage.getFromUser(),appid);
        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEvent());
        if (!StringUtils.isEmpty(wxMessage.getEventKey())) {
            msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEventKey());
        }
        return null;
    }

}

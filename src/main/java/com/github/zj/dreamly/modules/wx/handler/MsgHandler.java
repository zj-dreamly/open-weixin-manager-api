package com.github.zj.dreamly.modules.wx.handler;

import java.util.Map;

import com.github.zj.dreamly.modules.wx.service.MsgReplyService;
import com.github.zj.dreamly.modules.wx.service.WxMsgService;
import com.github.zj.dreamly.modules.wx.entity.WxMsg;
import me.chanjar.weixin.common.api.WxConsts;
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
public class MsgHandler extends AbstractHandler {
    @Resource
    MsgReplyService msgReplyService;
    @Resource
    WxMsgService wxMsgService;
    private static final String TRANSFER_CUSTOMER_SERVICE_KEY = "人工";

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {

        String textContent = wxMessage.getContent();
        String fromUser = wxMessage.getFromUser();
        String appid = wxMessage.getAuthorizeAppId();
        boolean autoReplyed = msgReplyService.tryAutoReply(appid, false, fromUser, textContent);
        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        if (TRANSFER_CUSTOMER_SERVICE_KEY.equals(textContent) || !autoReplyed) {
            wxMsgService.addWxMsg(WxMsg.buildOutMsg(wxMessage.getAuthorizeAppId(),
                    WxConsts.KefuMsgType.TRANSFER_CUSTOMER_SERVICE, fromUser, null));
            return WxMpXmlOutMessage
                    .TRANSFER_CUSTOMER_SERVICE().fromUser(wxMessage.getToUser())
                    .toUser(fromUser).build();
        }
        return null;

    }

}

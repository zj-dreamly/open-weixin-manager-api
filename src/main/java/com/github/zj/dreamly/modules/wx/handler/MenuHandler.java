package com.github.zj.dreamly.modules.wx.handler;

import java.util.Map;

import com.github.zj.dreamly.modules.wx.service.MsgReplyService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class MenuHandler extends AbstractHandler {
    @Resource
    MsgReplyService msgReplyService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {
        if (WxConsts.EventType.VIEW.equals(wxMessage.getEvent())) {
            return null;
        }
        log.info("【菜单消息处理器收到消息】：{}", wxMessage.toString());
        String appid = wxMessage.getAuthorizeAppId();
        logger.info("菜单事件：" + wxMessage.getEventKey());
        msgReplyService.tryAutoReply(appid, true, wxMessage.getFromUser(), wxMessage.getEventKey());
        return null;
    }


}

package com.github.zj.dreamly.modules.wx.handler;

import com.github.zj.dreamly.common.utils.Json;
import com.github.zj.dreamly.modules.wx.entity.WxMsg;
import com.github.zj.dreamly.modules.wx.service.WxMsgService;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author Binary Wang
 */
@Component
public class LogHandler extends AbstractHandler {
    @Resource
    private WxMsgService wxMsgService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        try {
            this.logger.debug("\n【日志处理器接收到请求消息】：{}", Json.toJsonString(wxMessage));
            wxMsgService.addWxMsg(new WxMsg(wxMessage));
        } catch (Exception e) {
            this.logger.error("记录消息异常", e);
        }

        return null;
    }

}

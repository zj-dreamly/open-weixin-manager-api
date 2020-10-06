package com.github.zj.dreamly.modules.wx.util;

import lombok.AllArgsConstructor;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutTextMessage;
import me.chanjar.weixin.open.api.impl.WxOpenComponentServiceImpl;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: 苍海之南
 * @create: 2019-01-26 10:39
 **/
@Service
@AllArgsConstructor
public class Messageutils {

    private final WxOpenComponentServiceImpl wxOpenComponentService;

    /**
     * 设置微信文本消息
     */
    public String text(WxMpXmlMessage inMessage, String content) {

        final WxMpXmlOutTextMessage text = WxMpXmlOutMessage.TEXT()
                .content(content)
                .fromUser(inMessage.getToUser())
                .toUser(inMessage.getFromUser())
                .build();
        return WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(text, wxOpenComponentService.getWxOpenConfigStorage());
    }

    /**
     * 设置微信图文消息
     */

    public String news(WxMpXmlMessage inMessage, WxMpXmlOutNewsMessage.Item item) {
        final WxMpXmlOutNewsMessage news = WxMpXmlOutMessage.NEWS()
                .fromUser(inMessage.getToUser())
                .toUser(inMessage.getFromUser())
                .addArticle(item)
                .build();
        return WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(news, wxOpenComponentService.getWxOpenConfigStorage());
    }
}

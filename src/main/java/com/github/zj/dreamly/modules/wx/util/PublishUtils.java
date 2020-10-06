package com.github.zj.dreamly.modules.wx.util;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.kefu.WxMpKefuMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.open.api.impl.WxOpenComponentServiceImpl;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <h2>PublishUtils</h2>
 *
 * @author: 苍海之南
 * @since: 2020-06-19 15:27
 **/
@Slf4j
@Service
public class PublishUtils {

    private static final String TEST_COMPONENT_MSG_TYPE_TEXT = "TESTCOMPONENT_MSG_TYPE_TEXT";

    private static final String TEST_COMPONENT_MSG_TYPE_TEXT_CALLBACK = "TESTCOMPONENT_MSG_TYPE_TEXT_callback";

    private static final String QUERY_AUTH_CODE = "QUERY_AUTH_CODE:";

    private static final String EVENT = "event";

    private static final String TEXT = "text";

    /**
     * <h2>全网发布测试用例（全网发布/覆盖发布时用到）</h2>
     */
    public String publish(WxMpXmlMessage inMessage, String appId,
                          WxOpenComponentServiceImpl wxOpenComponentService) {
        String out = "";
        try {
            if (StringUtils.equals(inMessage.getMsgType(), TEXT)) {
                if (StringUtils.equals(inMessage.getContent(), TEST_COMPONENT_MSG_TYPE_TEXT)) {
                    out = WxOpenXmlMessage.wxMpOutXmlMessageToEncryptedXml(
                            WxMpXmlOutMessage.TEXT().content(TEST_COMPONENT_MSG_TYPE_TEXT_CALLBACK)
                                    .fromUser(inMessage.getToUser())
                                    .toUser(inMessage.getFromUser())
                                    .build(),
                            wxOpenComponentService.getWxOpenConfigStorage()
                    );
                }

                else if (StringUtils.startsWith(inMessage.getContent(), QUERY_AUTH_CODE)) {
                    String msg = inMessage.getContent().replace("QUERY_AUTH_CODE:",
                            "") + "_from_api";
                    WxMpKefuMessage kefuMessage = WxMpKefuMessage
                            .TEXT()
                            .content(msg)
                            .toUser(inMessage.getFromUser())
                            .build();
                    wxOpenComponentService.getWxMpServiceByAppid(appId)
                            .getKefuService()
                            .sendKefuMessage(kefuMessage);
                }
            }

            else if (StringUtils.equals(inMessage.getMsgType(), EVENT)) {
                WxMpKefuMessage kefuMessage = WxMpKefuMessage.TEXT()
                        .content(inMessage.getEvent() + "from_callback")
                        .toUser(inMessage.getFromUser())
                        .build();
                wxOpenComponentService.getWxMpServiceByAppid(appId)
                        .getKefuService()
                        .sendKefuMessage(kefuMessage);
            }
        } catch (WxErrorException e) {
            log.error("【callback】：{}", e.getMessage());
        }

        return out;
    }
}

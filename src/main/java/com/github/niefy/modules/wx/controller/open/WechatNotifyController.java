package com.github.niefy.modules.wx.controller.open;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.modules.wx.dto.WechatReceiveMessageDto;
import com.github.niefy.modules.wx.dto.WechatReceiveTicketDto;
import com.github.niefy.modules.wx.util.Messageutils;
import com.github.niefy.modules.wx.util.PublishUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.open.api.impl.WxOpenComponentServiceImpl;
import me.chanjar.weixin.open.bean.message.WxOpenXmlMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Objects;

/**
 * @author 苍海之南
 *
 * <h2>接受微信服务器相关请求</h2>
 */
@Slf4j
@RestController
@RequestMapping("/notify")
@AllArgsConstructor
public class WechatNotifyController {

    private static final String AES = "aes";

    private static final String SUCCESS = "success";

    private static final String TEST_APP_ID_1 = "wxd101a85aa106f53e";

    private static final String TEST_APP_ID_2 = "wx570bc396a51b8ff8";

    private static final String COMPONENT_VERIFY_TICKET = "wechat.open.componentVerifyTicket";

    private final WxOpenComponentServiceImpl wxOpenComponentService;

    private final Messageutils messageutils;

    private final PublishUtils publishUtils;

    private final SysConfigService sysConfigService;

    /**
     * <h2>接受微信服务器每10分钟颁发的Ticket</h2>
     */
    @ApiIgnore
    @RequestMapping("/receive_ticket")
    public Object receiveTicket(@RequestBody(required = false) String requestBody,
                                WechatReceiveTicketDto receiveTicketDto) {

        final String encType = receiveTicketDto.getEncrypt_type();

        final String timestamp = receiveTicketDto.getTimestamp();
        final String nonce = receiveTicketDto.getNonce();
        final String signature = receiveTicketDto.getSignature();
        final String msgSignature = receiveTicketDto.getMsg_signature();

        if (!StringUtils.equalsIgnoreCase(AES, encType)
                || !wxOpenComponentService.checkSignature(timestamp, nonce, signature)) {
            log.info(encType);
            throw new IllegalArgumentException("【非法请求，可能属于伪造的请求！】");
        }

        // aes加密的消息
        WxOpenXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedXml(requestBody,
                wxOpenComponentService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.info("\n【接收微信Ticket请求，消息解密后内容为】：\n{} ", inMessage.toString());
        try {
            String out = wxOpenComponentService.route(inMessage);

            // 更新ticket
            final SysConfigEntity sysConfig = sysConfigService.getSysConfig(COMPONENT_VERIFY_TICKET);
            if (Objects.isNull(sysConfig)){
                SysConfigEntity sysConfigEntity = new SysConfigEntity();
                sysConfigEntity.setParamKey(COMPONENT_VERIFY_TICKET);
                sysConfigEntity.setParamValue(inMessage.getComponentVerifyTicket());
                sysConfigService.save(sysConfigEntity);
            }else {
                sysConfig.setParamValue(inMessage.getComponentVerifyTicket());
                sysConfigService.updateById(sysConfig);
            }
            log.info("\n【回复信息】：{}", out);
        } catch (WxErrorException e) {
            log.error("【receive_ticket】：", e);
        }
        return SUCCESS;
    }

    /**
     * <h2>接受微信服务器消息回调</h2>
     */
    @ApiIgnore
    @RequestMapping("{appId}/callback")
    public Object callback(@RequestBody(required = false) String requestBody,
                           @PathVariable("appId") String appId,
                           WechatReceiveMessageDto receiveMessageDto) {

        final String encType = receiveMessageDto.getEncrypt_type();
        final String timestamp = receiveMessageDto.getTimestamp();
        final String nonce = receiveMessageDto.getNonce();
        final String signature = receiveMessageDto.getSignature();
        final String msgSignature = receiveMessageDto.getMsg_signature();

        if (!StringUtils.equalsIgnoreCase(AES, encType)
                || !wxOpenComponentService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("【非法请求，可能属于伪造的请求！】");
        }

        String out = SUCCESS;
        WxMpXmlMessage inMessage = WxOpenXmlMessage.fromEncryptedMpXml(requestBody,
                wxOpenComponentService.getWxOpenConfigStorage(), timestamp, nonce, msgSignature);
        log.info("\n【接收微信Message请求，消息解密后内容为】：\n{} ", inMessage.toString());

        if (StringUtils.equalsAnyIgnoreCase(appId, TEST_APP_ID_1, TEST_APP_ID_2)) {
            out = publishUtils.publish(inMessage, appId, wxOpenComponentService);
        }

        // 扫码事件触发
        if (StrUtil.isNotBlank(inMessage.getEventKey())) {
            out = sendMessage(appId, inMessage);
        }

        // 订阅事件触发
        if (StrUtil.isNotBlank(inMessage.getEvent())) {
            updateSubscribe(appId, inMessage);
        }

        return out;
    }

    /**
     * <h2>被动回复消息</h2>
     */
    private String sendMessage(String appId, WxMpXmlMessage inMessage) {
        String out = SUCCESS;
        final String eventKey = inMessage.getEventKey();

        messageutils.text(inMessage, eventKey);
        log.info("【公众号[{}]开始推送消息：{}】", appId, out);
        return out;
    }

    /**
     * <h2>关注/取消关注事件</h2>
     */
    private void updateSubscribe(String appId, WxMpXmlMessage inMessage) {

        if (WxConsts.EventType.SUBSCRIBE.equals(inMessage.getEvent())) {
            final String fromUser = inMessage.getFromUser();
            log.warn("【用户[{}]取消了公众号[{}]订阅】", fromUser, appId);

        }

        if (WxConsts.EventType.UNSUBSCRIBE.equals(inMessage.getEvent())) {
            final String fromUser = inMessage.getFromUser();
            log.warn("【用户[{}]订阅了公众号{}】", fromUser, appId);

        }
    }
}

package com.github.zj.dreamly.modules.wx.dto;

import lombok.Data;

/**
 * <h2>WechatReceiveMessageDto</h2>
 *
 * @author: 苍海之南
 * @since: 2020-06-19 17:32
 **/
@Data
public class WechatReceiveMessageDto {
    /**
     * 签名
     */
    private String signature;
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 随机串
     */
    private String nonce;
    /**
     * 触发用户的openid
     */
    private String openid;

    /**
     * 加密类型，为 aes
     */
    private String encrypt_type;
    /**
     *消息体签名，用于验证消息体的正确性
     */
    private String msg_signature;
}

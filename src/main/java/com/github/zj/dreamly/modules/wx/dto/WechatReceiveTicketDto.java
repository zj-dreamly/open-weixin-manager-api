package com.github.zj.dreamly.modules.wx.dto;

import lombok.Data;

/**
 * <h2>WechatReceiveTicketDto</h2>
 *
 * @author: 苍海之南
 * @since: 2020-06-19 17:32
 **/
@Data
public class WechatReceiveTicketDto {
    /**
     * 时间戳
     */
    private String timestamp;
    /**
     * 随机串
     */
    private String nonce;
    /**
     * 签名
     */
    private String signature;

    /**
     * 加密类型，为 aes
     */
    private String encrypt_type;
    /**
     *消息体签名，用于验证消息体的正确性
     */
    private String msg_signature;

}

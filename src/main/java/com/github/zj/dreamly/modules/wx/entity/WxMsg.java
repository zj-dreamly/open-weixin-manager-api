package com.github.zj.dreamly.modules.wx.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

import java.io.Serializable;
import java.util.Date;

/**
 * 微信消息
 *
 * @author niefy
 * @date 2020-05-14 17:28:34
 */
@Data
@TableName("wx_msg")
public class WxMsg implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId
    private Long id;
    private String appid;
    /**
     * 微信用户ID
     */
    private String openid;
    /**
     * 消息方向
     */
    private byte inOut;
    /**
     * 消息类型
     */
    private String msgType;
    /**
     * 消息详情
     */
    private String detail;
    /**
     * 创建时间
     */
    private Date createTime;

    public static class WxMsgInOut {
        static final byte IN = 0;
        static final byte OUT = 1;
    }

    public WxMsg() {
    }

    public WxMsg(WxMpXmlMessage wxMessage) {
        this.openid = wxMessage.getFromUser();
        this.appid = wxMessage.getAuthorizeAppId();
        this.inOut = WxMsgInOut.IN;
        this.msgType = wxMessage.getMsgType();

        final JSONObject jsonObject = new JSONObject();
        Long createTime = wxMessage.getCreateTime();
        this.createTime = createTime == null ? new Date() : new Date(createTime * 1000);
        if (WxConsts.XmlMsgType.TEXT.equals(this.msgType)) {
            jsonObject.put("content", wxMessage.getContent());
        } else if (WxConsts.XmlMsgType.IMAGE.equals(this.msgType)) {
            jsonObject.put("picUrl", wxMessage.getPicUrl());
            jsonObject.put("mediaId", wxMessage.getMediaId());
        } else if (WxConsts.XmlMsgType.VOICE.equals(this.msgType)) {
            jsonObject.put("format", wxMessage.getFormat());
            jsonObject.put("mediaId", wxMessage.getMediaId());
        } else if (WxConsts.XmlMsgType.VIDEO.equals(this.msgType) ||
                WxConsts.XmlMsgType.SHORTVIDEO.equals(this.msgType)) {
            jsonObject.put("thumbMediaId", wxMessage.getThumbMediaId());
            jsonObject.put("mediaId", wxMessage.getMediaId());
        } else if (WxConsts.XmlMsgType.LOCATION.equals(this.msgType)) {
            jsonObject.put("locationX", wxMessage.getLocationX());
            jsonObject.put("locationY", wxMessage.getLocationY());
            jsonObject.put("scale", wxMessage.getScale());
            jsonObject.put("label", wxMessage.getLabel());
        } else if (WxConsts.XmlMsgType.LINK.equals(this.msgType)) {
            jsonObject.put("title", wxMessage.getTitle());
            jsonObject.put("description", wxMessage.getDescription());
            jsonObject.put("url", wxMessage.getUrl());
        } else if (WxConsts.XmlMsgType.EVENT.equals(this.msgType)) {
            jsonObject.put("event", wxMessage.getEvent());
            jsonObject.put("eventKey", wxMessage.getEventKey());
        }

        this.detail = jsonObject.toJSONString();
    }

    public static WxMsg buildOutMsg(String appid, String msgType, String openid, JSONObject detail) {
        WxMsg wxMsg = new WxMsg();
        wxMsg.appid = appid;
        wxMsg.msgType = msgType;
        wxMsg.openid = openid;
        wxMsg.detail = detail.toJSONString();
        wxMsg.createTime = new Date();
        wxMsg.inOut = WxMsgInOut.OUT;
        return wxMsg;
    }
}

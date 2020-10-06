package com.github.zj.dreamly.modules.wx.service;

import com.github.zj.dreamly.modules.wx.form.TemplateMsgBatchForm;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateMessage;

public interface TemplateMsgService {
    /**
     * 发送微信模版消息
     */
    void sendTemplateMsg(WxMpTemplateMessage msg,String appid);

    /**
     * 批量消息发送
     */
    void sendMsgBatch(TemplateMsgBatchForm form,String appid);
}

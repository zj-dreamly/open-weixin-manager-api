package com.github.niefy.modules.wx.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class WxMsgReplyForm {
    @NotEmpty(message = "公众号appid不能为空")
    private String appid;
    @NotEmpty(message = "用户信息不得为空")
    private String openid;
    @NotEmpty(message = "回复类型不得为空")
    private String replyType;
    @NotEmpty(message = "回复内容不得为空")
    private String replyContent;
}

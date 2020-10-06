package com.github.zj.dreamly.modules.wx.form;

import com.github.zj.dreamly.common.exception.RRException;
import com.github.zj.dreamly.common.utils.Json;
import lombok.Data;

@Data
public class TemplateMsgForm {
    private String openid;
    private String msg;
    private String template;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }

    public boolean isValid() {
        if (openid == null || openid.isEmpty() || msg == null || msg.isEmpty() || template == null || template.isEmpty()) {
            throw new RRException("缺少必要参数");
        }
        return true;
    }
}

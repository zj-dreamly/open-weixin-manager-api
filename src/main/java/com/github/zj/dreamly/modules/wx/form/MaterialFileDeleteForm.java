package com.github.zj.dreamly.modules.wx.form;

import com.github.zj.dreamly.common.utils.Json;
import lombok.Data;

@Data
public class MaterialFileDeleteForm {
    String mediaId;

    @Override
    public String toString() {
        return Json.toJsonString(this);
    }
}

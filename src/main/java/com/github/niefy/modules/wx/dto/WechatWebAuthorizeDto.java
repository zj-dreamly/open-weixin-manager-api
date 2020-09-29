package com.github.niefy.modules.wx.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <h2>WechatWebAuthorizeDto</h2>
 *
 * @author: 苍海之南
 * @since: 2020-06-23 10:59
 **/
@Data
public class WechatWebAuthorizeDto {

    @ApiModelProperty(value = "授权公众号的appid", required = true)
    private String appId;

    @ApiModelProperty(value = "授权回调地址，支持域名，ip地址，必须加http//", required = true)
    private String redirectUrl;

    @ApiModelProperty(value = "授权类型", required = true)
    private String authorizeType;

    @ApiModelProperty(value = "回调方式，如果为hash，请传递hash，否则可忽略")
    private String mode;
}

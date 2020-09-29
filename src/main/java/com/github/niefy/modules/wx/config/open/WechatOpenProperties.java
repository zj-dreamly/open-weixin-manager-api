package com.github.niefy.modules.wx.config.open;

import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author <a href="https://github.com/007gzs">007</a>
 */
@Component
@ConfigurationProperties(prefix = "wechat.open")
@Data
public class WechatOpenProperties {
    /**
     * 设置微信三方平台的appid
     */
    private String componentAppId;

    /**
     * 设置微信三方平台的app secret
     */
    private String componentSecret;

    /**
     * 设置微信三方平台的token
     */
    private String componentToken;

    /**
     * 设置微信三方平台的EncodingAESKey
     */
    private String componentAesKey;

    /**
     * 设置微信三方平台的回调域名
     */
    private String wechatOpenAuthorizeUrl;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.MULTI_LINE_STYLE);
    }
}

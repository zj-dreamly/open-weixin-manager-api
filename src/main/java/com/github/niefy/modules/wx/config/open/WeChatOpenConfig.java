package com.github.niefy.modules.wx.config.open;

import com.github.niefy.modules.sys.entity.SysConfigEntity;
import com.github.niefy.modules.sys.service.SysConfigService;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.service.WxAccountService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.open.api.impl.WxOpenComponentServiceImpl;
import me.chanjar.weixin.open.api.impl.WxOpenInMemoryConfigStorage;
import me.chanjar.weixin.open.api.impl.WxOpenServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 微信开放平台配置
 *
 * @author: zj-dreamly
 * @since: 2018-11-09 18:53
 **/
@Slf4j
@Component
@AllArgsConstructor
public class WeChatOpenConfig {

    private final WechatOpenProperties wechatOpenProperties;

    private final WxAccountService wxAccountService;

    private final SysConfigService sysConfigService;

    /**
     * 微信开放平台服务类
     *
     * @return {@link WxOpenComponentServiceImpl}
     */
    @Bean
    @SneakyThrows
    public WxOpenComponentServiceImpl wxOpenComponentServiceImpl() {
        WxOpenServiceImpl wxOpenServiceImpl = new WxOpenServiceImpl();

        final SysConfigEntity wechatOpenComponentAppId = sysConfigService.getSysConfig("wechat.open.componentAppId");
        final SysConfigEntity wechatOpenComponentSecret = sysConfigService.getSysConfig("wechat.open.ComponentSecret");
        final SysConfigEntity wechatOpenComponentToken = sysConfigService.getSysConfig("wechat.open.ComponentToken");
        final SysConfigEntity wechatComponentAesKey = sysConfigService.getSysConfig("wechat.open.ComponentAesKey");

        WxOpenInMemoryConfigStorage memoryConfigStorage = new WxOpenInMemoryConfigStorage();
        memoryConfigStorage.setComponentAppId(wechatOpenComponentAppId.getParamValue());
        memoryConfigStorage.setComponentAppSecret(wechatOpenComponentSecret.getParamValue());
        memoryConfigStorage.setComponentToken(wechatOpenComponentToken.getParamValue());
        memoryConfigStorage.setComponentAesKey(wechatComponentAesKey.getParamValue());
        wxOpenServiceImpl.setWxOpenConfigStorage(memoryConfigStorage);

        // 设置授权公众号的refresh_token
        final List<WxAccount> accountList = wxAccountService.list();
        for (WxAccount wxAccount : accountList) {
            memoryConfigStorage.setAuthorizerRefreshToken(wxAccount.getAppid(),
                    wxAccount.getRefreshToken());
        }
        // 设置10分钟推送一次的ticket，防止项目重启，内存中的ticket失效，需要在推送的回调接口中更新
        final SysConfigEntity sysConfig = sysConfigService.getSysConfig("wechat.open.componentVerifyTicket");
        memoryConfigStorage.setComponentVerifyTicket(sysConfig.getParamValue());
        return new WxOpenComponentServiceImpl(wxOpenServiceImpl);
    }
}

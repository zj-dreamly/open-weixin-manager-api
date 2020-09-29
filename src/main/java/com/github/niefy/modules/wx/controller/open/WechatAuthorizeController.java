package com.github.niefy.modules.wx.controller.open;

import com.baomidou.mybatisplus.extension.api.R;
import com.github.niefy.modules.wx.config.open.WechatOpenProperties;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author: 苍海之南
 * @since: 2019-04-29 16:12
 *
 * <h2>微信公众号开发者授权</h2>
 **/
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/authorize")
@Api(value = "微信公众号开发者授权", tags = "微信公众号开发者授权")
public class WechatAuthorizeController {

    private final WechatOpenProperties wechatOpenProperties;

    private final WxOpenComponentService wxOpenComponentService;

    /**
     * <h2>获取微信公众号授权链接</h2>
     */
    @GetMapping()
    @ApiOperation(value = "获取授权页面链接", notes = "此链接只能由微信开放平台配置的授权发起域名跳转")
    public R<String> getAuthUrl() {

        String url = wechatOpenProperties.getWechatOpenAuthorizeUrl() + "/authorize/auth/callback";
        try {
            url = wxOpenComponentService.getPreAuthUrl(url);
        } catch (WxErrorException e) {
            throw new SecurityException("获取授权页面链接失败");
        }
        return R.ok(url);
    }

    /**
     * <h2>公众号授权回调</h2>
     */
    @GetMapping("/auth/callback")
    @ApiIgnore
    public R<WxOpenQueryAuthResult> jump(@RequestParam("auth_code") String authorizationCode) {
        try {
            WxOpenQueryAuthResult queryAuthResult = wxOpenComponentService.getQueryAuth(authorizationCode);
            log.info("【getQueryAuth】：{}", queryAuthResult);
            return R.ok(queryAuthResult);
        } catch (WxErrorException e) {
            log.error("【gotoPreAuthUrl】：{}", e.getMessage());
            throw new SecurityException(e.getMessage());
        }
    }
}

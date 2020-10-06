package com.github.zj.dreamly.modules.wx.controller.open;

import cn.hutool.core.util.StrUtil;
import com.github.zj.dreamly.modules.wx.config.open.WechatOpenProperties;
import com.github.zj.dreamly.modules.wx.dto.WechatWebAuthorizeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.WxMpUserService;
import me.chanjar.weixin.mp.api.impl.WxMpUserServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * @author: 苍海之南
 * @since: 2019-04-29 16:12
 *
 * <h2>微信公众号网页授权</h2>
 **/
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/web/authorize")
@Api(value = "微信公众号开发者授权", tags = "微信公众号开发者授权")
public class WechatWebAuthorizeController {

    private static final String LANG = "zh_CN";

    private final WechatOpenProperties wechatOpenProperties;

    private final WxOpenComponentService wxOpenComponentService;

    private static final String HASH = "hash";

    /**
     * <h2>发起微信网页授权</h2>
     */
    @GetMapping()
    @ApiOperation(value = "发起微信网页授权", notes = "关于授权类型：" +
            "snsapi_base：不弹出授权页面，直接跳转，只能获取用户openid." +
            "snsapi_userinfo：弹出授权页面，可通过openid拿到昵称、性别、所在地。并且，即使在未关注的情况下，只要用户授权，也能获取其信息." +
            "snsapi_privateinfo：手动授权,可获取成员的详细信息,包含手机、邮箱。只适用于企业微信或企业号.")
    public void toAthorize(HttpServletResponse response, WechatWebAuthorizeDto wechatWebAuthorizeDto) {

        String redirectUrl = wechatWebAuthorizeDto.getRedirectUrl();
        if (StrUtil.isNotBlank(wechatWebAuthorizeDto.getMode()) && HASH.equals(wechatWebAuthorizeDto.getMode())) {
            redirectUrl = redirectUrl + wechatWebAuthorizeDto.getMode();
        }
        String url = wechatOpenProperties.getWechatOpenAuthorizeUrl() + "/web/authorize/wx_mp_user";
        String wxRedirectUrl = wxOpenComponentService.oauth2buildAuthorizationUrl(wechatWebAuthorizeDto.getAppId(),
                url, wechatWebAuthorizeDto.getAuthorizeType(), redirectUrl);

        try {
            response.sendRedirect(wxRedirectUrl);
        } catch (IOException e) {
            e.printStackTrace();
            log.info("【跳转失败】");
        }
    }

    /**
     * <h2>网页授权回调</h2>
     */
    @ApiIgnore
    @GetMapping("/wx_mp_user")
    @ResponseBody
    public void wxUserInfo(@RequestParam("code") String code,
                           @RequestParam("state") String state,
                           @RequestParam("appid") String appId,
                           HttpServletResponse response) throws IOException {

        WxMpUser wxMpUser = new WxMpUser();
        boolean subscribe = false;
        final WxMpOAuth2AccessToken oauth2getAccessToken;
        try {
            oauth2getAccessToken = wxOpenComponentService.oauth2getAccessToken(appId, code);
            final WxMpService wxMpService = wxOpenComponentService.getWxMpServiceByAppid(appId);
            wxMpUser = wxMpService.oauth2getUserInfo(oauth2getAccessToken, LANG);

            WxMpUserService wxMpUserService = new WxMpUserServiceImpl(wxMpService);
            final WxMpUser subscribeWxMpUser = wxMpUserService.userInfo(oauth2getAccessToken.getOpenId());

            if (!Objects.isNull(subscribeWxMpUser.getSubscribe())) {
                subscribe = subscribeWxMpUser.getSubscribe();
            }
        } catch (WxErrorException e) {
            log.info("【获取授权用户信息失败】");
            e.printStackTrace();
        }

        log.info("【授权用户】：{}", wxMpUser);

        String redirectUrl;
        if (StrUtil.endWith(state, HASH)) {
            final String removeSuffix = StrUtil.removeSuffix(state, HASH);
            redirectUrl = removeSuffix + "/#/?openId=" + wxMpUser.getOpenId();
        } else {
            redirectUrl = state + "?openId=" + wxMpUser.getOpenId();
        }
        log.info("即将重定向到：{}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

}

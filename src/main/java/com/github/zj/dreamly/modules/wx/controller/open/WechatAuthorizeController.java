package com.github.zj.dreamly.modules.wx.controller.open;

import com.github.zj.dreamly.modules.wx.config.open.WechatOpenProperties;
import com.github.zj.dreamly.modules.wx.entity.WxAccount;
import com.github.zj.dreamly.modules.wx.enums.MpAuthorizeType;
import com.github.zj.dreamly.modules.wx.service.WxAccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.open.api.WxOpenComponentService;
import me.chanjar.weixin.open.bean.result.WxOpenAuthorizerInfoResult;
import me.chanjar.weixin.open.bean.result.WxOpenQueryAuthResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

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

    private final WxAccountService wxAccountService;

    @GetMapping("/show")
    @ResponseBody
    public String gotoPreAuthUrlShow(){
        return "<a href='do'>点击前往微信开放平台授权</a>";
    }

    /**
     * <h2>获取微信公众号授权链接</h2>
     */
    @GetMapping("/do")
    @SneakyThrows
    @ApiOperation(value = "获取授权页面链接", notes = "此链接只能由微信开放平台配置的授权发起域名跳转")
    public void getAuthUrl(HttpServletResponse response) {

        String url = wechatOpenProperties.getWechatOpenAuthorizeUrl() + "/authorize/auth/callback";
        try {
            url = wxOpenComponentService.getPreAuthUrl(url);
        } catch (WxErrorException e) {
            throw new SecurityException("获取授权页面链接失败");
        }
        response.sendRedirect(url);
    }

    /**
     * <h2>公众号授权回调</h2>
     * https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/api/api_get_authorizer_info.html#%E5%85%AC%E4%BC%97%E5%8F%B7%E8%AE%A4%E8%AF%81%E7%B1%BB%E5%9E%8B
     */
    @GetMapping("/auth/callback")
    @ApiIgnore
    public String jump(@RequestParam("auth_code") String authorizationCode) {
        try {
            WxOpenQueryAuthResult queryAuthResult = wxOpenComponentService.getQueryAuth(authorizationCode);
            log.info("【getQueryAuth】：{}", queryAuthResult);

            final String appid = queryAuthResult.getAuthorizationInfo().getAuthorizerAppid();
            final WxOpenAuthorizerInfoResult authorizerInfo = wxOpenComponentService.getAuthorizerInfo(appid);

            final WxAccount wxAccount = wxAccountService.getById(appid);
            if (Objects.isNull(wxAccount)) {
                WxAccount newWxAccount = new WxAccount();
                newWxAccount.setAppid(appid);
                newWxAccount.setName(authorizerInfo.getAuthorizerInfo().getNickName());
                newWxAccount.setType(authorizerInfo.getAuthorizerInfo().getServiceTypeInfo());
                newWxAccount.setVerified(authorizerInfo.getAuthorizerInfo().getVerifyTypeInfo() == 0);
                newWxAccount.setRefreshToken(queryAuthResult.getAuthorizationInfo().getAuthorizerRefreshToken());
                newWxAccount.setAuthorizeType(MpAuthorizeType.OPEN.name());

                wxAccountService.getBaseMapper().insert(newWxAccount);
            } else {
                wxAccount.setRefreshToken(queryAuthResult.getAuthorizationInfo().getAuthorizerRefreshToken());
                wxAccountService.updateById(wxAccount);
            }
            return "已成功授权，请返回管理台查看。";
        } catch (WxErrorException e) {
            log.error("【gotoPreAuthUrl】：{}", e.getMessage());
            throw new SecurityException(e.getMessage());
        }
    }
}

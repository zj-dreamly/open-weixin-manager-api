package com.github.niefy.modules.wx.controller;

import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.service.WxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 微信用户（粉丝）
 * @author zj-dreamly
 */
@RestController
@RequestMapping("/wxUser")
@RequiredArgsConstructor
@Api(tags = {"微信粉丝"})
public class WxUserController {
    @Resource
    WxUserService wxUserService;
    private final WxMpService wxMpService;

    @GetMapping("/getUserInfo")
    @ApiOperation(value = "获取粉丝信息")
    public R getUserInfo(@RequestParam String appid, @CookieValue String openid) {
        this.wxMpService.switchoverTo(appid);
        WxUser wxUser = wxUserService.getById(openid);
        return R.ok().put(wxUser);
    }
}

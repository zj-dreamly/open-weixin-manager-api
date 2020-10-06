package com.github.zj.dreamly.modules.wx.controller;

import com.github.zj.dreamly.common.utils.R;
import com.github.zj.dreamly.modules.wx.entity.WxUser;
import com.github.zj.dreamly.modules.wx.service.WxUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 微信用户（粉丝）
 *
 * @author zj-dreamly
 */
@RestController
@RequestMapping("/wxUser")
@RequiredArgsConstructor
@Api(tags = {"微信粉丝"})
public class WxUserController {
    @Resource
    WxUserService wxUserService;

    @GetMapping("/getUserInfo")
    @ApiOperation(value = "获取粉丝信息")
    public R getUserInfo(@RequestParam String appid, @CookieValue String openid) {
        WxUser wxUser = wxUserService.getById(openid);
        return R.ok().put(wxUser);
    }
}

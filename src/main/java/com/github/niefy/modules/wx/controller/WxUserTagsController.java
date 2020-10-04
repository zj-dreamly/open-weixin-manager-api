package com.github.niefy.modules.wx.controller;

import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.entity.WxUser;
import com.github.niefy.modules.wx.form.WxUserTaggingForm;
import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.chanjar.weixin.common.error.WxError;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 粉丝标签
 *
 * @author chzn
 */
@RestController
@RequestMapping("/wxUserTags")
@RequiredArgsConstructor
@Api(tags = {"粉丝标签"})
public class WxUserTagsController {

    @Resource
    WxUserTagsService wxUserTagsService;

    @Resource
    WxUserService wxUserService;

    @GetMapping("/userTags")
    @ApiOperation(value = "当前用户的标签")
    public R userTags(@RequestParam String appid, @CookieValue String openid) {
        if (openid == null) {
            return R.error("none_openid");
        }
        WxUser wxUser = wxUserService.getById(openid);
        if (wxUser == null) {
            wxUser = wxUserService.refreshUserInfo(openid, appid);
            if (wxUser == null) {
                return R.error("not_subscribed");
            }
        }
        return R.ok().put(wxUser.getTagidList());
    }

    @PostMapping("/tagging")
    @ApiOperation(value = "给用户绑定标签")
    public R tagging(@RequestParam String appid, @CookieValue String openid, @RequestBody WxUserTaggingForm form) {
        try {
            wxUserTagsService.tagging(appid, form.getTagid(), openid);
        } catch (WxErrorException e) {
            WxError error = e.getError();
            //未关注公众号
            if (50005 == error.getErrorCode()) {
                return R.error("not_subscribed");
            } else {
                return R.error(error.getErrorMsg());
            }
        }
        return R.ok();
    }

    @PostMapping("/untagging")
    @ApiOperation(value = "解绑标签")
    public R untagging(@RequestParam String appid, @CookieValue String openid, @RequestBody WxUserTaggingForm form) throws WxErrorException {
        wxUserTagsService.untagging(appid, form.getTagid(), openid);
        return R.ok();
    }
}

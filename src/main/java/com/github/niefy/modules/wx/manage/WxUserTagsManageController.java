package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.form.WxUserBatchTaggingForm;
import com.github.niefy.modules.wx.form.WxUserTagForm;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 公众号用户标签
 *
 * @author zj-dreamly
 */
@RestController
@RequestMapping("/manage/wxUserTags")
@Api(tags = {"公众号用户标签-管理后台"})
public class WxUserTagsManageController {
    @Resource
    private WxUserTagsService wxUserTagsService;

    /**
     * 查询用户标签
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:wxuser:info")
    @ApiOperation(value = "列表")
    public R list(@RequestParam String appid) throws WxErrorException {
        List<WxUserTag> wxUserTags = wxUserTagsService.getWxTags(appid);
        return R.ok().put("list", wxUserTags);
    }

    /**
     * 修改或新增标签
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:wxuser:save")
    @ApiOperation(value = "保存")
    public R save(@RequestParam String appid, @RequestBody WxUserTagForm form) throws WxErrorException {
        Long tagid = form.getId();
        if (tagid == null || tagid <= 0) {
            wxUserTagsService.creatTag(appid, form.getName());
        } else {
            wxUserTagsService.updateTag(appid, tagid, form.getName());
        }
        return R.ok();
    }

    /**
     * 删除标签
     */
    @PostMapping("/delete/{tagid}")
    @RequiresPermissions("wx:wxuser:save")
    @ApiOperation(value = "删除标签")
    public R delete(@RequestParam String appid, @PathVariable("tagid") Long tagid) throws WxErrorException {
        if (tagid == null || tagid <= 0) {
            return R.error("标签ID不得为空");
        }
        wxUserTagsService.deleteTag(appid, tagid);
        return R.ok();
    }

    /**
     * 批量给用户打标签
     */
    @PostMapping("/batchTagging")
    @RequiresPermissions("wx:wxuser:save")
    @ApiOperation(value = "批量给用户打标签")
    public R batchTagging(@RequestParam String appid, @RequestBody WxUserBatchTaggingForm form) throws WxErrorException {
        wxUserTagsService.batchTagging(appid, form.getTagid(), form.getOpenidList());
        return R.ok();
    }

    /**
     * 批量移除用户标签
     */
    @PostMapping("/batchUnTagging")
    @RequiresPermissions("wx:wxuser:save")
    @ApiOperation(value = "批量移除用户标签")
    public R batchUnTagging(@RequestParam String appid, @RequestBody WxUserBatchTaggingForm form) throws WxErrorException {
        wxUserTagsService.batchUnTagging(appid, form.getTagid(), form.getOpenidList());
        return R.ok();
    }
}

package com.github.zj.dreamly.modules.wx.manage;

import com.github.zj.dreamly.modules.wx.service.MsgReplyRuleService;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.R;
import com.github.zj.dreamly.modules.wx.entity.MsgReplyRule;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 自动回复规则
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgReplyRule")
@Api(tags = {"自动回复规则-管理后台"})
@RequiredArgsConstructor
public class MsgReplyRuleManageController {

    private final MsgReplyRuleService msgReplyRuleService;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:msgreplyrule:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam String appid, @RequestParam Map<String, Object> params) {
        params.put("appid", appid);
        PageUtils page = msgReplyRuleService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{ruleId}")
    @RequiresPermissions("wx:msgreplyrule:info")
    @ApiOperation(value = "详情")
    public R info(@PathVariable("ruleId") Integer ruleId) {
        MsgReplyRule msgReplyRule = msgReplyRuleService.getById(ruleId);
        return R.ok().put("msgReplyRule", msgReplyRule);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:msgreplyrule:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.save(msgReplyRule);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("wx:msgreplyrule:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody MsgReplyRule msgReplyRule) {
        msgReplyRuleService.updateById(msgReplyRule);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:msgreplyrule:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody Integer[] ruleIds) {
        msgReplyRuleService.removeByIds(Arrays.asList(ruleIds));
        return R.ok();
    }

}
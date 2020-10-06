package com.github.zj.dreamly.modules.wx.manage;

import com.github.zj.dreamly.modules.wx.service.MsgTemplateService;
import com.github.zj.dreamly.modules.wx.service.TemplateMsgService;
import com.github.zj.dreamly.modules.wx.util.WxMpServiceUtil;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.R;
import com.github.zj.dreamly.modules.wx.entity.MsgTemplate;
import com.github.zj.dreamly.modules.wx.form.TemplateMsgBatchForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * 消息模板
 *
 * @author niefy
 * @email niefy@qq.com
 * @date 2019-11-12 18:30:15
 */
@RestController
@RequestMapping("/manage/msgTemplate")
@Api(tags = {"消息模板-管理后台", "模板消息的模板"})
@AllArgsConstructor
public class MsgTemplateManageController {

    private final MsgTemplateService msgTemplateService;

    private final TemplateMsgService templateMsgService;

    private final WxMpServiceUtil wxMpServiceUtil;

    /**
     * 列表
     */
    @GetMapping("/list")
    @RequiresPermissions("wx:msgtemplate:list")
    @ApiOperation(value = "列表")
    public R list(@RequestParam String appid, @RequestParam Map<String, Object> params) {
        params.put("appid", appid);
        PageUtils page = msgTemplateService.queryPage(params);
        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{id}")
    @RequiresPermissions("wx:msgtemplate:info")
    @ApiOperation(value = "详情-通过ID")
    public R info(@PathVariable("id") Long id) {
        MsgTemplate msgTemplate = msgTemplateService.getById(id);
        return R.ok().put("msgTemplate", msgTemplate);
    }

    /**
     * 信息
     */
    @GetMapping("/getByName")
    @RequiresPermissions("wx:msgtemplate:info")
    @ApiOperation(value = "详情-通过名称")
    public R getByName(String name) {
        MsgTemplate msgTemplate = msgTemplateService.selectByName(name);
        return R.ok().put("msgTemplate", msgTemplate);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "保存")
    public R save(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.save(msgTemplate);
        return R.ok();
    }

    /**
     * 修改
     */
    @PostMapping("/update")
    @RequiresPermissions("wx:msgtemplate:update")
    @ApiOperation(value = "修改")
    public R update(@RequestBody MsgTemplate msgTemplate) {
        msgTemplateService.updateById(msgTemplate);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    @RequiresPermissions("wx:msgtemplate:delete")
    @ApiOperation(value = "删除")
    public R delete(@RequestBody String[] ids) {
        msgTemplateService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

    /**
     * 同步公众号模板
     */
    @PostMapping("/syncWxTemplate")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "同步公众号模板")
    public R syncWxTemplate(@RequestParam String appid) throws WxErrorException {
        msgTemplateService.syncWxTemplate(appid);
        return R.ok();
    }

    /**
     * 批量向用户发送模板消息
     * 通过用户筛选条件（一般使用标签筛选），将消息发送给数据库中所有符合筛选条件的用户
     */
    @PostMapping("/sendMsgBatch")
    @RequiresPermissions("wx:msgtemplate:save")
    @ApiOperation(value = "批量向用户发送模板消息", notes = "将消息发送给数据库中所有符合筛选条件的用户")
    public R sendMsgBatch(@RequestParam String appid, @RequestBody TemplateMsgBatchForm form) {
        templateMsgService.sendMsgBatch(form, appid);
        return R.ok();
    }

}

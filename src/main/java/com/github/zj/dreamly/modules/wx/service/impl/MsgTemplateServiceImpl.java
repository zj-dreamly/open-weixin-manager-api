package com.github.zj.dreamly.modules.wx.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zj.dreamly.modules.wx.util.WxMpServiceUtil;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.Query;
import com.github.zj.dreamly.modules.wx.dao.MsgTemplateMapper;
import com.github.zj.dreamly.modules.wx.entity.MsgTemplate;
import com.github.zj.dreamly.modules.wx.service.MsgTemplateService;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author admin
 */
@Service("msgTemplateService")
@AllArgsConstructor
public class MsgTemplateServiceImpl extends ServiceImpl<MsgTemplateMapper, MsgTemplate> implements MsgTemplateService {

    private final WxMpServiceUtil wxMpServiceUtil;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String title = (String) params.get("title");
        String name = (String) params.get("name");
        String appid = (String) params.get("appid");
        IPage<MsgTemplate> page = this.page(
                new Query<MsgTemplate>().getPage(params),
                new QueryWrapper<MsgTemplate>()
                        .eq(!StringUtils.isEmpty(appid), "appid", appid)
                        .like(!StringUtils.isEmpty(title), "title", title)
                        .like(!StringUtils.isEmpty(name), "name", name)
        );

        return new PageUtils(page);
    }

    @Override
    public MsgTemplate selectByName(String name) {
        Assert.hasText(name, "模板名称不得为空");
        return this.getOne(new QueryWrapper<MsgTemplate>()
                .eq("name", name)
                .eq("status", 1)
                .last("LIMIT 1"));
    }

    @Override
    public void syncWxTemplate(String appid) throws WxErrorException {
        List<WxMpTemplate> wxMpTemplateList = wxMpServiceUtil.switchoverTo(appid)
                .getTemplateMsgService().getAllPrivateTemplate();

        List<MsgTemplate> templates = wxMpTemplateList.stream()
                .map(item -> new MsgTemplate(item, appid)).collect(Collectors.toList());

        final List<MsgTemplate> list = this.list(Wrappers.<MsgTemplate>lambdaQuery().eq(MsgTemplate::getAppid, appid));

        final List<String> names = list.stream()
                .map(MsgTemplate::getName)
                .collect(Collectors.toList());

        final List<MsgTemplate> filterTemplates = templates.stream()
                .filter(item -> !names.contains(item.getName()))
                .collect(Collectors.toList());

        if (CollectionUtil.isNotEmpty(filterTemplates)) {
            this.saveBatch(templates);
        }
    }

}

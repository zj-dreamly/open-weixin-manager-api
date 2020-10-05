package com.github.niefy.modules.wx.service.impl;

import com.github.niefy.modules.wx.service.WxUserService;
import com.github.niefy.modules.wx.service.WxUserTagsService;
import com.github.niefy.modules.wx.util.WxMpServiceUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.tag.WxUserTag;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zj-dreamly
 */
@Service
@Slf4j
public class WxUserTagsServiceImpl implements WxUserTagsService {

    @Resource
    private WxMpServiceUtil wxMpServiceUtil;

    @Resource
    private WxUserService wxUserService;

    @Override
    public List<WxUserTag> getWxTags(String appid) throws WxErrorException {
        log.info("拉取公众号用户标签");
        return wxMpServiceUtil.switchoverTo(appid).getUserTagService().tagGet();
    }

    @Override
    public void creatTag(String appid, String formName) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().tagCreate(formName);
    }

    @Override
    public void updateTag(String appid, Long tagid, String name) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().tagUpdate(tagid, name);
    }

    @Override
    public void deleteTag(String appid, Long tagid) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().tagDelete(tagid);
    }

    @Override
    public void batchTagging(String appid, Long tagid, String[] openidList) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().batchTagging(tagid, openidList);
        //标签更新后更新对应用户信息
        wxUserService.refreshUserInfoAsync(openidList, appid);
    }

    @Override
    public void batchUnTagging(String appid, Long tagid, String[] openidList) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().batchUntagging(tagid, openidList);
        //标签更新后更新对应用户信息
        wxUserService.refreshUserInfoAsync(openidList, appid);
    }

    @Override
    public void tagging(String appid, Long tagid, String openid) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().batchTagging(tagid, new String[]{openid});
        wxUserService.refreshUserInfo(openid, appid);
    }

    @Override
    public void untagging(String appid, Long tagid, String openid) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid).getUserTagService().batchUntagging(tagid, new String[]{openid});
        wxUserService.refreshUserInfo(openid, appid);
    }

}

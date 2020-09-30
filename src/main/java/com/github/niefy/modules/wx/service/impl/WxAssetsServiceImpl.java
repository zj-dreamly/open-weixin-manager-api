package com.github.niefy.modules.wx.service.impl;

import com.github.niefy.modules.wx.dto.PageSizeConstant;
import com.github.niefy.modules.wx.service.WxAssetsService;
import com.github.niefy.modules.wx.util.WxMpServiceUtil;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Service
@CacheConfig(cacheNames = {"wxAssetsServiceCache"})
@Slf4j
public class WxAssetsServiceImpl implements WxAssetsService {
    @Autowired
    WxMpService wxMpService;
    @Autowired
    WxMpServiceUtil wxMpServiceUtil;

    @Override
    public WxMpMaterialCountResult materialCount(String appid) throws WxErrorException {
        log.info("从API获取素材总量");
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialCount();
    }

    @Override
    public WxMpMaterialNews materialNewsInfo(String appid, String mediaId) throws WxErrorException {
        log.info("从API获取图文素材详情,mediaId={}", mediaId);
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialNewsInfo(mediaId);
    }

    @Override
    public WxMpMaterialFileBatchGetResult materialFileBatchGet(String appid, String type, int page) throws WxErrorException {
        log.info("从API获取媒体素材列表,type={},page={}", type, page);
        final int pageSize = PageSizeConstant.PAGE_SIZE_SMALL;
        int offset = (page - 1) * pageSize;
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialFileBatchGet(type, offset, pageSize);
    }

    @Override
    public WxMpMaterialNewsBatchGetResult materialNewsBatchGet(String appid, int page) throws WxErrorException {
        log.info("从API获取媒体素材列表,page={}", page);
        final int pageSize = PageSizeConstant.PAGE_SIZE_SMALL;
        int offset = (page - 1) * pageSize;
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialNewsBatchGet(offset, pageSize);
    }

    @Override
    public WxMpMaterialUploadResult materialNewsUpload(String appid, List<WxMpNewsArticle> articles) throws WxErrorException {
        log.info("上传图文素材...");
        Assert.notEmpty(articles, "图文列表不得为空");
        WxMpMaterialNews news = new WxMpMaterialNews();
        news.setArticles(articles);
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialNewsUpload(news);
    }

    /**
     * 更新图文素材中的某篇文章
     */
    @Override
    public void materialArticleUpdate(String appid, WxMpMaterialArticleUpdate form) throws WxErrorException {
        log.info("更新图文素材...");
        wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialNewsUpdate(form);
    }

    @Override
    public WxMpMaterialUploadResult materialFileUpload(String appid, String mediaType, String fileName, MultipartFile file) throws WxErrorException, IOException {
        log.info("上传媒体素材：{}", fileName);
        String originalFilename = file.getOriginalFilename();
        File tempFile = File.createTempFile(fileName + "--",
                Objects.requireNonNull(originalFilename).substring(originalFilename.lastIndexOf(".")));
        file.transferTo(tempFile);
        WxMpMaterial wxMaterial = new WxMpMaterial();
        wxMaterial.setFile(tempFile);
        wxMaterial.setName(fileName);
        if (WxConsts.MediaFileType.VIDEO.equals(mediaType)) {
            wxMaterial.setVideoTitle(fileName);
        }
        WxMpMaterialUploadResult res = wxMpServiceUtil
                .switchoverTo(appid)
                .getMaterialService()
                .materialFileUpload(mediaType, wxMaterial);
        tempFile.deleteOnExit();
        return res;
    }

    @Override
    public boolean materialDelete(String appid, String mediaId) throws WxErrorException {
        log.info("删除素材，mediaId={}", mediaId);
        return wxMpServiceUtil.switchoverTo(appid).getMaterialService().materialDelete(mediaId);
    }
}

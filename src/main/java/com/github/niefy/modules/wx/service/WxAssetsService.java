package com.github.niefy.modules.wx.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.material.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface WxAssetsService {
    /**
     *  获取素材总数
     */
    WxMpMaterialCountResult materialCount(String appid) throws WxErrorException;

    /**
     * 获取图文素材详情
     */
    WxMpMaterialNews materialNewsInfo(String appid, String mediaId) throws WxErrorException;
    /**
     * 根据类别分页获取非图文素材列表
     */
    WxMpMaterialFileBatchGetResult materialFileBatchGet(String appid, String type, int page) throws WxErrorException;

    /**
     * 分页获取图文素材列表
     */
    WxMpMaterialNewsBatchGetResult materialNewsBatchGet(String appid, int page) throws WxErrorException;

    /**
     * 添加图文永久素材
     */
    WxMpMaterialUploadResult materialNewsUpload(String appid, List<WxMpNewsArticle> articles)throws WxErrorException;

    /**
     * 更新图文素材中的某篇文章
     */
    void materialArticleUpdate(String appid, WxMpMaterialArticleUpdate form) throws WxErrorException;

    /**
     * 添加多媒体永久素材
     */
    WxMpMaterialUploadResult materialFileUpload(String appid, String mediaType, String fileName, MultipartFile file) throws WxErrorException, IOException;

    /**
     * 删除素材
     */
    boolean materialDelete(String appid, String mediaId)throws WxErrorException;
}

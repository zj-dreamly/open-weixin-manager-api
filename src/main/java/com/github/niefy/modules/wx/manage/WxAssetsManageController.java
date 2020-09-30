package com.github.niefy.modules.wx.manage;

import com.github.niefy.common.utils.R;
import com.github.niefy.modules.wx.form.MaterialFileDeleteForm;
import com.github.niefy.modules.wx.service.WxAssetsService;
import com.github.niefy.modules.wx.util.WxMpServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.material.*;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 微信公众号素材管理
 * 参考官方文档：https://developers.weixin.qq.com/doc/offiaccount/Asset_Management/New_temporary_materials.html
 * 参考WxJava开发文档：https://github.com/Wechat-Group/WxJava/wiki/MP_永久素材管理
 * @author zj-dreamly
 */
@RestController
@RequestMapping("/manage/wxAssets")
@Api(tags = {"公众号素材-管理后台"})
@AllArgsConstructor
public class WxAssetsManageController {

    private final WxAssetsService wxAssetsService;

    private final WxMpServiceUtil wxMpServiceUtil;

    /**
     * 获取素材总数
     */
    @GetMapping("/materialCount")
    @ApiOperation(value = "文件素材总数")
    public R materialCount(@RequestParam String appid) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialCountResult res = wxAssetsService.materialCount();
        return R.ok().put(res);
    }

    /**
     * 获取素材总数
     */
    @GetMapping("/materialNewsInfo")
    @ApiOperation(value = "图文素材总数")
    public R materialNewsInfo(@RequestParam String appid,String mediaId) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialNews res = wxAssetsService.materialNewsInfo(mediaId);
        return R.ok().put(res);
    }


    /**
     * 根据类别分页获取非图文素材列表
     */
    @GetMapping("/materialFileBatchGet")
    @RequiresPermissions("wx:wxassets:list")
    @ApiOperation(value = "根据类别分页获取非图文素材列表")
    public R materialFileBatchGet(@RequestParam String appid,@RequestParam(defaultValue = "image") String type,
                                  @RequestParam(defaultValue = "1") int page) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialFileBatchGetResult res = wxAssetsService.materialFileBatchGet(type,page);
        return R.ok().put(res);
    }

    /**
     * 分页获取图文素材列表
     */
    @GetMapping("/materialNewsBatchGet")
    @RequiresPermissions("wx:wxassets:list")
    @ApiOperation(value = "分页获取图文素材列表")
    public R materialNewsBatchGet(@RequestParam String appid,@RequestParam(defaultValue = "1") int page) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialNewsBatchGetResult res = wxAssetsService.materialNewsBatchGet(page);
        return R.ok().put(res);
    }

    /**
     * 添加图文永久素材
     */
    @PostMapping("/materialNewsUpload")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "添加图文永久素材")
    public R materialNewsUpload(@RequestParam String appid,@RequestBody List<WxMpNewsArticle> articles) throws WxErrorException {
        if(articles.isEmpty()) {
            return R.error("图文列表不得为空");
        }
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialUploadResult res = wxAssetsService.materialNewsUpload(articles);
        return R.ok().put(res);
    }

    /**
     * 修改图文素材文章
     */
    @PostMapping("/materialArticleUpdate")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "修改图文素材文章")
    public R materialArticleUpdate(@RequestParam String appid,@RequestBody WxMpMaterialArticleUpdate form) throws WxErrorException {
        if(form.getArticles()==null) {
            return R.error("文章不得为空");
        }
        wxMpServiceUtil.switchoverTo(appid);
        wxAssetsService.materialArticleUpdate(form);
        return R.ok();
    }

    /**
     * 添加多媒体永久素材
     */
    @PostMapping("/materialFileUpload")
    @RequiresPermissions("wx:wxassets:save")
    @ApiOperation(value = "添加多媒体永久素材")
    public R materialFileUpload(@RequestParam String appid,MultipartFile file, String fileName, String mediaType) throws WxErrorException, IOException {
        if (file == null) {
            return R.error("文件不得为空");
        }
        wxMpServiceUtil.switchoverTo(appid);
        WxMpMaterialUploadResult res = wxAssetsService.materialFileUpload(mediaType,fileName,file);
        return R.ok().put(res);
    }

    /**
     * 删除素材
     */
    @PostMapping("/materialDelete")
    @RequiresPermissions("wx:wxassets:delete")
    @ApiOperation(value = "删除素材")
    public R materialDelete(@RequestParam String appid,@RequestBody MaterialFileDeleteForm form) throws WxErrorException {
        wxMpServiceUtil.switchoverTo(appid);
        boolean res = wxAssetsService.materialDelete(form.getMediaId());
        return R.ok().put(res);
    }

}

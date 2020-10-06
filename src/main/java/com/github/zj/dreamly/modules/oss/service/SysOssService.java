package com.github.zj.dreamly.modules.oss.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zj.dreamly.modules.oss.entity.SysOssEntity;
import com.github.zj.dreamly.common.utils.PageUtils;

import java.util.Map;

/**
 * 文件上传
 * @author Mark sunlightcs@gmail.com
 */
public interface SysOssService extends IService<SysOssEntity> {
    /**
     * 分页查询用户数据
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);
}

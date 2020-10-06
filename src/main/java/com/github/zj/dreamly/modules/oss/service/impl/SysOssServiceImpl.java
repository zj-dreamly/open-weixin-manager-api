package com.github.zj.dreamly.modules.oss.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.zj.dreamly.modules.oss.dao.SysOssDao;
import com.github.zj.dreamly.modules.oss.entity.SysOssEntity;
import com.github.zj.dreamly.modules.oss.service.SysOssService;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.common.utils.Query;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * @author zj-dreamly
 */
@Service("sysOssService")
public class SysOssServiceImpl extends ServiceImpl<SysOssDao, SysOssEntity> implements SysOssService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SysOssEntity> page = this.page(
            new Query<SysOssEntity>().getPage(params)
        );
        return new PageUtils(page);
    }

}

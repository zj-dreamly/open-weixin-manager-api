package com.github.niefy.modules.wx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.github.niefy.common.utils.PageUtils;
import com.github.niefy.common.utils.Query;
import com.github.niefy.modules.wx.dao.WxAccountMapper;
import com.github.niefy.modules.wx.entity.WxAccount;
import com.github.niefy.modules.wx.enums.MpAuthorizeType;
import com.github.niefy.modules.wx.service.WxAccountService;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.config.WxMpConfigStorage;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zj-dreamly
 */
@Service("wxAccountService")
public class WxAccountServiceImpl extends ServiceImpl<WxAccountMapper, WxAccount> implements WxAccountService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    WxMpService wxMpService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String name = (String) params.get("name");
        IPage<WxAccount> page = this.page(
                new Query<WxAccount>().getPage(params),
                new QueryWrapper<WxAccount>()
                        .like(!StringUtils.isEmpty(name), "name", name)
        );

        return new PageUtils(page);
    }

    @PostConstruct
    public void loadWxMpConfigStorages() {
        logger.info("加载公众号配置...");
        List<WxAccount> accountList = this.listByType(MpAuthorizeType.MP);;
        if (accountList == null || accountList.isEmpty()) {
            logger.info("未读取到公众号配置，请在管理后台添加");
            return;
        }
        logger.info("加载到{}条公众号配置", accountList.size());
        accountList.forEach(this::addAccountToRuntime);
        logger.info("公众号配置加载完成");
    }

    @Override
    public boolean save(WxAccount entity) {
        Assert.notNull(entity, "WxAccount不得为空");
        String appid = entity.getAppid();
        //已有此appid信息，更新
        if (this.isAccountInRuntime(appid)) {
            logger.info("更新公众号配置");
            wxMpService.removeConfigStorage(appid);
            this.addAccountToRuntime(entity);
            return SqlHelper.retBool(this.baseMapper.updateById(entity));
        } else {//已有此appid信息，新增
            logger.info("新增公众号配置");
            this.addAccountToRuntime(entity);
            entity.setAuthorizeType(MpAuthorizeType.MP.name());
            return SqlHelper.retBool(this.baseMapper.insert(entity));
        }

    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        Assert.notEmpty(idList, "WxAccount不得为空");

        // 更新wxMpService配置
        logger.info("同步移除公众号配置");
        idList.forEach(id -> wxMpService.removeConfigStorage((String) id));

        return SqlHelper.retBool(this.baseMapper.deleteBatchIds(idList));
    }

    @Override
    public List<WxAccount> listByType(MpAuthorizeType mpAuthorizeType) {
        return this.list(Wrappers.<WxAccount>lambdaQuery().eq(WxAccount::getAuthorizeType, mpAuthorizeType.name()));
    }

    /**
     * 判断当前账号是存在
     */
    private boolean isAccountInRuntime(String appid) {
        try {
            return wxMpService.switchover(appid);
        } catch (NullPointerException e) {// sdk bug，未添加任何账号时configStorageMap为null会出错
            return false;
        }
    }

    /**
     * 添加账号到当前程序，如首次添加需初始化configStorageMap
     */
    private synchronized void addAccountToRuntime(WxAccount entity) {
        String appid = entity.getAppid();
        WxMpDefaultConfigImpl config = entity.toWxMpConfigStorage();
        try {
            wxMpService.addConfigStorage(appid, config);
        } catch (NullPointerException e) {
            logger.info("需初始化configStorageMap...");
            Map<String, WxMpConfigStorage> configStorages = new HashMap<>(4);
            configStorages.put(appid, config);
            wxMpService.setMultiConfigStorages(configStorages, appid);
        }
    }

}
package com.github.zj.dreamly.modules.wx.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.zj.dreamly.common.utils.PageUtils;
import com.github.zj.dreamly.modules.wx.entity.Article;
import com.github.zj.dreamly.modules.wx.enums.ArticleTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author chzn
 */
public interface ArticleService extends IService<Article> {
    /**
     * 分页查询用户数据
     *
     * @param params 查询参数
     * @return PageUtils 分页结果
     */
    PageUtils queryPage(Map<String, Object> params);

    /**
     * 查询文章详情，每次查询后增加点击次数
     *
     * @param id id
     * @return {@link Article}
     */
    Article findById(int id);

    /**
     * 添加或编辑文章,同名文章不可重复添加
     *
     * @param article {@link Article}
     */

    @Override
    boolean save(Article article);

    /**
     * 按条件分页查询
     *
     * @param title 标题
     * @param page  分页参数
     * @return {@link Article}
     */
    IPage<Article> getArticles(String title, int page);

    /**
     * 查看目录，不返回文章详情字段
     *
     * @param articleType 文章类型
     * @param category    分类
     * @return {@link Article}
     */
    List<Article> selectCategory(ArticleTypeEnum articleType, String category);

    /**
     * 文章查找，不返回文章详情字段
     *
     * @param articleType 文章类型
     * @param category    分类
     * @param keywords    关键字
     * @return {@link Article}
     */
    List<Article> search(ArticleTypeEnum articleType, String category, String keywords);
}

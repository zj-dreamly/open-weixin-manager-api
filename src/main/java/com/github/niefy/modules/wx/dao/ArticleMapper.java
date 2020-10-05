package com.github.niefy.modules.wx.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.niefy.modules.wx.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.scheduling.annotation.Async;

/**
 * @author zj-dreamly
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    @Async
    void addOpenCount(int id);
}

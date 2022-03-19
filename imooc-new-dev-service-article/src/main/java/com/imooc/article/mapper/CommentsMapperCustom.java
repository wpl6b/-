package com.imooc.article.mapper;

import com.imooc.pojo.vo.CommentsVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentsMapperCustom {
    public List<CommentsVO> queryArticleCommentList(@Param("paramMap") Map<String, Object> map);
}

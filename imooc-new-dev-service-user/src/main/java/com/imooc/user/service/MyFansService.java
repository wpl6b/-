package com.imooc.user.service;

import com.imooc.pojo.vo.RegionRatioVO;
import com.imooc.utils.PagedGridResult;

import java.util.List;

public interface MyFansService {

    public boolean isMeFollowThisWriter(String writerId, String fanId);
    public void follow(String writerId, String fanId);
    public void unfollow(String writerId, String fanId);

    public PagedGridResult queryMyFanList(String writerId, Integer page, Integer pageSize);

    public Integer queryFansCountBySex(String writerId, Integer sex);

    public List<RegionRatioVO> queryFansCountByRegion(String writerId);
}

package com.imooc.user.controller;

import com.imooc.api.BaseController;
import com.imooc.api.controller.user.MyFansControllerApi;
import com.imooc.enums.Sex;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.pojo.vo.FansCountsVO;
import com.imooc.pojo.vo.RegionRatioVO;
import com.imooc.user.service.MyFansService;
import com.imooc.utils.PagedGridResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MyFansController extends BaseController implements MyFansControllerApi {

    @Autowired
    private MyFansService myFansService;

    @Override
    public GraceJSONResult isMeFollowThisWriter(String writerId, String fanId) {

        return GraceJSONResult.ok(myFansService.isMeFollowThisWriter(writerId, fanId));
    }

    @Override
    public GraceJSONResult follow(String writerId, String fanId) {

        myFansService.follow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult unfollow(String writerId, String fanId) {
        myFansService.unfollow(writerId, fanId);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult queryAll(String writerId, Integer page, Integer pageSize) {
        if(page == null)    page = COMMON_START_PAGE;

        if(pageSize == null) pageSize = COMMON_PAGE_SIZE;

        PagedGridResult pagedGridResult = myFansService.queryMyFanList(writerId, page, pageSize);
        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult queryRatio(String writerId) {
        FansCountsVO fansCountsVO = new FansCountsVO();
        Integer men = myFansService.queryFansCountBySex(writerId, Sex.man.type);
        Integer women = myFansService.queryFansCountBySex(writerId, Sex.woman.type);
        fansCountsVO.setManCounts(men);
        fansCountsVO.setWomanCounts(women);
        return GraceJSONResult.ok(fansCountsVO);
    }

    @Override
    public GraceJSONResult queryRatioByRegion(String writerId) {
        List<RegionRatioVO> regionRatioVOS = myFansService.queryFansCountByRegion(writerId);
        return GraceJSONResult.ok(regionRatioVOS);
    }
}

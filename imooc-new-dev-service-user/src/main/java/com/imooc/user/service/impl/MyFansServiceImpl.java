package com.imooc.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.imooc.api.BaseService;
import com.imooc.pojo.AppUser;
import com.imooc.pojo.Fans;
import com.imooc.pojo.vo.RegionRatioVO;
import com.imooc.user.mapper.FansMapper;
import com.imooc.user.service.MyFansService;
import com.imooc.user.service.UserService;
import com.imooc.utils.PagedGridResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyFansServiceImpl extends BaseService implements MyFansService {

    @Autowired
    private FansMapper fansMapper;

    @Autowired
    private UserService userService;


    @Autowired
    Sid sid;

    @Override
    public boolean isMeFollowThisWriter(String writerId, String fanId) {
        Fans fans = new Fans();
        fans.setWriterId(writerId);
        fans.setFanId(fanId);
        int count = fansMapper.selectCount(fans);
        return count == 1;
    }

    @Transactional
    @Override
    public void follow(String writerId, String fanId) {

        //获取粉丝用户信息
        AppUser fanInfo = userService.getUser(fanId);

        //构建插入数据
        Fans fans = new Fans();
        String fansPk = sid.nextShort();
        fans.setId(fansPk);
        fans.setWriterId(writerId);
        fans.setFanId(fanId);
        fans.setFace(fanInfo.getFace());
        fans.setFanNickname(fanInfo.getNickname());
        fans.setSex(fanInfo.getSex());
        fans.setProvince(fanInfo.getProvince());

        fansMapper.insert(fans);

        redis.increment(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        redis.increment(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
    }

    @Transactional
    @Override
    public void unfollow(String writerId, String fanId) {
        Example example = new Example(Fans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("writerId", writerId);
        criteria.andEqualTo("fanId", fanId);
        fansMapper.deleteByExample(example);

        redis.decrement(REDIS_WRITER_FANS_COUNTS + ":" + writerId, 1);
        redis.decrement(REDIS_MY_FOLLOW_COUNTS + ":" + fanId, 1);
    }

    @Override
    public PagedGridResult queryMyFanList(String writerId, Integer page, Integer pageSize) {
        Fans fans = new Fans();
        fans.setWriterId(writerId);

        PageHelper.startPage(page, pageSize);
        List<Fans> fansList = fansMapper.select(fans);
        return setterPagedGrid(fansList, page);
    }

    @Override
    public Integer queryFansCountBySex(String writerId, Integer sex) {
        Fans fans = new Fans();
        fans.setWriterId(writerId);
        fans.setSex(sex);
        Integer count = fansMapper.selectCount(fans);
        return count;
    }

    public static final String[] regions = {"北京", "天津", "上海", "重庆",
            "河北", "山西", "辽宁", "吉林", "黑龙江", "江苏", "浙江", "安徽", "福建", "江西", "山东",
            "河南", "湖北", "湖南", "广东", "海南", "四川", "贵州", "云南", "陕西", "甘肃", "青海", "台湾",
            "内蒙古", "广西", "西藏", "宁夏", "新疆",
            "香港", "澳门"};

    @Override
    public List<RegionRatioVO> queryFansCountByRegion(String writerId) {
        ArrayList<RegionRatioVO> list = new ArrayList<>();

        Fans fans = new Fans();
        fans.setWriterId(writerId);


        for (String region :
                regions) {
            fans.setProvince(region);
            int count = fansMapper.selectCount(fans);

            RegionRatioVO regionRatioVO = new RegionRatioVO();
            regionRatioVO.setName(region);
            regionRatioVO.setValue(count);
            list.add(regionRatioVO);
        }

        return list;
    }
}

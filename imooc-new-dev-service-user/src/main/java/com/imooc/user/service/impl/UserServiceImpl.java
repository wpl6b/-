package com.imooc.user.service.impl;

import com.imooc.enums.Sex;
import com.imooc.enums.UserStatus;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AppUser;
import com.imooc.pojo.bo.UpdateUserInfoBO;
import com.imooc.user.mapper.AppUserMapper;
import com.imooc.user.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.RedisOperator;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;

import static com.imooc.api.BaseController.REDIS_USER_INFO;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private AppUserMapper userMapper;

    @Autowired
    private Sid sid;

    @Autowired
    private RedisOperator redis;

    private static final String USER_FACE0 = "https://gimg2.baidu.com/";
    private static final String USER_FACE1 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201901%2F30%2F20190130123128_rsmyu.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631011600&t=7764ec96c96282ba9b4ddf36c0d2d333";
    private static final String USER_FACE2 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201810%2F28%2F20181028201723_chjja.jpg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631011600&t=39fe2d4820a919c4209fcab13099dd15";
    private static final String USER_FACE3 = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201903%2F15%2F20190315094443_5R33j.thumb.700_0.jpeg&refer=http%3A%2F%2Fb-ssl.duitang.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1631011600&t=b4a37273e698331c9051586efeb0daed";
    @Override
    public AppUser queryMobileIsExist(String mobile) {

        Example example = new Example(AppUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("mobile", mobile);
        AppUser user = userMapper.selectOneByExample(example);
        return user;

    }

    @Transactional
    @Override
    public AppUser createUser(String mobile) {
        AppUser user = new AppUser();
        user.setId(sid.nextShort());
        user.setMobile(mobile);
        user.setNickname("用户" + DesensitizationUtil.commonDisplay(mobile));
        user.setFace(USER_FACE1);
        user.setBirthday(DateUtil.stringToDate("1900-01-01"));
        user.setSex(Sex.secret.type);
        user.setActiveStatus(UserStatus.INACTIVE.type);
        user.setTotalIncome(0);
        user.setCreatedTime(new Date());
        user.setUpdatedTime(new Date());

        userMapper.insert(user);
        return user;
    }

    @Override
    public AppUser getUser(String userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public void updateUserInfo(UpdateUserInfoBO updateUserInfoBO) {

        String userId = updateUserInfoBO.getId();

        //双写一致
        redis.del(userId);
//------------------------------此处可能涌入大量请求-----------------------mysql未来得及更新
        AppUser user = new AppUser();
        BeanUtils.copyProperties(updateUserInfoBO, user);
        user.setActiveStatus(UserStatus.ACTIVE.type);
        user.setUpdatedTime(new Date());
        if(userMapper.updateByPrimaryKeySelective(user) != 1){
            GraceException.display(ResponseStatusEnum.USER_UPDATE_ERROR);
        }

//        // 再次查询用户的最新信息，放入redis中
//        //这不是controller的getUser()  而是从mysql中直接getUser()
//        user = getUser(userId);
//        redis.set(REDIS_USER_INFO + ":" + userId, JsonUtils.objectToJson(user));
//        这一段可以删除！

        try {        /*缓存双删          question：如果更新的时候只更新mysql不更新redis（上面的那一步） 是不是就不需要进行双删呢?
                                          answer: 不是！ 如果在第一次删除redis.del(userId) 后， mysql还未来得及更新数据，
                                                 此时有大量请求通过UserController中的getUser()获取数据，
                                                 则单删没有意义，此时redis中还是会放入旧数据。

                     */

            Thread.sleep(100);
            redis.del(REDIS_USER_INFO + ":" + userId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

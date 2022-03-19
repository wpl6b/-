package com.imooc.admin.controller;

import com.imooc.admin.service.AdminUserService;
import com.imooc.api.BaseController;
import com.imooc.api.controller.admin.AdminMngControllerApi;
import com.imooc.enums.FaceVerifyType;
import com.imooc.exception.GraceException;
import com.imooc.grace.result.GraceJSONResult;
import com.imooc.grace.result.ResponseStatusEnum;
import com.imooc.pojo.AdminUser;
import com.imooc.pojo.bo.AdminLoginBO;
import com.imooc.pojo.bo.NewAdminBO;
import com.imooc.utils.FaceVerifyUtils;
import com.imooc.utils.PagedGridResult;
import com.imooc.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;
import java.util.UUID;

@RestController
public class AdminMngController extends BaseController implements AdminMngControllerApi {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private RedisOperator redis;

    @Autowired
    private RestTemplate restTemplate;


    @Autowired
    private FaceVerifyUtils faceVerifyUtils;

    private void doLoginSettings(AdminUser adminUser,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {
        // 保存token放入到redis中
        String token = UUID.randomUUID().toString().trim();
        redis.set(REDIS_ADMIN_TOKEN + ":" + adminUser.getId(), token);
        // 保存admin登录基本token信息到cookie中
        setCookie(request,response, "atoken", token,COOKIE_MONTH);
        setCookie(request,response, "aname", adminUser.getAdminName(),COOKIE_MONTH);
        setCookie(request,response, "aid", adminUser.getId(),COOKIE_MONTH);
    }

    @Override
    public GraceJSONResult adminLogin(@Valid AdminLoginBO adminLoginBO, BindingResult result, HttpServletRequest request, HttpServletResponse response) {
        //校验参数是否完整
        if(result.hasErrors()){
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

        AdminUser adminUser = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        if(adminUser == null)   return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIST_ERROR);

        boolean checkpw = BCrypt.checkpw(adminLoginBO.getPassword(), adminUser.getPassword());

        if(!checkpw)    return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NOT_EXIST_ERROR);

        doLoginSettings(adminUser, request, response);
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult adminIsExist(String username) {
        checkAdminExist(username);
        return GraceJSONResult.ok();
    }

    private void checkAdminExist(String username){
        AdminUser adminUser = adminUserService.queryAdminByUsername(username);
        if(adminUser != null){
            GraceException.display(ResponseStatusEnum.ADMIN_USERNAME_EXIST_ERROR);
        }
    }

    @Override
    public GraceJSONResult addNewAdmin(@Valid NewAdminBO newAdminBO, BindingResult result,HttpServletRequest request, HttpServletResponse response) {
        //校验参数是否完整
        if(result.hasErrors()){
            Map<String, String> errorMap = getErrors(result);
            return GraceJSONResult.errorMap(errorMap);
        }

//        //人脸未入库 则必须使用密码进行创建
//        if(StringUtils.isBlank(newAdminBO.getImg64())){
//            //密码为空 或确认密码为空
//            if(StringUtils.isBlank(newAdminBO.getPassword()) || StringUtils.isBlank(newAdminBO.getConfirmPassword())){
//                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
//            }else {
//
//                //两次密码不一致
//                if(!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())){
//                    return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
//                }
//
//                //还来check?
//                checkAdminExist(newAdminBO.getUsername());
//
//                adminUserService.createAdminUser(newAdminBO);
//                return GraceJSONResult.ok();
//            }

        //未添加人脸 则必须使用密码进行创建
        if(StringUtils.isBlank(newAdminBO.getImg64())){
            //密码为空 或确认密码为空
            if(StringUtils.isBlank(newAdminBO.getPassword()) || StringUtils.isBlank(newAdminBO.getConfirmPassword())){
                return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_NULL_ERROR);
            }
        }

        //人脸未添加需要保证两次密码相同  人脸入库可以不输入密码 若输入也要求相同
        if(StringUtils.isNotBlank(newAdminBO.getPassword()) && StringUtils.isNotBlank(newAdminBO.getConfirmPassword())){
                //两次密码不一致
                if(!newAdminBO.getPassword().equalsIgnoreCase(newAdminBO.getConfirmPassword())){
                    return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_PASSWORD_ERROR);
                }
        }

        //TO--DO
        //校验用户名唯一
        checkAdminExist(newAdminBO.getUsername());

        //调用service存入admin信息
        adminUserService.createAdminUser(newAdminBO);
        return GraceJSONResult.ok();

    }

    @Override
    public GraceJSONResult getAdminList(Integer page, Integer pageSize) {
        if(page == null)    page = COMMON_START_PAGE;
        if(pageSize == null)    pageSize = COMMON_PAGE_SIZE;

//        PageInfo<AdminUser> adminUserPageInfo =
        PagedGridResult pagedGridResult = adminUserService.queryAdminList(page, pageSize);

        return GraceJSONResult.ok(pagedGridResult);
    }

    @Override
    public GraceJSONResult adminLogout(String adminId, HttpServletRequest request, HttpServletResponse response) {
        redis.del(REDIS_ADMIN_TOKEN + ":" + adminId);
        deleteCookie(request, response,"atoken");
        deleteCookie(request, response,"aname");
        deleteCookie(request, response,"aid");
        return GraceJSONResult.ok();
    }

    @Override
    public GraceJSONResult adminFaceLogin(AdminLoginBO adminLoginBO, HttpServletRequest request, HttpServletResponse response) {
        String userName = adminLoginBO.getUsername();
        if(StringUtils.isBlank(userName))   return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_NAME_NULL_ERROR);

        String img64 = adminLoginBO.getImg64();
        if(StringUtils.isBlank(img64))  return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_NULL_ERROR);
//        System.out.println("img64:-------------------------------------");
//        System.out.println(img64);

        AdminUser adminUser = adminUserService.queryAdminByUsername(adminLoginBO.getUsername());
        String faceId = adminUser.getFaceId();
        if(StringUtils.isBlank(faceId)) return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);

        String url = "http://files.imoocnews.com:8004/fs/readFace64InGridFS?faceId=" + faceId;
        ResponseEntity<GraceJSONResult> responseEntity = restTemplate.getForEntity(url, GraceJSONResult.class);
        GraceJSONResult responseEntityBody = responseEntity.getBody();

//        String base64 = (String) responseEntityBody.getData();
        String base64 = responseEntityBody.getData().toString();  //效果同上

        //替换换行符 响应的时候为什么会给base64 加上 “\r\n”呢？
        String base64Replace = base64.replace("\r\n", "");

        // 3. 调用阿里ai进行人脸对比识别，判断可信度，从而实现人脸登录
        boolean result = faceVerifyUtils.faceVerify(FaceVerifyType.BASE64.type,
                img64,
                base64Replace,
                70);

        if (!result) {
            return GraceJSONResult.errorCustom(ResponseStatusEnum.ADMIN_FACE_LOGIN_ERROR);
        }

        // 4. admin登录后的数据设置，redis与cookie
        doLoginSettings(adminUser, request, response);

        return GraceJSONResult.ok();
    }
}

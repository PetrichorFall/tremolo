package com.ttd.api.controller;

import com.ttd.common.util.IMoocJSONResult;
import com.ttd.common.util.MD5Utils;
import com.ttd.pojo.entity.Users;
import com.ttd.pojo.entity.vo.UserVO;
import com.ttd.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Api(value="用户注册登录的接口", tags= {"注册和登录的controller"})
public class RegisterLoginController extends BasicController{

    private static final Logger log = LoggerFactory.getLogger(RegisterLoginController.class);

    @Autowired
    private UserService userService;

    @ApiOperation(value="用户登录", notes="用户登录的接口")
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public IMoocJSONResult login(@RequestBody Users user) throws Exception {

        String userName = user.getUsername();
        String password = user.getPassword();
        log.info("the login is {}",userName);
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)){
            return IMoocJSONResult.errorMsg("用户名或者密码不能为空！！！");
        }
        Users userResult = userService.queryUserForLogin(userName, MD5Utils.getMD5Str(password));
        if (null != userResult){
            userResult.setPassword("");
            UserVO userVO = setUserRedisSessionToken(userResult);
            return IMoocJSONResult.ok(userVO);
        }else {
            return IMoocJSONResult.errorMsg("用户名或者密码不正确！！！");
        }

    }

    @ApiOperation(value="用户注册", notes="用户注册的接口")
    @PostMapping("/regist")
    public IMoocJSONResult register(@RequestBody Users users) throws Exception {
        log.info("start to regist ");
        long start = System.currentTimeMillis();
    //        简单验证
        if (StringUtils.isBlank(users.getUsername()) || StringUtils.isBlank(users.getPassword())){
            return IMoocJSONResult.errorMsg("用户名或者密码不能为空！！！");
        }

    //  判断用户是否存在
        boolean userNameIsExists = userService.userIsExist(users.getUsername());
        log.info("用户是否存在，{}",userNameIsExists);
        if (!userNameIsExists){
            users.setNickname(users.getUsername());
            users.setPassword(MD5Utils.getMD5Str(users.getPassword()));
            users.setFansCounts(0);
            users.setReceiveLikeCounts(0);
            users.setFollowCounts(0);
            userService.saveUser(users);
        }else {
            return IMoocJSONResult.errorMsg("用户名已经存在，请换一个再试");
        }
        users.setPassword("");
        UserVO userVO = setUserRedisSessionToken(users);
        log.info("end of regist method and cost :{}",System.currentTimeMillis()-start);
        return IMoocJSONResult.ok(userVO);
    }

    /**
     * 保存token到Redis
     * @param userResult 登录信息
     * @return UserVO
     */
    private UserVO setUserRedisSessionToken(Users userResult) {

        String uniqueId = UUID.randomUUID().toString();
        // 保存一个月
        redis.set(USER_REDIS_SESSION + ":" + userResult.getId(),uniqueId,1000*60*30);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userResult,userVO);
        userVO.setUserToken(uniqueId);
        return userVO;
    }

    @ApiOperation(value="用户注销", notes="用户注销的接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    @PostMapping("/logout")
    public IMoocJSONResult logout(String userId) throws Exception {
        log.info("the userId is ========{}",userId);
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return IMoocJSONResult.ok();
    }
}

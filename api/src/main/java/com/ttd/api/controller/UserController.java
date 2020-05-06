package com.ttd.api.controller;

import com.ttd.common.idWork.utils.Utils;
import com.ttd.common.util.IMoocJSONResult;
import com.ttd.pojo.entity.Users;
import com.ttd.pojo.entity.UsersReport;
import com.ttd.pojo.entity.vo.PublisherVideo;
import com.ttd.pojo.entity.vo.UserVO;
import com.ttd.pojo.entity.vo.UsersVO;
import com.ttd.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@Api(value="用户相关业务的接口", tags= {"用户相关业务 的controller"})
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private static final String FILE_SPACE = "E:/wechatDev/workspace/vedio/imooc_videos_dev/images";

    @ApiOperation(value = "上传头像",notes = "用户上传头像接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    @PostMapping("/uploadFace")
    public IMoocJSONResult uploadFile(String userId, @RequestParam("file")MultipartFile[] files) throws IOException {

        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }
        // 保持数据库的相对路径
        String uploadFileDB = "/" + userId + "/face";
        log.info("the DB path is ======={}",uploadFileDB);

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (null != files || 0 < files.length){
                String originalFilename = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(originalFilename)){
                    // 文件最终保存路径
                    String finalPath = FILE_SPACE + uploadFileDB + "/" + originalFilename;
                    // 设置数据库保存路径
                    uploadFileDB += ("/" + originalFilename);

                    File file = new File(finalPath);
                    if (null != file.getParentFile() || !file.getParentFile().isDirectory()){
                        // 创建父文件夹
                        file.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(file);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);

                }
            }else {
                return IMoocJSONResult.errorMsg("上传出错！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != fileOutputStream){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        Users users = new Users();
        users.setId(userId);
        users.setFaceImage(uploadFileDB);
        userService.updateUserInfo(users);
        return IMoocJSONResult.ok(uploadFileDB);
    }

    @ApiOperation(value = "查询用户信息",notes = "查询用户信息接口")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query")
    @PostMapping("/query")
    public IMoocJSONResult query(String userId,String fanId){
        log.info("the userId is {}",userId);
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空");
        }

        Users usersInfo = userService.queryUserInfo(userId);
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(usersInfo,userVO);
        boolean ifFollow = userService.queryIfFollow(userId, fanId);
        log.info("if have follow {}",ifFollow);
        userVO.setFollow(ifFollow);
        return IMoocJSONResult.ok(userVO);
    }


    @PostMapping("/queryPublisher")
    public IMoocJSONResult queryPublisher(String loginUserId,String videoId,String publishUserId){
        log.info("enter to query publisher nad the login user ID is {}",loginUserId);
        if (StringUtils.isBlank(publishUserId)){
            return IMoocJSONResult.errorMsg("");
        }
        // 1. 查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        // 2. 查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return IMoocJSONResult.ok(bean);
    }

    @PostMapping("/beyourfans")
    public IMoocJSONResult beyourfans(String userId,String fanId){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("关注成功...");
    }
    @PostMapping("/dontbeyourfans")
    public IMoocJSONResult dontbeyourfans(String userId,String fanId){
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return IMoocJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);

        return IMoocJSONResult.ok("取消关注成功...");
    }

    @PostMapping("/reportUser")
    public IMoocJSONResult reportUser(@RequestBody UsersReport usersReport){
        // 保存举报信息
        userService.reportUser(usersReport);

        return IMoocJSONResult.errorMsg("举报成功...有你平台变得更美好...");
    }
}

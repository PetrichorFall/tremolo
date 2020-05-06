package com.ttd.api.controller;

import com.ttd.common.enums.VideoStatusEnum;
import com.ttd.common.idWork.utils.PagedResult;
import com.ttd.common.util.FetchVideoCover;
import com.ttd.common.util.IMoocJSONResult;
import com.ttd.common.util.MergeVideoMp3;
import com.ttd.pojo.entity.Bgm;
import com.ttd.pojo.entity.Comments;
import com.ttd.pojo.entity.Videos;
import com.ttd.service.BmgService;
import com.ttd.service.VideoService;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.UUID;

@RestController
@Api(value = "视频相关的业务",tags = {"视频相关业务的接口"})
@RequestMapping("/video")
public class VideosController extends BasicController {

    private static final Logger log = LoggerFactory.getLogger(VideosController.class);

    @Autowired
    private VideoService videoService;

    @Autowired
    private BmgService bmgService;

    @PostMapping("showAll")
    public IMoocJSONResult showAll(@RequestBody Videos videos,Integer isSaveRecord,
                                   Integer page,Integer pageSize){
        log.info("==== start to showAll videos method =====");
        if (null == page){
            page = 1;
        }
        if (null == pageSize){
            pageSize = PAGE_SIZE;
        }

        PagedResult allVideos = videoService.getAllVideos(videos, isSaveRecord, page, pageSize);
        return IMoocJSONResult.ok(allVideos);

    }

    @ApiOperation(value="上传视频", notes="上传视频的接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userId", value="用户id", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="bgmId", value="背景音乐id", required=false,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoSeconds", value="背景音乐播放长度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoWidth", value="视频宽度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="videoHeight", value="视频高度", required=true,
                    dataType="String", paramType="form"),
            @ApiImplicitParam(name="desc", value="视频描述", required=false,
                    dataType="String", paramType="form")
    })
    @PostMapping(value = "/upload", headers="content-type=multipart/form-data")
    public IMoocJSONResult upload(String userId,String bgmId, double videoSeconds,
                                  int videoWidth, int videoHeight,
                                  String desc,
                                  @ApiParam(value="短视频", required=true)
                                          MultipartFile file) throws Exception {
        log.info("enter upload video ");
        if (StringUtils.isBlank(userId)){
            return IMoocJSONResult.errorMsg("用户Id不能为空......");
        }

        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";

        log.info("the uploadPathDB is {} ",uploadPathDB);
        log.info("the coverPathDB is {} ",coverPathDB);

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        String finalVideoPath = "";
        try {
            if (null != file){
                String originalFilename = file.getOriginalFilename();
                String arrayFilenameItem[] =  originalFilename.split("\\.");
                String fileNamePrefix = "";
                for (int i = 0 ; i < arrayFilenameItem.length-1 ; i ++) {
                    fileNamePrefix += arrayFilenameItem[i];
                }
                if (StringUtils.isNotBlank(originalFilename)){

                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + originalFilename;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + originalFilename);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if (null != outFile.getParentFile() && !outFile.getParentFile().isDirectory()){
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }


                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
                    IOUtils.copy(inputStream,fileOutputStream);
                }

            }else {
                return  IMoocJSONResult.errorMsg("上传出错！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (null != fileOutputStream){
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }


        // 判断bgmId是否为空，如果不为空，
        // 那就查询bgm的信息，并且合并视频，生产新的视频

        if (StringUtils.isNotBlank(bgmId)){
            Bgm bgm = bmgService.queryBgmById(bgmId);
            String mp3Path = FILE_SPACE + bgm.getPath();
            MergeVideoMp3 mergeVideoMp3 = new MergeVideoMp3(FFMPEG_EXE);
            String videoInputPath = finalVideoPath;

            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;
            mergeVideoMp3.convert(videoInputPath,mp3Path,videoSeconds,finalVideoPath);
        }
        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalVideoPath=" + finalVideoPath);

        // 对视频进行截图
        FetchVideoCover videoInfo = new FetchVideoCover(FFMPEG_EXE);
        videoInfo.getConvert(finalVideoPath, FILE_SPACE + coverPathDB);

        // 保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float)videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        log.info("the description is {}",desc);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        video.setCreateTime(new Date());

        String videoId = videoService.saveVideo(video);
        return IMoocJSONResult.ok(videoId);
    }

    /**
     * @Description: 我收藏(点赞)过的视频列表
     */
    @PostMapping("/showMyLike")
    public IMoocJSONResult showMyLike(String userId, Integer page, Integer pageSize) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 6;
        }

        PagedResult videosList = videoService.queryMyLikeVideos(userId, page, pageSize);
        log.info("the my like video is {}",videosList);
        return IMoocJSONResult.ok(videosList);
    }

    /**
     * @Description: 我关注的人发的视频
     */
    @PostMapping("/showMyFollow")
    public IMoocJSONResult showMyFollow(String userId, Integer page) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return IMoocJSONResult.ok();
        }

        if (page == null) {
            page = 1;
        }

        int pageSize = 6;

        PagedResult videosList = videoService.queryMyFollowVideos(userId, page, pageSize);

        return IMoocJSONResult.ok(videosList);
    }

    @PostMapping("/hot")
    public IMoocJSONResult getHot(){
        return IMoocJSONResult.ok(videoService.getHotwords());
    }

    @PostMapping("/getVideoComments")
    public IMoocJSONResult getVideoComments(String videoId, Integer page, Integer pageSize){

        if (StringUtils.isBlank(videoId)) {
            return IMoocJSONResult.ok();
        }

        // 分页查询视频列表，时间顺序倒序排序
        if (page == null) {
            page = 1;
        }

        if (pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return IMoocJSONResult.ok(list);

    }

    @PostMapping("/userLike")
    public IMoocJSONResult userLike(String userId,String videoId,String videoCreaterId){
        videoService.userLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/userUnLike")
    public IMoocJSONResult userUnLike(String userId,String videoId,String videoCreaterId){
        videoService.userUnLikeVideo(userId, videoId, videoCreaterId);
        return IMoocJSONResult.ok();
    }

    @PostMapping("/saveComment")
    public IMoocJSONResult saveComment(@RequestBody Comments comment,
                                       String fatherCommentId, String toUserId){
        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return IMoocJSONResult.ok();
    }
}

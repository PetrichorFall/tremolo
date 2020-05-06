package com.ttd.api.controller;

import com.ttd.common.util.IMoocJSONResult;
import com.ttd.service.BmgService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "背景音乐业务接口",tags = {"背景音乐业务Controller"})
@RequestMapping("/bmg")
public class BmgController extends BasicController {

    private static final Logger log = LoggerFactory.getLogger(BmgService.class);

    @Autowired
    private BmgService bmgService;

    @ApiOperation(value="获取背景音乐列表", notes="获取背景音乐列表的接口")
    @PostMapping("/list")
    public IMoocJSONResult bmgList(){
        log.info("enter to query all bmg music");
        return IMoocJSONResult.ok(bmgService.queryBgmList());
    }
}

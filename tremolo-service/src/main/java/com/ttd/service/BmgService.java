package com.ttd.service;

import com.ttd.pojo.entity.Bgm;

import java.util.List;

public interface BmgService {
    /**
     * @Description: 查询背景音乐列表
     */
    public List<Bgm> queryBgmList();

    /**
     * @Description: 根据id查询bgm信息
     */
    public Bgm queryBgmById(String bgmId);
}

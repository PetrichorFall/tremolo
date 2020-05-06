package com.ttd.service.impl;

import com.ttd.common.idWork.Sid;
import com.ttd.pojo.dao.BgmMapper;
import com.ttd.pojo.entity.Bgm;
import com.ttd.service.BmgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BmgServiceImpl implements BmgService {

    private static final Logger log = LoggerFactory.getLogger(BmgServiceImpl.class);

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        log.info("=======start to query Bmg List ==========");
        List<Bgm> bgms = bgmMapper.selectAll();
        return bgms;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}

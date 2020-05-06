package com.ttd.service.impl;

import com.ttd.common.idWork.Sid;
import com.ttd.pojo.dao.UserMapper;
import com.ttd.pojo.dao.UsersFansMapper;
import com.ttd.pojo.dao.UsersLikeVideosMapper;
import com.ttd.pojo.dao.UsersReportMapper;
import com.ttd.pojo.entity.Users;
import com.ttd.pojo.entity.UsersFans;
import com.ttd.pojo.entity.UsersLikeVideos;
import com.ttd.pojo.entity.UsersReport;
import com.ttd.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Autowired
    private Sid sid;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean userIsExist(String username)
    {
        Users users = new Users();
        users.setUsername(username);
        Users userResult = userMapper.selectOne(users);
        return null == userResult ? false : true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        userMapper.insert(user);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserForLogin(String username, String password) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("username",username);
        criteria.andEqualTo("password",password);
        Users users = userMapper.selectOneByExample(userExample);
        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void updateUserInfo(Users user) {
        log.info("the user info is {}",user);
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",user.getId());
        userMapper.updateByExampleSelective(user,example);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Users queryUserInfo(String userId) {
        log.info("the userId is ================={}",userId);
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",userId);
        Users users = userMapper.selectOneByExample(example);

        return users;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public boolean isUserLikeVideo(String userId, String videoId) {

        log.info("enter the get isUserLikeVideo() method");
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(videoId)){
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("videoId",videoId);

        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);
        if (null != usersLikeVideos ||  usersLikeVideos.size() > 0){
            return true;
        }
        return false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {
        String relId = sid.nextShort();

        UsersFans userFan = new UsersFans();
        userFan.setId(relId);
        userFan.setUserId(userId);
        userFan.setFanId(fanId);
        log.info("the userFan is {}",userFan);
        usersFansMapper.insert(userFan);

        userMapper.addFansCount(userId);
        userMapper.addFollersCount(fanId);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);

        userMapper.reduceFansCount(userId);
        userMapper.reduceFollersCount(fanId);
    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public void reportUser(UsersReport userReport) {
        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());

        usersReportMapper.insert(userReport);
    }
}

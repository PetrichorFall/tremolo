package com.ttd.pojo.dao;

import com.ttd.common.util.MyMapper;

import com.ttd.pojo.entity.UserExample;
import com.ttd.pojo.entity.Users;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

public interface UserMapper extends MyMapper<Users> {
    int countByExample(UserExample example);

    int insert(Users record);

    int insertSelective(Users record);

    List<Users> selectByExample(Example example);

    int updateByPrimaryKeySelective(Users record);

    int updateByPrimaryKey(Users record);

    /**
     * @Description: 用户受喜欢数累加
     */
    public void addReceiveLikeCount(String userId);

    /**
     * @Description: 用户受喜欢数累减
     */
    public void reduceReceiveLikeCount(String userId);

    /**
     * @Description: 增加粉丝数
     */
    public void addFansCount(String userId);

    /**
     * @Description: 增加关注数
     */
    public void addFollersCount(String userId);

    /**
     * @Description: 减少粉丝数
     */
    public void reduceFansCount(String userId);

    /**
     * @Description: 减少关注数
     */
    public void reduceFollersCount(String userId);

}
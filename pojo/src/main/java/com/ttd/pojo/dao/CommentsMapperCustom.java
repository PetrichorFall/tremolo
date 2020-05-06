package com.ttd.pojo.dao;


import com.ttd.common.util.MyMapper;
import com.ttd.pojo.entity.Comments;
import com.ttd.pojo.entity.vo.CommentsVO;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}
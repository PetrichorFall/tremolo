package com.ttd.pojo.dao;


import com.ttd.common.util.MyMapper;
import com.ttd.pojo.entity.SearchRecords;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}
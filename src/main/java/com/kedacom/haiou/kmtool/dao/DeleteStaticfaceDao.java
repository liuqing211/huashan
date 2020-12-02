package com.kedacom.haiou.kmtool.dao;

import com.kedacom.haiou.kmtool.entity.DeleteStaticfaceLog;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DeleteStaticfaceDao {

    int saveDeleteStaticfaceLog(DeleteStaticfaceLog deleteStaticfaceLog);
}

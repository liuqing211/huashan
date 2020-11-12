package com.kedacom.haiou.kmtool.dao;

import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.entity.HaiouRepositoryMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HaiouRepositoryDao {

    List<HaiouRepository> getAllBlackListRepo();

    HaiouRepositoryMapping queryRepoMappingByAlgIDAndRepoId(@Param("algorithmId") String algId, @Param("repositoryId") String repoId);
}

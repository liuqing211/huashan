package com.kedacom.haiou.kmtool.dao;

import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import com.kedacom.haiou.kmtool.entity.HaiouRepositoryMapping;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface HaiouRepositoryDao {

    List<HaiouRepository> getAllBlackListRepo();

    String queryRepoMappingByAlgIDAndRepoId(@Param("algorithmId") String algId, @Param("repositoryId") String repoId);

    List<HaiouRepository> getRepoByCreatorId(@Param("creatorName") String creatorName);

    HaiouRepository queryRepoById(@Param("repositoryId") String repoId);

    List<HaiouRepository> getBlackRepoByCreatorName(@Param("creatorName") String creatorName);
}

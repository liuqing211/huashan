package com.kedacom.haiou.kmtool.dao;

import com.kedacom.haiou.kmtool.entity.CalarmMsgInfo;
import com.kedacom.haiou.kmtool.entity.HaiouRepository;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CalarmMsgInfoDao {

    List<CalarmMsgInfo> queryFeatureMsgInfo();

    boolean updateTriggerTime(@Param("dispositionId") String dispositionID, @Param("notificationId") String notificationID,
                              @Param("triggerTime") String triggerTime, @Param("triggerTimeStr") String triggerTimeStr);
}

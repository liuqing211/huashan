package com.kedacom.haiou.kmtool.service;

import com.kedacom.haiou.kmtool.dto.HaiouBasicNewVO;
import com.kedacom.haiou.kmtool.dto.PersonBaseInfo;
import com.kedacom.haiou.kmtool.utils.ESClientManager;
import com.kedacom.haiou.kmtool.utils.IdFactory;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/12/18.
 */
@Component
@Slf4j
public class FaceSearchService {

    @Autowired
    private ESClientManager esClientManager;


    public List<PersonBaseInfo> searchEsFaceByTabID(String tabId){
        log.info("开始查询老平台 {} 人员库中的人员信息", tabId);
        List<PersonBaseInfo> personBaseInfoList = new ArrayList<>();

        SearchResponse searchResponse = esClientManager.getClient().prepareSearch("a_haiou_basic_new")
                .setTypes("a_haiou_basic_new")
                .setQuery(QueryBuilders.nestedQuery("pictures",
                        QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
                                FilterBuilders.termFilter("pictures.repository_id", tabId)
                        )))
                .setScroll(new TimeValue(10, TimeUnit.MINUTES))
                .setSize(1000)
                .execute().actionGet();

        while (true){
            SearchHits hits = searchResponse.getHits();
            if (hits.getHits().length > 0) {
                log.info("本次查询 {} 人员库共查询出 {} 条", tabId, hits.getHits().length);
                for (SearchHit searchHit : hits) {
                    PersonBaseInfo personBaseInfo = new PersonBaseInfo();

                    String guid = (String) searchHit.getSource().get("guid");
                    if (StringUtils.isEmpty(guid)) {
                        log.info("{}-{}该人员的guid异常，过滤", searchHit.getSource().get("person_id"), searchHit.getSource().get("name"));
                        continue;
                    }

                    personBaseInfo.setName((String) searchHit.getSource().get("name"));
                    personBaseInfo.setIdNumber((String) searchHit.getSource().get("person_id"));

                    List<Map> picturesList = (List<Map>) searchHit.getSource().get("pictures");
                    if (CollectionUtils.isEmpty(picturesList)) {
                        log.info("{}-{}该人员不属于任何库，过滤", searchHit.getSource().get("person_id"), searchHit.getSource().get("name"));
                        continue;
                    } else {
                        for (int i = 0; i < picturesList.size(); i++) {
                            Map<Object, Object> picture = picturesList.get(i);
                            List<String> repoList = (ArrayList<String>) picture.get("repository_id");
                            repoList.forEach(repo -> {
                                if (repo.equals(tabId)) {
                                    personBaseInfo.setPicUrl((String) picture.get("url"));
                                }
                            });
                        }
                    }

                    personBaseInfo.setRelativeID(IdFactory.RelativeIDType());
                    personBaseInfo.setImageID(IdFactory.ImageIDType());

                    personBaseInfoList.add(personBaseInfo);

                }

            }

            searchResponse = esClientManager.getClient().prepareSearchScroll(searchResponse.getScrollId())
                    .setScroll(new TimeValue(10, TimeUnit.MINUTES))
                    .execute().actionGet();

            if (searchResponse.getHits().getHits().length == 0) {
                break;
            }
        }

        return personBaseInfoList;
    }
}

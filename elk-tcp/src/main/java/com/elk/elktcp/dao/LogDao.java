package com.elk.elktcp.dao;

import com.elk.elktcp.entity.Log;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface LogDao extends ElasticsearchRepository<Log, Long> {

    /**
     * 设置高亮，HighlightParameters自定义高亮参数
     * @param methodName
     * @return
     */
    @Highlight(fields = {
            @HighlightField(name = "methodName", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<Log>> findByMethodNameLike(String methodName);


}

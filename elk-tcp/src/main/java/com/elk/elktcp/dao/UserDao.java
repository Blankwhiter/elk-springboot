package com.elk.elktcp.dao;

import com.elk.elktcp.entity.User;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface UserDao extends ElasticsearchRepository<User, Long> {
    @Highlight(fields = {
            @HighlightField(name = "userName", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<User>> findByUserNameLike(String userName);


    @Highlight(fields = {
            @HighlightField(name = "desc", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<User>> findByDescLike(String desc);


}

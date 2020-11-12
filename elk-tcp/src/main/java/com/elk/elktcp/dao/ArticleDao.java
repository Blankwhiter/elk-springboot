package com.elk.elktcp.dao;

import com.elk.elktcp.entity.Article;
import org.springframework.data.elasticsearch.annotations.Highlight;
import org.springframework.data.elasticsearch.annotations.HighlightField;
import org.springframework.data.elasticsearch.annotations.HighlightParameters;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ArticleDao extends ElasticsearchRepository<Article, Long> {

    @Highlight(fields = {
            @HighlightField(name = "name", parameters = @HighlightParameters(
                    preTags = "<strong>",
                    postTags = "</strong>",
                    fragmentSize = 500,
                    numberOfFragments = 3)),
            @HighlightField(name = "text", parameters = @HighlightParameters(
                    preTags = "<span>",
                    postTags = "</span>",
                    fragmentSize = 500,
                    numberOfFragments = 3))

    })
    List<SearchHit<Article>> findAllByNameLikeOrTextLike(String name, String text);


}

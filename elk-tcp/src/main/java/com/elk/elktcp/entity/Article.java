package com.elk.elktcp.entity;

import com.elk.elktcp.annotation.EnableEsAuditing;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * 使用中文分词 和 拼音
 */
@Document(indexName = "my-test-article")
@Mapping(mappingPath = "/json/article-mapping.json")
@Setting(settingPath = "/json/article-setting.json")
@EnableEsAuditing
public class Article extends EsBaseEntity {

    private String text;
    //    @Field(type = FieldType.Text, searchAnalyzer = "ik_en_analyzer", analyzer = "ik_en_analyzer")
    private String name;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

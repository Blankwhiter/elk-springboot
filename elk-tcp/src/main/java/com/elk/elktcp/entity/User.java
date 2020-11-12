package com.elk.elktcp.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * 标记文档
 */
@Document(indexName = "test-plugin", type = "test")
public class User implements Serializable {

    /**
     * 标记id 必需要有
     */
    @Id
    private Long id;
    /**
     * 设置分词
     */
    @Field(name = "user_name", type = FieldType.Text, analyzer = "ik_smart", searchAnalyzer = "ik_max_word")
    private String userName;

    /**
     * 设置分词
     */
    @Field(name = "desc", type = FieldType.Text, analyzer = "pinyin", searchAnalyzer = "pinyin")
    private String desc;

    /**
     * 自动检测类型
     */
    @Field(type = FieldType.Auto)
    private Integer age;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}

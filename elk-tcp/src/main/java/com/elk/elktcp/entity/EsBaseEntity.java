package com.elk.elktcp.entity;

import com.elk.elktcp.utils.SnowflakeIdWorker;
import org.springframework.data.annotation.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 实体基础类
 */
public class EsBaseEntity {
    public static final String dateTimeFormat = "yyyy/MM/dd||yyyy-MM-dd" +
            "||yyyy-MM-dd HH:mm:ss||yyyy/MM/dd HH:mm:ss" +
            "||yyyy-MM-dd HH:mm:ss.SSS||yyyy/MM/dd HH:mm:ss.SSS" +
            "||yyyy-MM-dd'T'HH:mm:ss.SSS";
    /**
     * 创建时间.
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = dateTimeFormat)
    @CreatedDate
    protected String createTime;
    /**
     * 创建人.
     */
    @Field(type = FieldType.Keyword)
    @CreatedBy
    protected String creator;
    /**
     * 更新时间.
     */
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = dateTimeFormat)
    @LastModifiedDate
    protected String updateTime;
    /**
     * 更新人.
     */
    @Field(type = FieldType.Keyword)
    @LastModifiedBy
    protected String updateUser;
    /**
     * 主键.
     */
    @Id
    private String id = String.valueOf(SnowflakeIdWorker.getInstance().nextId());

    public static String getDateTimeFormat() {
        return dateTimeFormat;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}

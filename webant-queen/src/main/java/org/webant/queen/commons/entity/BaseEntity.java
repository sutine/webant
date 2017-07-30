package org.webant.queen.commons.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.webant.queen.utils.DateFormatUtils;

import java.util.Date;

public class BaseEntity {

    @Field(type = FieldType.Integer)
    public Integer dataVersion = 1;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT)
    public Date dataCreateTime = new Date();

    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT)
    public Date dataUpdateTime = new Date();

    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT, timezone = "GMT+8")
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = DateFormatUtils.DATE_TIME_MILLI_FORMAT)
    public Date dataDeleteTime;

    public Integer getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(Integer dataVersion) {
        this.dataVersion = dataVersion;
    }

    public Date getDataCreateTime() {
        return dataCreateTime;
    }

    public void setDataCreateTime(Date dataCreateTime) {
        this.dataCreateTime = dataCreateTime;
    }

    public Date getDataUpdateTime() {
        return dataUpdateTime;
    }

    public void setDataUpdateTime(Date dataUpdateTime) {
        this.dataUpdateTime = dataUpdateTime;
    }

    public Date getDataDeleteTime() {
        return dataDeleteTime;
    }

    public void setDataDeleteTime(Date dataDeleteTime) {
        this.dataDeleteTime = dataDeleteTime;
    }
}

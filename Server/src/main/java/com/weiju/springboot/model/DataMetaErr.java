package com.weiju.springboot.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.json.JSONObject;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataMetaErr extends JSONObject implements Serializable{
    private Object data;
    private Object meta;
    private Object error;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Object getMeta() {
        return meta;
    }

    public void setMeta(Object meta) {
        this.meta = meta;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }



}

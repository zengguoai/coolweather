package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

/**
 * Created by weiguanghua on 18-3-19.
 */

public class Province extends DataSupport {//准备创建3张表，对应实体类，省市县的数据信息
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }
}

package com.buxuesong.account.model;

import java.math.BigDecimal;

public class SaveStockRequest {
    private String code;
    private BigDecimal costPrise;
    private int bonds;
    private String app;

    private boolean hide;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getCostPrise() {
        return costPrise;
    }

    public void setCostPrise(BigDecimal costPrise) {
        this.costPrise = costPrise;
    }

    public int getBonds() {
        return bonds;
    }

    public void setBonds(int bonds) {
        this.bonds = bonds;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    @Override
    public String toString() {
        return "SaveStockRequest{" +
            "code='" + code + '\'' +
            ", costPrise=" + costPrise +
            ", bonds=" + bonds +
            ", app='" + app + '\'' +
            ", hide=" + hide +
            '}';
    }
}

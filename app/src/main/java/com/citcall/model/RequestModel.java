package com.citcall.model;

/**
 * Created by Agustya on 10/7/16.
 */

public class RequestModel extends BaseModel {
    private String trx_id;
    private String msisdn;
    private String via;
    private String token;
    private String dial_code;
    private String dial_status;
    private String call_status;
    private String result;

    public String getTrx_id() {
        return trx_id;
    }

    public void setTrx_id(String trx_id) {
        this.trx_id = trx_id;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getVia() {
        return via;
    }

    public void setVia(String via) {
        this.via = via;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDial_code() {
        return dial_code;
    }

    public void setDial_code(String dial_code) {
        this.dial_code = dial_code;
    }

    public String getDial_status() {
        return dial_status;
    }

    public void setDial_status(String dial_status) {
        this.dial_status = dial_status;
    }

    public String getCall_status() {
        return call_status;
    }

    public void setCall_status(String call_status) {
        this.call_status = call_status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}

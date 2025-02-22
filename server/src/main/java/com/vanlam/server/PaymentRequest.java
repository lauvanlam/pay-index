package com.vanlam.server;

public class PaymentRequest {
    private String out_trade_no;
    private String name;
    private String money;
    private String type;
    private String interfaceType;
    private String timestamp;
    private String sign_type;
    private String method;
    private String pid;
    private String notify_url;
    private String return_url;
    private String clientip;
    private String sign;

    // Getters and Setters
    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInterfaceType() {
        return interfaceType;
    }

    public void setInterfaceType(String interfaceType) {
        this.interfaceType = interfaceType;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getNotify_url() {
        return notify_url;
    }

    public void setNotify_url(String notify_url) {
        this.notify_url = notify_url;
    }

    public String getReturn_url() {
        return return_url;
    }

    public void setReturn_url(String return_url) {
        this.return_url = return_url;
    }

    public String getClientip() {
        return clientip;
    }

    public void setClientip(String clientip) {
        this.clientip = clientip;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public PaymentRequest() {
    }

    public PaymentRequest(String out_trade_no, String name, String money, String type, String interfaceType, String timestamp, String sign_type, String method, String pid, String notify_url, String return_url, String clientip, String sign) {
        this.out_trade_no = out_trade_no;
        this.name = name;
        this.money = money;
        this.type = type;
        this.interfaceType = interfaceType;
        this.timestamp = timestamp;
        this.sign_type = sign_type;
        this.method = method;
        this.pid = pid;
        this.notify_url = notify_url;
        this.return_url = return_url;
        this.clientip = clientip;
        this.sign = sign;
    }
}
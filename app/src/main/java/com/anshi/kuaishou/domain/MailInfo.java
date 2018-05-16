package com.anshi.kuaishou.domain;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/16 下午 2:02
 * Desc   : description
 */
public class MailInfo {

    /**
     * parcelId : 259092
     * mailNo : W3333333335
     * frameCode : A
     * num : 720011
     * recipientMobile : 15388888888
     * recipientName : 张三
     * repeatType : 0
     * parcelStatus : 2
     * weixin_status : 2
     * sms_status : 1
     * companyName : 中通速递
     * arriveTimestamp : 1526441996302
     * takeTimestamp : null
     * takeCode : 108917
     */

    private int parcelId;
    private String mailNo;
    private String frameCode;
    private int num;
    private String recipientMobile;
    private String recipientName;
    private int repeatType;
    private int parcelStatus;
    private int weixin_status;
    private int sms_status;
    private String companyName;
    private long arriveTimestamp;
    private long takeTimestamp;
    private int takeCode;

    public int getParcelId() {
        return parcelId;
    }

    public void setParcelId(int parcelId) {
        this.parcelId = parcelId;
    }

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public String getFrameCode() {
        return frameCode;
    }

    public void setFrameCode(String frameCode) {
        this.frameCode = frameCode;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getRecipientMobile() {
        return recipientMobile;
    }

    public void setRecipientMobile(String recipientMobile) {
        this.recipientMobile = recipientMobile;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public int getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(int repeatType) {
        this.repeatType = repeatType;
    }

    public int getParcelStatus() {
        return parcelStatus;
    }

    public void setParcelStatus(int parcelStatus) {
        this.parcelStatus = parcelStatus;
    }

    public int getWeixin_status() {
        return weixin_status;
    }

    public void setWeixin_status(int weixin_status) {
        this.weixin_status = weixin_status;
    }

    public int getSms_status() {
        return sms_status;
    }

    public void setSms_status(int sms_status) {
        this.sms_status = sms_status;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public long getArriveTimestamp() {
        return arriveTimestamp;
    }

    public void setArriveTimestamp(long arriveTimestamp) {
        this.arriveTimestamp = arriveTimestamp;
    }

    public long getTakeTimestamp() {
        return takeTimestamp;
    }

    public void setTakeTimestamp(long takeTimestamp) {
        this.takeTimestamp = takeTimestamp;
    }

    public int getTakeCode() {
        return takeCode;
    }

    public void setTakeCode(int takeCode) {
        this.takeCode = takeCode;
    }
}

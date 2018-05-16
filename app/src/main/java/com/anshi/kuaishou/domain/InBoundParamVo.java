package com.anshi.kuaishou.domain;

import java.util.List;

/**
 * Created by Android Studio.
 * Author : zhangzhongqiang
 * Email  : betterzhang.dev@gmail.com
 * Time   : 2018/05/16 下午 2:45
 * Desc   : description
 */
public class InBoundParamVo {


    /**
     * stationId : 721
     * stationName : 熊猫快收服务站
     * companyId : 125
     * version : 1.1.1
     * inputway : 1
     * companyName : 中通速递
     * source : 2
     * packetInfo : [{"mailNo":"W3333333335","frameCode":"A","num":"720011","recipientMobile":"15388888888","recipientName":"张三"},{"mailNo":"W3333333336","frameCode":"A","num":"720012","recipientMobile":"15388888888","recipientName":"张三"}]
     */

    private int stationId;
    private String stationName;
    private int companyId;
    private String version;
    private int inputway;
    private String companyName;
    private int source;
    private List<PacketInfoBean> packetInfo;

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getInputway() {
        return inputway;
    }

    public void setInputway(int inputway) {
        this.inputway = inputway;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public List<PacketInfoBean> getPacketInfo() {
        return packetInfo;
    }

    public void setPacketInfo(List<PacketInfoBean> packetInfo) {
        this.packetInfo = packetInfo;
    }

    public static class PacketInfoBean {
        /**
         * mailNo : W3333333335
         * frameCode : A
         * num : 720011
         * recipientMobile : 15388888888
         * recipientName : 张三
         */

        private String mailNo;
        private String frameCode;
        private String num;
        private String recipientMobile;
        private String recipientName;

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

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
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
    }
}

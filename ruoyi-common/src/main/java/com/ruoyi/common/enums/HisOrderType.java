package com.ruoyi.common.enums;

/**
 * His业务操作类型
 * 
 * @author ruoyi
 */
public enum HisOrderType
{
    DOREG("doreg", "预约挂号" ),
    DOPAY("dopay", "缴费支付"),
    INPATIENTPAYMENT("inpatientpayment", "住院押金补缴"),
    LEAVEHOSPAY("leavehospay", "离院结算"),;
    private String key;
    private String desc;

    HisOrderType(String key, String desc) {
        this.key=key;
        this.desc=desc;
    }
}

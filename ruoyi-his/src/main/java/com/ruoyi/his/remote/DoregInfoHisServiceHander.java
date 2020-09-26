package com.ruoyi.his.remote;

import com.alibaba.fastjson.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.HisException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.his.constant.HisBusinessTypeEnum;
import com.ruoyi.his.constant.PayStatusEnum;
import com.ruoyi.his.domain.DopayInfo;
import com.ruoyi.his.domain.DoregInfo;
import com.ruoyi.his.remote.request.DoRegIn;
import com.ruoyi.his.remote.response.BaseResponse;
import com.ruoyi.his.remote.response.DoRegOut;
import com.ruoyi.his.service.IDoregInfoService;
import com.ruoyi.his.service.ISmsService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 预约挂号Service业务层处理
 * 
 * @author whl
 * @date 2020-08-08
 */
@Service
public class DoregInfoHisServiceHander extends AbstractHisServiceHandler<DoRegIn,DoregInfo, DoRegOut> {
    @Autowired
    private IDoregInfoService doregInfoService;
    @Autowired
    private ISmsService iSmsService;

    @Override
    public HisBusinessTypeEnum getBusinessType() {
        return HisBusinessTypeEnum.DOREG;
    }


    @Override
    DoregInfo getOrderDetail(String outTradeNo) {
        DoregInfo doregInfo = doregInfoService.getDetailByOutTradeNo(outTradeNo);
        doregInfo.setAmount(doregInfo.getPayAmount());
        return doregInfo;
    }

    @Override
    int updateOrder(DoregInfo doregInfo) {
        return doregInfoService.updateDoregInfo(doregInfo);
    }

    @Override
    public DoRegIn buildRequestData(String outTradeNo) {
        DoregInfo doregInfoNew = getOrderDetail(outTradeNo);
        DoRegIn doRegInInfo = new DoRegIn();
        //医生编号
        doRegInInfo.setOrgandoctorId(doregInfoNew.getOrgandoctorId());
        //科室编号
        doRegInInfo.setDepartmentorganId(doregInfoNew.getDepartmentorganId());
        //患者身份证号
        doRegInInfo.setCardNo(doregInfoNew.getCardNo());
        //患者编号
        doRegInInfo.setPatientNo(doregInfoNew.getPatientNo());
        //取号时间（号源日期）
        doRegInInfo.setSourceDate(DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD,doregInfoNew.getSourceDate()));
        //时间段标识 0表示无时间段
        doRegInInfo.setTimestypeNo(doregInfoNew.getTimestypeNo());
        //1,上午 2，中午3 下午 4，晚上 5，凌晨
        doRegInInfo.setSourceTimeType(doregInfoNew.getSourceTimeType());
        //1,银联，2支付宝 3，现场支付 4、医保账户，5、微信，6、云医院微信，7、云医院支付宝，8、诊疗卡
        doRegInInfo.setPayType(doregInfoNew.getPayType());
        //支付流水号
        doRegInInfo.setPayNo(doregInfoNew.getPayNo());
        //支付金额
        doRegInInfo.setPayAmount(String.valueOf(doregInfoNew.getPayAmount()));

        return doRegInInfo;
    }

    @Override
    protected DoRegOut transResult(String result) {
        return JSON.parseObject(result, DoRegOut.class);
    }


    @Override
    public BaseResponse afterInvokeCallSumbit(String outTradeNo, DoRegOut regOut) {
        DoregInfo doregInfoTemp = getOrderDetail(outTradeNo);
        DoregInfo doregInfo = new DoregInfo();
        doregInfo.setId(doregInfoTemp.getId());
        doregInfo.setResultMsg(regOut.getResultMsg());
        doregInfo.setUpdateTime(DateUtils.getNowDate());
        doregInfo.setSuccessfulPayment(regOut.isOk()?PayStatusEnum.ORDER_SUCCESS.getCode():PayStatusEnum.ORDER_FAIL.getCode());
        if(regOut.isOk()){
            doregInfo.setMedicalCode(regOut.getMedicalCode());
            doregInfo.setSourceMark(regOut.getSourceMark());
            doregInfo.setConsultationFee(regOut.getConsultationFee());
            sendSmsMsg(doregInfoTemp.getSynUserName());

        }
        doregInfoService.updateDoregInfo(doregInfo);
        return regOut.isOk()?BaseResponse.success():BaseResponse.fail("操作失败，支付金额稍后将会原路返回");
    }


    /***
     * 发送短信
     * @param phone
     */
    private void sendSmsMsg(String phone){
        //发送短信
        try {
            String msg ="【广东省农垦中心医院】您已预约成功，就诊当天请务必携带身份证或者诊疗卡在预约时间内到自助机取号就诊，不取号不能正常就诊。";
            AjaxResult ajaxResult =iSmsService.sendSmsMessage(phone,msg);
        }catch (Exception ex){
            logger.error("DoregInfoHisServiceHander.afterInvokeCallSumbit发送预约短信时，phone={},发送失败={}",
                    phone,ex.getMessage());
        }
    }

    /***
     * 下单失败+退款失败
     * @return
     */
    @Override
    protected List<DoregInfo> getRefundOrderList() {
        List<DoregInfo> refundList = new ArrayList<>();
        DoregInfo query = new DoregInfo();
        query.setSuccessfulPayment(PayStatusEnum.ORDER_FAIL.getCode());
        List<DoregInfo> submitFail =doregInfoService.selectDoregInfoList(query);
        if(CollectionUtils.isNotEmpty(submitFail)){
            refundList.addAll(submitFail);
        }
        query = new DoregInfo();
        query.setSuccessfulPayment(PayStatusEnum.REFUND_TODO.getCode());
        List<DoregInfo> refundTodo =doregInfoService.selectDoregInfoList(query);
        if(CollectionUtils.isNotEmpty(refundTodo)){
            refundList.addAll(refundTodo);
        }
        return refundList;
    }
}

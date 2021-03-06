package com.ruoyi.his.service;

import java.util.List;
import com.ruoyi.his.domain.DopayInfo;

/**
 * 门诊支付Service接口
 * 
 * @author whl
 * @date 2020-08-29
 */
public interface IDopayInfoService 
{
    /**
     * 查询门诊支付
     * 
     * @param id 门诊支付ID
     * @return 门诊支付
     */
    public DopayInfo selectDopayInfoById(Long id);

    /**
     * 查询门诊支付列表
     * 
     * @param dopayInfo 门诊支付
     * @return 门诊支付集合
     */
    public List<DopayInfo> selectDopayInfoList(DopayInfo dopayInfo);

    /**
     * 新增门诊支付
     * 
     * @param dopayInfo 门诊支付
     * @return 结果
     */
    public int insertDopayInfo(DopayInfo dopayInfo);

    /**
     * 修改门诊支付
     * 
     * @param dopayInfo 门诊支付
     * @return 结果
     */
    public int updateDopayInfo(DopayInfo dopayInfo);

    /**
     * 批量删除门诊支付
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteDopayInfoByIds(String ids);

    /**
     * 删除门诊支付信息
     * 
     * @param id 门诊支付ID
     * @return 结果
     */
    public int deleteDopayInfoById(Long id);
}

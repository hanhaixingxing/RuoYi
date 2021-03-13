package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.core.domain.entity.SysDevice;
import com.ruoyi.common.core.domain.entity.SysRole;

/**
 * 设备管理 服务层
 *
 * @author ruoyi
 */
public interface ISysDeviceService
{
    /**
     * 查询设备管理数据
     *
     * @param device 设备信息
     * @return 设备信息集合
     */
    public List<SysDevice> selectDeviceList(SysDevice device);

    /**
     * 查询设备管理树
     *
     * @param device 设备信息
     * @return 所有设备信息
     */
    public List<Ztree> selectDeviceTree(SysDevice device);

    /**
     * 查询设备管理树（排除下级）
     *
     * @param device 设备信息
     * @return 所有设备信息
     */
    public List<Ztree> selectDeviceTreeExcludeChild(SysDevice device);

    /**
     * 根据角色ID查询菜单
     *
     * @param role 角色对象
     * @return 菜单列表
     */
    public List<Ztree> roleDeviceTreeData(SysRole role);

    /**
     * 查询设备人数
     *
     * @param parentId 父设备ID
     * @return 结果
     */
    public int selectDeviceCount(Long parentId);

    /**
     * 查询设备是否存在用户
     *
     * @param deviceId 设备ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeviceExistUser(Long deviceId);

    /**
     * 删除设备管理信息
     *
     * @param deviceId 设备ID
     * @return 结果
     */
    public int deleteDeviceById(Long deviceId);

    /**
     * 新增保存设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    public int insertDevice(SysDevice device);

    /**
     * 修改保存设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    public int updateDevice(SysDevice device);

    /**
     * 根据设备ID查询信息
     *
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public SysDevice selectDeviceById(Long deviceId);

    /**
     * 根据ID查询所有子设备（正常状态）
     *
     * @param deviceId 设备ID
     * @return 子设备数
     */
    public int selectNormalChildrenDeviceById(Long deviceId);

    /**
     * 校验设备名称是否唯一
     *
     * @param device 设备信息
     * @return 结果
     */
    public String checkDeviceNameUnique(SysDevice device);
}

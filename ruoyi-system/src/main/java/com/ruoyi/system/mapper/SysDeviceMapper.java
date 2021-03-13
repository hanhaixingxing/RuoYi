package com.ruoyi.system.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.ruoyi.common.core.domain.entity.SysDevice;

/**
 * 设备管理 数据层
 *
 * @author ruoyi
 */
public interface SysDeviceMapper
{
    /**
     * 查询设备人数
     *
     * @param device 设备信息
     * @return 结果
     */
    public int selectDeviceCount(SysDevice device);

    /**
     * 查询设备是否存在用户
     *
     * @param deviceId 设备ID
     * @return 结果
     */
    public int checkDeviceExistUser(Long deviceId);

    /**
     * 查询设备管理数据
     *
     * @param device 设备信息
     * @return 设备信息集合
     */
    public List<SysDevice> selectDeviceList(SysDevice device);

    /**
     * 删除设备管理信息
     *
     * @param deviceId 设备ID
     * @return 结果
     */
    public int deleteDeviceById(Long deviceId);

    /**
     * 新增设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    public int insertDevice(SysDevice device);

    /**
     * 修改设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    public int updateDevice(SysDevice device);

    /**
     * 修改子元素关系
     *
     * @param devices 子元素
     * @return 结果
     */
    public int updateDeviceChildren(@Param("devices") List<SysDevice> devices);

    /**
     * 根据设备ID查询信息
     *
     * @param deviceId 设备ID
     * @return 设备信息
     */
    public SysDevice selectDeviceById(Long deviceId);

    /**
     * 校验设备名称是否唯一
     *
     * @param deviceName 设备名称
     * @param parentId 父设备ID
     * @return 结果
     */
    public SysDevice checkDeviceNameUnique(@Param("deviceName") String deviceName, @Param("parentId") Long parentId);

    /**
     * 根据角色ID查询设备
     *
     * @param roleId 角色ID
     * @return 设备列表
     */
    public List<String> selectRoleDeviceTree(Long roleId);

    /**
     * 修改所在设备的父级设备状态
     *
     * @param device 设备
     */
    public void updateDeviceStatus(SysDevice device);

    /**
     * 根据ID查询所有子设备
     *
     * @param deviceId 设备ID
     * @return 设备列表
     */
    public List<SysDevice> selectChildrenDeviceById(Long deviceId);

    /**
     * 根据ID查询所有子设备（正常状态）
     *
     * @param deviceId 设备ID
     * @return 子设备数
     */
    public int selectNormalChildrenDeviceById(Long deviceId);
}

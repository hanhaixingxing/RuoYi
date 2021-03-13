package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.core.domain.entity.SysDevice;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysDeviceMapper;
import com.ruoyi.system.service.ISysDeviceService;

/**
 * 设备管理 服务实现
 *
 * @author ruoyi
 */
@Service
public class SysDeviceServiceImpl implements ISysDeviceService
{
    @Autowired
    private SysDeviceMapper deviceMapper;

    /**
     * 查询设备管理数据
     *
     * @param device 设备信息
     * @return 设备信息集合
     */
    @Override
//    @DataScope(deviceAlias = "d")
    public List<SysDevice> selectDeviceList(SysDevice device)
    {
        return deviceMapper.selectDeviceList(device);
    }

    /**
     * 查询设备管理树
     *
     * @param device 设备信息
     * @return 所有设备信息
     */
    @Override
//    @DataScope(deviceAlias = "d")
    public List<Ztree> selectDeviceTree(SysDevice device)
    {
        List<SysDevice> deviceList = deviceMapper.selectDeviceList(device);
        List<Ztree> ztrees = initZtree(deviceList);
        return ztrees;
    }

    /**
     * 查询设备管理树（排除下级）
     *
     * @param device  设备ID
     * @return 所有设备信息
     */
    @Override
//    @DataScope(deviceAlias = "d")
    public List<Ztree> selectDeviceTreeExcludeChild(SysDevice device)
    {
        Long deviceId = device.getDeviceId();
        List<SysDevice> deviceList = deviceMapper.selectDeviceList(device);
        Iterator<SysDevice> it = deviceList.iterator();
        while (it.hasNext())
        {
            SysDevice d = (SysDevice) it.next();
            if (d.getDeviceId().intValue() == deviceId
                    || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deviceId + ""))
            {
                it.remove();
            }
        }
        List<Ztree> ztrees = initZtree(deviceList);
        return ztrees;
    }

    /**
     * 根据角色ID查询设备（数据权限）
     *
     * @param role 角色对象
     * @return 设备列表（数据权限）
     */
    @Override
    public List<Ztree> roleDeviceTreeData(SysRole role)
    {
        Long roleId = role.getRoleId();
        List<Ztree> ztrees = new ArrayList<Ztree>();
        List<SysDevice> deviceList = selectDeviceList(new SysDevice());
        if (StringUtils.isNotNull(roleId))
        {
            List<String> roleDeviceList = deviceMapper.selectRoleDeviceTree(roleId);
            ztrees = initZtree(deviceList, roleDeviceList);
        }
        else
        {
            ztrees = initZtree(deviceList);
        }
        return ztrees;
    }

    /**
     * 对象转设备树
     *
     * @param deviceList 设备列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDevice> deviceList)
    {
        return initZtree(deviceList, null);
    }

    /**
     * 对象转设备树
     *
     * @param deviceList 设备列表
     * @param roleDeviceList 角色已存在菜单列表
     * @return 树结构列表
     */
    public List<Ztree> initZtree(List<SysDevice> deviceList, List<String> roleDeviceList)
    {

        List<Ztree> ztrees = new ArrayList<Ztree>();
        boolean isCheck = StringUtils.isNotNull(roleDeviceList);
        for (SysDevice device : deviceList)
        {
            if (UserConstants.DEPT_NORMAL.equals(device.getStatus()))
            {
                Ztree ztree = new Ztree();
                ztree.setId(device.getDeviceId());
                ztree.setpId(device.getParentId());
                ztree.setName(device.getDeviceName());
                ztree.setTitle(device.getDeviceName());
                if (isCheck)
                {
                    ztree.setChecked(roleDeviceList.contains(device.getDeviceId() + device.getDeviceName()));
                }
                ztrees.add(ztree);
            }
        }
        return ztrees;
    }

    /**
     * 查询设备人数
     *
     * @param parentId 设备ID
     * @return 结果
     */
    @Override
    public int selectDeviceCount(Long parentId)
    {
        SysDevice device = new SysDevice();
        device.setParentId(parentId);
        return deviceMapper.selectDeviceCount(device);
    }

    /**
     * 查询设备是否存在用户
     *
     * @param deviceId 设备ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeviceExistUser(Long deviceId)
    {
        int result = deviceMapper.checkDeviceExistUser(deviceId);
        return result > 0 ? true : false;
    }

    /**
     * 删除设备管理信息
     *
     * @param deviceId 设备ID
     * @return 结果
     */
    @Override
    public int deleteDeviceById(Long deviceId)
    {
        return deviceMapper.deleteDeviceById(deviceId);
    }

    /**
     * 新增保存设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    @Override
    public int insertDevice(SysDevice device)
    {
        SysDevice info = deviceMapper.selectDeviceById(device.getParentId());
        // 如果父节点不为"正常"状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus()))
        {
            throw new BusinessException("设备停用，不允许新增");
        }
        device.setAncestors(info.getAncestors() + "," + device.getParentId());
        return deviceMapper.insertDevice(device);
    }

    /**
     * 修改保存设备信息
     *
     * @param device 设备信息
     * @return 结果
     */
    @Override
    @Transactional
    public int updateDevice(SysDevice device)
    {
        SysDevice newParentDevice = deviceMapper.selectDeviceById(device.getParentId());
        SysDevice oldDevice = selectDeviceById(device.getDeviceId());
        if (StringUtils.isNotNull(newParentDevice) && StringUtils.isNotNull(oldDevice))
        {
            String newAncestors = newParentDevice.getAncestors() + "," + newParentDevice.getDeviceId();
            String oldAncestors = oldDevice.getAncestors();
            device.setAncestors(newAncestors);
            updateDeviceChildren(device.getDeviceId(), newAncestors, oldAncestors);
        }
        int result = deviceMapper.updateDevice(device);
        if (UserConstants.DEPT_NORMAL.equals(device.getStatus()))
        {
            // 如果该设备是启用状态，则启用该设备的所有上级设备
            updateParentDeviceStatus(device);
        }
        return result;
    }

    /**
     * 修改该设备的父级设备状态
     *
     * @param device 当前设备
     */
    private void updateParentDeviceStatus(SysDevice device)
    {
        String updateBy = device.getUpdateBy();
        device = deviceMapper.selectDeviceById(device.getDeviceId());
        device.setUpdateBy(updateBy);
        deviceMapper.updateDeviceStatus(device);
    }

    /**
     * 修改子元素关系
     *
     * @param deviceId 被修改的设备ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeviceChildren(Long deviceId, String newAncestors, String oldAncestors)
    {
        List<SysDevice> children = deviceMapper.selectChildrenDeviceById(deviceId);
        for (SysDevice child : children)
        {
            child.setAncestors(child.getAncestors().replace(oldAncestors, newAncestors));
        }
        if (children.size() > 0)
        {
            deviceMapper.updateDeviceChildren(children);
        }
    }

    /**
     * 根据设备ID查询信息
     *
     * @param deviceId 设备ID
     * @return 设备信息
     */
    @Override
    public SysDevice selectDeviceById(Long deviceId)
    {
        return deviceMapper.selectDeviceById(deviceId);
    }

    /**
     * 根据ID查询所有子设备（正常状态）
     *
     * @param deviceId 设备ID
     * @return 子设备数
     */
    @Override
    public int selectNormalChildrenDeviceById(Long deviceId)
    {
        return deviceMapper.selectNormalChildrenDeviceById(deviceId);
    }

    /**
     * 校验设备名称是否唯一
     *
     * @param device 设备信息
     * @return 结果
     */
    @Override
    public String checkDeviceNameUnique(SysDevice device)
    {
        Long deviceId = StringUtils.isNull(device.getDeviceId()) ? -1L : device.getDeviceId();
        SysDevice info = deviceMapper.checkDeviceNameUnique(device.getDeviceName(), device.getParentId());
        if (StringUtils.isNotNull(info) && info.getDeviceId().longValue() != deviceId.longValue())
        {
            return UserConstants.DEPT_NAME_NOT_UNIQUE;
        }
        return UserConstants.DEPT_NAME_UNIQUE;
    }
}

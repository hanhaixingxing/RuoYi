package com.ruoyi.web.controller.system;

import java.util.List;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.core.domain.entity.SysDevice;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.service.ISysDeviceService;

/**
 * 设备信息
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/device")
public class SysDeviceController extends BaseController
{
    private String prefix = "system/device";

    @Autowired
    private ISysDeviceService deviceService;

    @RequiresPermissions("system:device:view")
    @GetMapping()
    public String device()
    {
        return prefix + "/device";
    }

    @RequiresPermissions("system:device:list")
    @PostMapping("/list")
    @ResponseBody
    public List<SysDevice> list(SysDevice device)
    {
        List<SysDevice> deviceList = deviceService.selectDeviceList(device);
        return deviceList;
    }

    /**
     * 新增设备
     */
    @GetMapping("/add/{parentId}")
    public String add(@PathVariable("parentId") Long parentId, ModelMap mmap)
    {
        mmap.put("device", deviceService.selectDeviceById(parentId));
        return prefix + "/add";
    }

    /**
     * 新增保存设备
     */
    @Log(title = "设备管理", businessType = BusinessType.INSERT)
    @RequiresPermissions("system:device:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated SysDevice device)
    {
        if (UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deviceService.checkDeviceNameUnique(device)))
        {
            return error("新增设备'" + device.getDeviceName() + "'失败，设备名称已存在");
        }
        device.setCreateBy(ShiroUtils.getLoginName());
        return toAjax(deviceService.insertDevice(device));
    }

    /**
     * 修改
     */
    @GetMapping("/edit/{deviceId}")
    public String edit(@PathVariable("deviceId") Long deviceId, ModelMap mmap)
    {
        SysDevice device = deviceService.selectDeviceById(deviceId);
        if (StringUtils.isNotNull(device) && 100L == deviceId)
        {
            device.setParentName("无");
        }
        mmap.put("device", device);
        return prefix + "/edit";
    }

    /**
     * 保存
     */
    @Log(title = "设备管理", businessType = BusinessType.UPDATE)
    @RequiresPermissions("system:device:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated SysDevice device)
    {
        if (UserConstants.DEPT_NAME_NOT_UNIQUE.equals(deviceService.checkDeviceNameUnique(device)))
        {
            return error("修改设备'" + device.getDeviceName() + "'失败，设备名称已存在");
        }
        else if (device.getParentId().equals(device.getDeviceId()))
        {
            return error("修改设备'" + device.getDeviceName() + "'失败，上级设备不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, device.getStatus())
                && deviceService.selectNormalChildrenDeviceById(device.getDeviceId()) > 0)
        {
            return AjaxResult.error("该设备包含未停用的子设备！");
        }
        device.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(deviceService.updateDevice(device));
    }

    /**
     * 删除
     */
    @Log(title = "设备管理", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:device:remove")
    @GetMapping("/remove/{deviceId}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("deviceId") Long deviceId)
    {
        if (deviceService.selectDeviceCount(deviceId) > 0)
        {
            return AjaxResult.warn("存在下级设备,不允许删除");
        }
//        if (deviceService.checkDeviceExistUser(deviceId))
//        {
//            return AjaxResult.warn("设备存在用户,不允许删除");
//        }
        return toAjax(deviceService.deleteDeviceById(deviceId));
    }

    /**
     * 校验设备名称
     */
    @PostMapping("/checkDeviceNameUnique")
    @ResponseBody
    public String checkDeviceNameUnique(SysDevice device)
    {
        return deviceService.checkDeviceNameUnique(device);
    }

    /**
     * 选择设备树
     *
     * @param deviceId 设备ID
     * @param excludeId 排除ID
     */
    @GetMapping(value = { "/selectDeviceTree/{deviceId}", "/selectDeviceTree/{deviceId}/{excludeId}" })
    public String selectDeviceTree(@PathVariable("deviceId") Long deviceId,
                                 @PathVariable(value = "excludeId", required = false) String excludeId, ModelMap mmap)
    {
        mmap.put("device", deviceService.selectDeviceById(deviceId));
        mmap.put("excludeId", excludeId);
        return prefix + "/tree";
    }

    /**
     * 加载设备列表树
     */
    @GetMapping("/treeData")
    @ResponseBody
    public List<Ztree> treeData()
    {
        List<Ztree> ztrees = deviceService.selectDeviceTree(new SysDevice());
        return ztrees;
    }

    /**
     * 加载设备列表树（排除下级）
     */
    @GetMapping("/treeData/{excludeId}")
    @ResponseBody
    public List<Ztree> treeDataExcludeChild(@PathVariable(value = "excludeId", required = false) Long excludeId)
    {
        SysDevice device = new SysDevice();
        device.setDeviceId(excludeId);
        List<Ztree> ztrees = deviceService.selectDeviceTreeExcludeChild(device);
        return ztrees;
    }

    /**
     * 加载角色设备（数据权限）列表树
     */
    @GetMapping("/roleDeviceTreeData")
    @ResponseBody
    public List<Ztree> deviceTreeData(SysRole role)
    {
        List<Ztree> ztrees = deviceService.roleDeviceTreeData(role);
        return ztrees;
    }
}

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
drop table if exists sys_role_device;
create table sys_role_device(
                               role_id   bigint(20) not null comment '角色ID',
                               device_id   bigint(20) not null comment '设备ID',
                               primary key(role_id, device_id)
) engine=innodb comment = '角色和设备关联表';

-- ----------------------------
-- 初始化-角色和设备关联表数据
-- ----------------------------
insert into sys_role_device values ('2', '100');
insert into sys_role_device values ('2', '101');
insert into sys_role_device values ('2', '105');
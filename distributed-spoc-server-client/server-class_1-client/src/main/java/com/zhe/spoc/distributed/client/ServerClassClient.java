package com.zhe.spoc.distributed.client;

import com.zhe.common.entity.ClassEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient("class-server")
public interface ServerClassClient {

    // 查看自己教学的班级
    @GetMapping("/classByMyId/{id}")
    @PreAuthorize("hasAnyAuthority('teacher')")
    public List<ClassEntity> classByMyId(@PathVariable("id") Integer id);
}

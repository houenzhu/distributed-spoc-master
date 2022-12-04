package com.zhe.spoc.distributed.client;

import com.zhe.common.entity.StudentEntity;
import com.zhe.common.entity.UserEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-server")
public interface ServerStuClient {
    @GetMapping("/getMyInfoFeign/{stuId}")
    @PreAuthorize("hasAnyAuthority('student', 'teacher')")
    StudentEntity getMyInfoFeign(@PathVariable("stuId") Integer stuId);

}

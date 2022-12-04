package com.zhe.spoc.distributed.controller;

import com.zhe.common.api.CommonResult;
import com.zhe.common.api.ResultCode;
import com.zhe.common.entity.ClassEntity;
import com.zhe.spoc.distributed.service.StuClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author HouEnZhu
 * @ClassName StuClassController
 * @Description TODO
 * @date 2022/10/24 19:52
 * @Version 1.0
 */

@RestController
@RequestMapping("/Student")
public class StuClassController {

    @Autowired
    private StuClassService stuClassService;

    @GetMapping("/myClass")
    @PreAuthorize("hasAnyAuthority('student')")
    public CommonResult<Map<String, Object>> myClass() {
        Map<String, Object> map = stuClassService.StuClassAll();
        if (map == null) {
            return CommonResult.failed(ResultCode.VALIDATE_FAILED, "暂未有班级信息噢");
        }
        return CommonResult.success(map);
    }
}

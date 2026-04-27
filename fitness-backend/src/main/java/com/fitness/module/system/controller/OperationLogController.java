package com.fitness.module.system.controller;

import com.fitness.common.PageResult;
import com.fitness.common.Result;
import com.fitness.module.system.service.OperationLogService;
import com.fitness.module.system.vo.OperationLogVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operation-logs")
@RequiredArgsConstructor
@Tag(name = "操作日志管理")
public class OperationLogController {

    private final OperationLogService operationLogService;

    @GetMapping
    @Operation(summary = "分页查询操作日志")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public Result<PageResult<OperationLogVO>> pageList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String operation,
            @RequestParam(required = false) String keyword) {
        return Result.success(operationLogService.pageList(pageNum, pageSize, module, operation, keyword));
    }
}

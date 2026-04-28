package com.easymusic.controller;

import com.easymusic.entity.vo.AdminDashboardVO;
import com.easymusic.entity.vo.ResponseVO;
import com.easymusic.service.AdminDashboardService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class AdminDashboardController extends ABaseController {

    @Resource
    private AdminDashboardService adminDashboardService;

    @RequestMapping("/loadDashboard")
    public ResponseVO loadDashboard() {
        AdminDashboardVO result = adminDashboardService.loadDashboard();
        return getSuccessResponseVO(result);
    }
}

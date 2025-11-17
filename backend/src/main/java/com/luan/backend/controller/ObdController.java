package com.luan.backend.controller;

import com.luan.backend.model.ObdLiveData;
import com.luan.backend.service.ObdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/obd")
public class ObdController {

    @Autowired
    private ObdService obdService;

    // 前端每秒调用，用来刷新仪表盘
    @GetMapping("/live")
    public ObdLiveData getLiveData() {
        return obdService.getLiveData();
    }

    // 前端点击“清除故障码”按钮时调用
    @PostMapping("/dtc/clear")
    public Map<String, Object> clearDtc() {
        obdService.clearDtc();
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        res.put("message", "All DTC cleared");
        return res;
    }
}

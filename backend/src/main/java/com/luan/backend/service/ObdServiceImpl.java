package com.luan.backend.service;

import com.luan.backend.model.ObdLiveData;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class ObdServiceImpl implements ObdService {

    private final Random random = new Random();

    //  保存当前激活的故障码
    private final List<String> activeDtcs = new ArrayList<>();

    @Override
    public ObdLiveData getLiveData() {
        ObdLiveData data = new ObdLiveData();

        // ====== 1. 模拟实时传感器数据 ======
        data.setRpm(600 + random.nextInt(5000));          // 600 ~ 5600
        data.setSpeed(random.nextInt(180));               // 0 ~ 179 km/h
        data.setCoolantTemp(70 + random.nextInt(40));     // 70 ~ 109 ℃
        data.setThrottle(random.nextInt(100));            // 0 ~ 99 %
        data.setFuelPressure(200 + random.nextInt(150));  // 200 ~ 349
        data.setBatteryVoltage(13.0 + random.nextDouble());// 13.0 ~ 13.9


        // ====== 2. 故障触发规则 ======

        // 规则① 冷却液温度 >105°C（电压异常）
        if (data.getCoolantTemp() > 105 && !activeDtcs.contains("P0117")) {
            activeDtcs.add("P0117"); // P0117：冷却液温度传感器电压低
        }

        // 规则② 发动机转速 >5500（点火失败）
        if (data.getRpm() > 5500 && !activeDtcs.contains("P0300")) {
            activeDtcs.add("P0300"); // P0300：多缸随机点火失败
        }

        // 规则③ 燃油压力 <220（催化转化器效率低）
        if (data.getFuelPressure() < 220 && !activeDtcs.contains("P0420")) {
            activeDtcs.add("P0420"); // P0420：催化转化器效率低
        }

        // ====== 3. 返回状态 ======
        data.setHasActiveDtc(!activeDtcs.isEmpty());
        data.setDtcList(new ArrayList<>(activeDtcs)); // 返回前端

        return data;
    }

    @Override
    public void clearDtc() {
        // 清除全部故障码
        activeDtcs.clear();
    }
}

package com.luan.backend.service;

import com.luan.backend.model.ObdLiveData;

public interface ObdService {

    // 每次被调用时，生成/更新一帧实时数据
    ObdLiveData getLiveData();

    // 清除故障码
    void clearDtc();
}

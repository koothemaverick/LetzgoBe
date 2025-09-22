package com.letzgo.LetzgoBe.domain.fcm.service;

import com.letzgo.LetzgoBe.domain.fcm.dto.res.FcmMessageResponse;

public interface FcmService {
    void sendMessageTo(FcmMessageResponse message);
}

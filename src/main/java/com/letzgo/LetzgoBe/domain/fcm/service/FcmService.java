package com.letzgo.LetzgoBe.domain.fcm.service;

import com.letzgo.LetzgoBe.domain.fcm.dto.FcmMessage;

public interface FcmService {
    void sendMessageTo(FcmMessage message);
}

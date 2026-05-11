package com.hms.PharmacyMS.config;

import com.hms.hms_common.event.PaymentSuccessEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class PaymentSuccessEventDeserializer extends ObjectMapperDeserializer<PaymentSuccessEvent> {
    public PaymentSuccessEventDeserializer() {
        super(PaymentSuccessEvent.class);
    }
}

package com.hms.NotificationMS.config;

import com.hms.hms_common.event.AppointmentEvent;
import io.quarkus.kafka.client.serialization.ObjectMapperDeserializer;

public class AppointmentEventDeserializer extends ObjectMapperDeserializer<AppointmentEvent> {
    public AppointmentEventDeserializer() {
        super(AppointmentEvent.class);
    }
}

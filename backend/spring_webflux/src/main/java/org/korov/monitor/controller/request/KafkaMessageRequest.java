package org.korov.monitor.controller.request;

import lombok.Data;

/**
 * @author korov
 */
@Data
public class KafkaMessageRequest {
    private Long sourceId;
    private String topic;
    private String key;
    private String message;
    private Integer partition;
}

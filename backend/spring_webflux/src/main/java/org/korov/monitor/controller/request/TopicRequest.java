package org.korov.monitor.controller.request;

import lombok.Data;

/**
 * @author korov
 */
@Data
public class TopicRequest {
    private Long sourceId;
    private String topic;
    private String broker;
    private Integer partition;
    private Integer replica;
}

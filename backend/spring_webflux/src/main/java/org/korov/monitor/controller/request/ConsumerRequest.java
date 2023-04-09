package org.korov.monitor.controller.request;

import lombok.Data;

/**
 * @author zhu.lei
 * @date 2023-04-09 20:20
 */
@Data
public class ConsumerRequest {
    private String topic;
    private String broker;
    private String group;
    private String reset;
    private Long partition;
    private Long offset;
}

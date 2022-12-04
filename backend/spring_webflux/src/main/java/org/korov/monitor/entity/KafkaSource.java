package org.korov.monitor.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


/**
 * @author korov
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("kafka_source")
public class KafkaSource {
    @Id
    Long id;
    String name;
    String broker;
}

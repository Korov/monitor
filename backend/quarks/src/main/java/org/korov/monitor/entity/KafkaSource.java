package org.korov.monitor.entity;


import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * @author korov
 */
@Entity
@Table(name = "kafka_source")
public class KafkaSource extends PanacheEntity {
    @Column(name = "name")
    private String name;
    @Column(name = "broker")
    private String broker;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }
}

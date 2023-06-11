package org.korov.monitor.entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author korov
 */
@Entity
@Table(name = "zookeeper_source")
public class ZookeeperSource extends PanacheEntity {
    @Column(name = "name")
    String name;
    @Column(name = "address")
    String address;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

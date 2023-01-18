package org.korov.monitor.vo;

public class Broker {
    private Integer id;
    private String host;
    private Integer port;

    public Broker(Integer id, String host, Integer port) {
        this.id = id;
        this.host = host;
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}

package org.korov.monitor.websocket;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/collected-costs")
@ApplicationScoped
public class CostCollector {
    private static final Logger logger = LoggerFactory.getLogger(CostCollector.class);
    private double sum;

    @GET
    // expose the sum of the collected costs
    public synchronized double getCosts() {
        return sum;
    }

    @Incoming("collector")
    // consume costs from collector channel
    synchronized void collect(Cost cost) {
        if ("EUR".equals(cost.getCurrency())) {
            sum += cost.getValue();
        }
        logger.info("sum:{}", sum);
    }
}

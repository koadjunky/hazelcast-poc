package eu.malycha.hazelcast.poc.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HazelcastPocServer {

    public static void main(String[] argv) {
        SpringApplication.run(HazelcastPocServer.class, argv);
    }
}

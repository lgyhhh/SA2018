package com.nju.mdfs.datanode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class DatanodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatanodeApplication.class, args);
    }
}

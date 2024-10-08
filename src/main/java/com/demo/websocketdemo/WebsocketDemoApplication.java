package com.demo.websocketdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class WebsocketDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketDemoApplication.class, args);
    }

}

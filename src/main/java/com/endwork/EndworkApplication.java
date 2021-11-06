package com.endwork;

import com.endwork.util.SpringUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class EndworkApplication {

    public static void main(String[] args) {
        //SpringApplication.run(EndworkApplication.class, args);


        ApplicationContext applicationContext =
                SpringApplication.run(EndworkApplication.class, args);

        SpringUtil.setApplicationContext(applicationContext);
        SpringUtil.printBean();
    }

}

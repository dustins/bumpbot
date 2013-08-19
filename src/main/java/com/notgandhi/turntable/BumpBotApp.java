package com.notgandhi.turntable;/*
 * Copyright
 */

import com.notgandhi.turntable.config.AppConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Class Description
 *
 * @author Dustin Sweigart <dustin@swigg.net>
 */
public class BumpBotApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().setDefaultProfiles("production");
        context.register(AppConfig.class);
        context.refresh();

        context.start();
    }
}

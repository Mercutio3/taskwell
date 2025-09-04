package com.taskwell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskwellApp {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new TaskwellApp().getGreeting());
        SpringApplication.run(TaskwellApp.class, args);
    }
}

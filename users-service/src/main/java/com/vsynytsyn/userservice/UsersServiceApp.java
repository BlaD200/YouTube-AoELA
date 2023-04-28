package com.vsynytsyn.userservice;

import com.vsynytsyn.commons.config.ModelMapperConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(ModelMapperConfiguration.class)
public class UsersServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(UsersServiceApp.class, args);
    }
}

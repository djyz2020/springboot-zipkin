package com.primeton.zipkin.service1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by haibozhang on 2019/8/14.
 */
@RestController
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/hi")
    public String hi(){
        ResponseEntity<String> result = restTemplate.getForEntity("http://localhost:8088/service2/test", String.class);
        return result.getBody();
    }

}

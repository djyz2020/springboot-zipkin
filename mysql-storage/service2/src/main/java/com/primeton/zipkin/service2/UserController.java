package com.primeton.zipkin.service2;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by haibozhang on 2019/8/14.
 */
@Api("service的API接口")
@RestController
@RequestMapping("/service2")
public class UserController {

    @Autowired
    private RestTemplate restTemplate;

    @ApiOperation("trace第二步")
    @RequestMapping("/test")
    public String hi() throws InterruptedException {
        Thread.sleep(200);
        ResponseEntity<String> result3 = restTemplate.getForEntity("http://localhost:8083/service3/test", String.class);
        ResponseEntity<String> result4 = restTemplate.getForEntity("http://localhost:8084/service4/test", String.class);
        return result3.getBody() + ":" + result4.getBody();
    }

}

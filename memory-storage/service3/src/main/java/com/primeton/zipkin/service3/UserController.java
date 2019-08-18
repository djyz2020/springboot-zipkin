package com.primeton.zipkin.service3;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by haibozhang on 2019/8/14.
 */
@Api("service的API接口")
@RestController
@RequestMapping("/service3")
public class UserController {

    @ApiOperation("trace第三步")
    @RequestMapping("/test")
    public String service() throws Exception {
        Thread.sleep(300);
        return "service3";
    }

}

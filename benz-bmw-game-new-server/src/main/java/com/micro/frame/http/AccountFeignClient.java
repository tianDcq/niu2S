package com.micro.frame.http;

import com.micro.common.bean.GlobeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "ACCOUNT-SERVICE")
public interface AccountFeignClient {

    @PostMapping("/acc/getPlayer")
    GlobeResponse<Object> getPlayer(@RequestParam("siteId") Long siteId,@RequestParam("account") String account);

}

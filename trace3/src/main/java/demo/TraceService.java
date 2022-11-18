package demo;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Component
@FeignClient(name = "trace", url = "127.0.0.1:8080")
public interface TraceService {

    @RequestMapping(method = RequestMethod.GET, path="/say/{message}")
    public void say(@PathVariable String message);
    
}

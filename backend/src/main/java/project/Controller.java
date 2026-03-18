package project;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class Controller {
    @GetMapping("/test")
    public String getMethodName() {
        return "hello world";
    }
    
}

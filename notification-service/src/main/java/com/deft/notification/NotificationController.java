package com.deft.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sergey Golitsyn
 * created on 12.10.2023
 */
@RestController
@RequestMapping
public class NotificationController {

    @GetMapping("/message")
    public String test() {
        return "Hello JavaInUse Called in Notification Service";
    }
}

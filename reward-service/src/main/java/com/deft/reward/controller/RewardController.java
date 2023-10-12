package com.deft.reward.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Sergey Golitsyn
 * created on 12.10.2023
 */
@RestController
@RequestMapping
public class RewardController {

    @GetMapping("/message")
    public String test() {
        return "Hello JavaInUse Called in Reward Service";
    }
}

package com.softdev.system.generator.controller;

import com.softdev.system.generator.util.ValueUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 页面控制器
 *
 * @author zhengkai.blog.csdn.net
 */
@RequiredArgsConstructor
@Controller
public class PageController {
    
    private final ValueUtil valueUtil;

    @GetMapping("/")
    public ModelAndView defaultPage() {
        return new ModelAndView("newui2").addObject("value", valueUtil);
    }
    
    @GetMapping("/index")
    public ModelAndView indexPage() {
        return new ModelAndView("newui2").addObject("value", valueUtil);
    }

}
package br.senac.sp.pi.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class ControllerIndex {

    @GetMapping("index")
    public ModelAndView index() {
        return new ModelAndView("../static/index");
    }

}


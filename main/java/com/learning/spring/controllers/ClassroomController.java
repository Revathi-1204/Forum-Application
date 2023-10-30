package com.learning.spring.controllers;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.learning.spring.models.ClassroomService;
import com.learning.spring.models.Student;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/classroom")
public class ClassroomController {
    
    @Autowired
    private ClassroomService classroom;

    @GetMapping
    public String classroom(Model model) throws SQLException {
        model.addAttribute("students", classroom.getStudents());
        return "classroom";
    }

    @PostMapping("/add")
    public String add(@Valid @ModelAttribute Student student,BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.student", result);
            attr.addFlashAttribute("student", student);
            return "redirect:/classroom";
        }
        classroom.add(student);
        return "redirect:/classroom";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable int id, @Valid @ModelAttribute Student student, BindingResult result, RedirectAttributes attr) {
        if (result.hasErrors()) {
            attr.addFlashAttribute("org.springframework.validation.BindingResult.student", result);
            attr.addFlashAttribute("student", student);
            return "redirect:/classroom";
        }
        classroom.replace(id, student);
        return "redirect:/classroom";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable int id) {
        classroom.remove(id);
        return "redirect:/classroom";
    }
}

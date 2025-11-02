package com.poli.fastlap.web.view;

import com.poli.fastlap.service.runner.RunnerAlreadyExistsException;
import com.poli.fastlap.service.runner.RunnerService;
import com.poli.fastlap.web.dto.RunnerDto;
import com.poli.fastlap.web.view.model.RunnerForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/runners")
@RequiredArgsConstructor
public class RunnerViewController {

    private final RunnerService runnerService;

    @GetMapping
    public String listRunners(Model model) {
        if (!model.containsAttribute("runnerForm")) {
            model.addAttribute("runnerForm", new RunnerForm());
        }
        populateRunners(model);
        return "runners/list";
    }

    @PostMapping
    public String registerRunner(@Valid @ModelAttribute("runnerForm") RunnerForm runnerForm,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            populateRunners(model);
            return "runners/list";
        }
        try {
            RunnerDto runner = runnerService.register(runnerForm.toRequest());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Corredor '%s' registrado correctamente".formatted(runner.nickname()));
            return "redirect:/runners";
        } catch (RunnerAlreadyExistsException ex) {
            bindingResult.rejectValue("nickname", "runnerForm.nickname", ex.getMessage());
            populateRunners(model);
            return "runners/list";
        }
    }

    private void populateRunners(Model model) {
        List<RunnerDto> runners = runnerService.findAll();
        model.addAttribute("runners", runners);
        model.addAttribute("activePage", "runners");
    }
}

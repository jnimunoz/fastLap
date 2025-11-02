package com.poli.fastlap.web.view;

import com.poli.fastlap.service.lap.LapHistoryService;
import com.poli.fastlap.service.race.LapRecordRequest;
import com.poli.fastlap.service.race.RaceAlreadyInProgressException;
import com.poli.fastlap.service.race.RaceNotFoundException;
import com.poli.fastlap.service.race.RaceNotInProgressException;
import com.poli.fastlap.service.race.RaceService;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.service.runner.RunnerService;
import com.poli.fastlap.web.dto.LapDto;
import com.poli.fastlap.web.dto.RaceDto;
import com.poli.fastlap.web.dto.RaceLapHistoryDto;
import com.poli.fastlap.web.dto.RunnerDto;
import com.poli.fastlap.web.view.model.RaceForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/race")
@RequiredArgsConstructor
public class RaceViewController {

    private final RaceService raceService;
    private final RunnerService runnerService;
    private final LapHistoryService lapHistoryService;

    @GetMapping
    public String raceDashboard(Model model) {
        if (!model.containsAttribute("raceForm")) {
            model.addAttribute("raceForm", new RaceForm());
        }
        List<RunnerDto> runners = runnerService.findAll();
        model.addAttribute("runners", runners);
        model.addAttribute("activePage", "race");

        Optional<RaceDto> activeRace = raceService.getActiveRace();
        activeRace.ifPresent(race -> {
            model.addAttribute("activeRace", race);
            RaceLapHistoryDto historyDto = lapHistoryService.getHistoryForRace(race.id());
            model.addAttribute("raceHistory", historyDto);
            model.addAttribute("laps", historyDto.laps());
            Map<Long, Long> lapCounts = historyDto.laps().stream()
                    .collect(Collectors.groupingBy(LapDto::runnerId, Collectors.counting()));
            model.addAttribute("lapCounts", lapCounts);
        });

        if (activeRace.isEmpty()) {
            model.addAttribute("activeRace", null);
            model.addAttribute("raceHistory", null);
            model.addAttribute("laps", List.of());
            model.addAttribute("lapCounts", Map.of());
        }

        return "race/control";
    }

    @PostMapping("/start")
    public String startRace(@Valid @ModelAttribute("raceForm") RaceForm raceForm,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.raceForm", bindingResult);
            redirectAttributes.addFlashAttribute("raceForm", raceForm);
            return "redirect:/race";
        }
        try {
            raceService.startRace(raceForm.toRequest());
            redirectAttributes.addFlashAttribute("successMessage", "Carrera iniciada correctamente.");
        } catch (RaceAlreadyInProgressException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            redirectAttributes.addFlashAttribute("raceForm", raceForm);
        }
        return "redirect:/race";
    }

    @PostMapping("/{raceId}/finish")
    public String finishRace(@PathVariable Long raceId,
                             RedirectAttributes redirectAttributes) {
        try {
            raceService.finishRace(raceId);
            redirectAttributes.addFlashAttribute("successMessage", "Carrera finalizada correctamente.");
        } catch (RaceNotFoundException | RaceNotInProgressException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/race";
    }

    @PostMapping("/{raceId}/lap")
    public String registerLap(@PathVariable Long raceId,
                              @RequestParam("runnerId") Long runnerId,
                              RedirectAttributes redirectAttributes) {
        try {
            LapDto lap = raceService.recordLap(raceId, new LapRecordRequest(runnerId));
            redirectAttributes.addFlashAttribute("successMessage",
                    "Vuelta %d registrada para %s".formatted(lap.lapNumber(), lap.runnerNickname()));
        } catch (RaceNotFoundException | RaceNotInProgressException | RunnerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/race";
    }
}

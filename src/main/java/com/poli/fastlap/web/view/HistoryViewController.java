package com.poli.fastlap.web.view;

import com.poli.fastlap.service.lap.LapHistoryService;
import com.poli.fastlap.service.race.RaceNotFoundException;
import com.poli.fastlap.service.race.RaceService;
import com.poli.fastlap.service.runner.RunnerNotFoundException;
import com.poli.fastlap.service.runner.RunnerService;
import com.poli.fastlap.web.dto.RaceDto;
import com.poli.fastlap.web.dto.RaceLapHistoryDto;
import com.poli.fastlap.web.dto.RunnerDto;
import com.poli.fastlap.web.dto.RunnerLapHistoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/history")
@RequiredArgsConstructor
public class HistoryViewController {

    private final RunnerService runnerService;
    private final RaceService raceService;
    private final LapHistoryService lapHistoryService;

    @GetMapping
    public String historyHome(Model model) {
        List<RunnerDto> runners = runnerService.findAll();
        model.addAttribute("runners", runners);
        List<RaceDto> races = raceService.findAll();
        model.addAttribute("races", races);
        model.addAttribute("activeRace", raceService.getActiveRace().orElse(null));
        model.addAttribute("activePage", "history");
        return "history/index";
    }

    @GetMapping("/runners/{runnerId}")
    public String runnerHistory(@PathVariable Long runnerId,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        try {
            RunnerLapHistoryDto historyDto = lapHistoryService.getHistoryForRunner(runnerId);
            model.addAttribute("history", historyDto);
            model.addAttribute("activePage", "history");
            return "history/runner";
        } catch (RunnerNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/history";
        }
    }

    @GetMapping("/races/{raceId}")
    public String raceHistory(@PathVariable Long raceId,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        try {
            RaceLapHistoryDto historyDto = lapHistoryService.getHistoryForRace(raceId);
            model.addAttribute("history", historyDto);
            model.addAttribute("activePage", "history");
            return "history/race";
        } catch (RaceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/history";
        }
    }
}

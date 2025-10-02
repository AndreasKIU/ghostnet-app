package de.ihrname.ghostnet.web;

import de.ihrname.ghostnet.domain.*;
import de.ihrname.ghostnet.repo.PersonRepo;
import de.ihrname.ghostnet.service.GhostNetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller @RequiredArgsConstructor
@RequestMapping("/nets")
public class NetController {
  private final GhostNetService svc;
  private final PersonRepo people;

  // Standard: REPORTED + SCHEDULED (MISSING ausgeblendet)
  @GetMapping
  public String list(@RequestParam(name="filter", required=false, defaultValue="open") String filter, Model m) {
    EnumSet<NetStatus> statuses = switch (filter) {
      case "open" -> EnumSet.of(NetStatus.REPORTED, NetStatus.SCHEDULED);
      case "scheduled" -> EnumSet.of(NetStatus.SCHEDULED);
      case "missing" -> EnumSet.of(NetStatus.MISSING);
      case "all" -> EnumSet.of(NetStatus.REPORTED, NetStatus.SCHEDULED, NetStatus.MISSING);
      default -> EnumSet.of(NetStatus.REPORTED, NetStatus.SCHEDULED);
    };
    m.addAttribute("filter", filter);
    m.addAttribute("nets", svc.listByStatuses(statuses));
    m.addAttribute("recoverers", people.findAll().stream().filter(p -> p.getRole()==Role.RECOVERER).toList());
    m.addAttribute("reporters", people.findAll().stream().filter(p -> p.getRole()==Role.REPORTER).toList());
    return "nets/list";
  }

  // Wer bergt was? – Übersicht
  @GetMapping("/team")
  public String team(Model m) {
    List<Person> recoverers = people.findAll().stream().filter(p -> p.getRole()==Role.RECOVERER).toList();
    var scheduled = svc.listByStatuses(EnumSet.of(NetStatus.SCHEDULED));
    Map<Long, List<GhostNet>> assignments = scheduled.stream()
        .filter(n -> n.getAssignee() != null)
        .collect(Collectors.groupingBy(n -> n.getAssignee().getId()));

    m.addAttribute("recoverers", recoverers);
    m.addAttribute("assignments", assignments);
    return "nets/team";
  }

  @GetMapping("/new")
  public String newForm(Model m) {
    m.addAttribute("net", new GhostNet());
    m.addAttribute("reporters", people.findAll().stream().filter(p -> p.getRole()==Role.REPORTER).toList());
    return "nets/new";
  }

  @PostMapping
  public String create(@ModelAttribute("net") GhostNet net,
                       @RequestParam(required=false) Long reporterId) {
    svc.report(net, reporterId);
    return "redirect:/nets";
  }

  @PostMapping("/{id}/assign")
  public String assign(@PathVariable Long id, @RequestParam Long personId) {
    svc.assign(id, personId);
    return "redirect:/nets";
  }

  @PostMapping("/{id}/unassign")
  public String unassign(@PathVariable Long id, @RequestParam Long personId) {
    svc.unassign(id, personId);
    return "redirect:/nets";
  }

  @PostMapping("/{id}/recover")
  public String recover(@PathVariable Long id, @RequestParam Long personId) {
    svc.markRecovered(id, personId);
    return "redirect:/nets";
  }

  @PostMapping("/{id}/missing")
  public String missing(@PathVariable Long id, @RequestParam Long reporterId) {
    svc.markMissing(id, reporterId);
    return "redirect:/nets";
  }

  @PostMapping("/{id}/reopen")
  public String reopen(@PathVariable Long id, @RequestParam Long reporterId) {
    svc.reopen(id, reporterId);
    return "redirect:/nets";
  }
}

package de.ihrname.ghostnet.web;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

  // Erwartete "fachliche" Fehler -> freundlich zurück auf /nets
  @ExceptionHandler(IllegalStateException.class)
  public String handleIllegalState(IllegalStateException ex, RedirectAttributes ra) {
    ra.addFlashAttribute("error", ex.getMessage());
    return "redirect:/nets";
  }

  // Unerwartete/Rendering-Fehler -> eigene Fehlerseite (vermeidet Whitelabel/Schleifen)
  @ExceptionHandler(Exception.class)
  public ModelAndView handleOther(Exception ex, HttpServletRequest req) {
    ModelAndView mav = new ModelAndView("error");
    mav.addObject("message", ex.getClass().getSimpleName() + ": " + ex.getMessage());
    return mav;
  }
}

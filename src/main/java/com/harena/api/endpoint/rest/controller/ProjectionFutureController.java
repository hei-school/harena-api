package com.harena.api.endpoint.rest.controller;

import com.harena.api.endpoint.rest.mapper.FluxJournalierMapper;
import com.harena.api.endpoint.rest.model.FluxJournalier;
import com.harena.api.endpoint.rest.model.ListPayload;
import com.harena.api.exception.InternalServerErrorException;
import com.harena.api.service.ProjectionFutureService;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patrimoines/{nomPatrimoine}")
public class ProjectionFutureController {
  private final ProjectionFutureService projectionFutureService;
  private final FluxJournalierMapper fluxJournalierMapper;

  @GetMapping("flux-journaliers")
  public ListPayload<FluxJournalier> fluxJournaliers(
      @PathVariable String nomPatrimoine,
      @RequestParam LocalDate debut,
      @RequestParam LocalDate fin) {
    return new ListPayload<>(
        projectionFutureService.getFluxJournaliers(nomPatrimoine, debut, fin).stream()
            .map(fluxJournalierMapper::toRestModel)
            .toList());
  }

  @GetMapping(value = "graphe", produces = MediaType.IMAGE_PNG_VALUE)
  public byte[] getGraphe(
      @PathVariable String nomPatrimoine,
      @RequestParam LocalDate debut,
      @RequestParam LocalDate fin) {
    try {
      return Files.readAllBytes(
          projectionFutureService.getGraph(nomPatrimoine, debut, fin).toPath());
    } catch (IOException e) {
      throw new InternalServerErrorException();
    }
  }
}

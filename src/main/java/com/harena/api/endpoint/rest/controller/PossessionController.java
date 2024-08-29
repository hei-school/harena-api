package com.harena.api.endpoint.rest.controller;

import com.harena.api.endpoint.rest.mapper.PossesionMapper;
import com.harena.api.endpoint.rest.model.ListPayload;
import com.harena.api.endpoint.rest.model.Possession;
import com.harena.api.service.PossessionService;
import com.harena.api.utils.Page;
import com.harena.api.utils.PageRequest;
import com.harena.api.utils.Pageable;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/patrimoines/{nomPatrimoine}/possessions")
public class PossessionController {
  private final PossessionService possessionService;
  private final PossesionMapper possesionMapper;

  @GetMapping
  public Page<Possession> getPatrimoinePossessions(
      @PathVariable String nomPatrimoine,
      @RequestParam("page") int pageNumber,
      @RequestParam("page_size") int pageSize) {
    var possessions = possessionService.getPossessions(nomPatrimoine);
    List<Possession> possessionList =
        possessions.stream().map(possesionMapper::toRestModel).toList();
    Pageable<Possession> possessionPageable = new Pageable<>(possessionList);

    return possessionPageable.getPage(PageRequest.of(pageNumber, pageSize));
  }

  @PutMapping
  public ListPayload<Possession> crupdatePatrimoinePossessions(
      @PathVariable String nomPatrimoine, @RequestBody ListPayload<Possession> toSavePossessions) {
    return new ListPayload<>(
        possessionService
            .savePossessions(
                nomPatrimoine,
                toSavePossessions.data().stream().map(possesionMapper::toObjectModel).toList())
            .stream()
            .map(possesionMapper::toRestModel)
            .toList());
  }

  @GetMapping("/{nomPossession}")
  public Possession getPossessionPatrimoineByNom(
      @PathVariable String nomPatrimoine, @PathVariable String nomPossession) {
    Optional<Possession> fetchedPossession =
        possessionService
            .getPossession(nomPatrimoine, nomPossession)
            .map(possesionMapper::toRestModel);

    return fetchedPossession.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }

  @DeleteMapping("/{nomPossession}")
  public Possession removePossessionByNom(
      @PathVariable String nomPatrimoine, @PathVariable String nomPossession) {
    return possessionService
        .removePossession(nomPatrimoine, nomPossession)
        .map(possesionMapper::toRestModel)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
  }
}

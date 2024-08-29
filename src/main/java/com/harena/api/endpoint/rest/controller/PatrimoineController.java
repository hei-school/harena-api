package com.harena.api.endpoint.rest.controller;

import com.harena.api.endpoint.rest.mapper.PatrimoineMapper;
import com.harena.api.endpoint.rest.model.ListPayload;
import com.harena.api.endpoint.rest.model.Patrimoine;
import com.harena.api.service.PatrimoineService;
import com.harena.api.utils.PageRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("patrimoines")
public class PatrimoineController {
  private final PatrimoineService patrimoineService;
  private final PatrimoineMapper patrimoineMapper;

  @PutMapping
  public ListPayload<Patrimoine> crupdatePatrimoines(@RequestBody ListPayload<Patrimoine> entity) {
    List<school.hei.patrimoine.modele.Patrimoine> patrimoines =
        entity.data().stream().map(patrimoineMapper::toObjectModel).toList();
    return new ListPayload<>(
        patrimoineService.savePatrimoines(patrimoines).stream()
            .map(patrimoineMapper::toRestModel)
            .toList());
  }

  @GetMapping
  public ListPayload<Patrimoine> getPatrimoines(
      @RequestParam("page") int pageNumber, @RequestParam("page_size") int pageSize) {
    return new ListPayload<>(
        patrimoineService.getAllPatrimoine(PageRequest.of(pageNumber, pageSize)).stream()
            .map(patrimoineMapper::toRestModel)
            .toList());
  }

  @GetMapping("/{nomPatrimoine}")
  public Patrimoine getPatrimoine(@PathVariable String nomPatrimoine) {
    return patrimoineMapper.toRestModel(patrimoineService.getPatrimone(nomPatrimoine));
  }
}

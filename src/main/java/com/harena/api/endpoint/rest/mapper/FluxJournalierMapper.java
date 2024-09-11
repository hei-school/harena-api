package com.harena.api.endpoint.rest.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.FluxJournalier;

import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

@Component
@RequiredArgsConstructor
public class FluxJournalierMapper
    implements Mapper<FluxJournalier, com.harena.api.endpoint.rest.model.FluxJournalier> {
  private final FluxAgentMapper fluxAgentMapper;
  private final ArgentMapper argentMapper;

  @Override
  public com.harena.api.endpoint.rest.model.FluxJournalier toRestModel(
      FluxJournalier objectModel) {
    return new com.harena.api.endpoint.rest.model.FluxJournalier()
        .fluxArgents(objectModel.flux().stream().map(fluxAgentMapper::toRestModel).toList())
        .date(objectModel.date())
            .argent(argentMapper.toRestModel(objectModel.argent()));
  }

  @Override
  public FluxJournalier toObjectModel(
      com.harena.api.endpoint.rest.model.FluxJournalier restModel) {
    return new FluxJournalier(
        restModel.getDate(),
        requireNonNull(argentMapper.toObjectModel(requireNonNull(restModel.getArgent()))),
        requireNonNull(restModel.getFluxArgents()).stream()
            .map(fluxAgentMapper::toObjectModel)
            .collect(Collectors.toSet()));
  }
}

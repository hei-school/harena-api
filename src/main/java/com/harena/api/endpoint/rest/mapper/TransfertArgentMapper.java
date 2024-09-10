package com.harena.api.endpoint.rest.mapper;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.*;

@Component
@RequiredArgsConstructor
class TransfertArgentMapper
    implements Mapper<TransfertArgent, com.harena.api.endpoint.rest.model.TransfertArgent> {
  private final DeviseMapper deviseMapper;
  private final PossesionMapper possesionMapper;
  private final ArgentMapper argentMapper;

  @SneakyThrows
  private static Object getPrivateFieldValue(String fieldName, Object instance) {
    var field = Materiel.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    var value = field.get(instance);
    field.setAccessible(false);
    return value;
  }

  @Override
  public com.harena.api.endpoint.rest.model.TransfertArgent toRestModel(
      TransfertArgent objectModel) {
    // The worst things ! cause field: transfertCommeGroupe don't have getter
    GroupePossession groupePossession =
        (GroupePossession) getPrivateFieldValue("transfertCommeGroupe", objectModel);
    // The worst things ! cause field: possessions don't have getter
    Set<Possession> possessions =
        (Set<Possession>) getPrivateFieldValue("possessions", groupePossession);
    com.harena.api.endpoint.rest.model.GroupePossession restGroupePossession =
        new com.harena.api.endpoint.rest.model.GroupePossession();

    restGroupePossession.setNom(groupePossession.getNom());
    restGroupePossession.setT(groupePossession.getT());
    restGroupePossession.setDevise(deviseMapper.toRestModel(groupePossession.getDevise()));
    restGroupePossession.setValeurComptable(groupePossession.getValeurComptable());
    restGroupePossession.setPossessions(
        possessions.stream().map(possesionMapper::toRestModel).toList());

    return new com.harena.api.endpoint.rest.model.TransfertArgent()
        .nom(objectModel.getNom())
        .t(objectModel.getT())
        .valeurComptable(objectModel.getValeurComptable())
        .devise(deviseMapper.toRestModel(objectModel.getDevise()))
        .transfertCommeGroupe(restGroupePossession);
  }

  @Override
  public TransfertArgent toObjectModel(
      com.harena.api.endpoint.rest.model.TransfertArgent restModel) {
    var nom = restModel.getNom();
    var restDevise = restModel.getDevise();
    Argent depuisArgent =
        argentMapper.toObjectModel(
            Objects.requireNonNull(
                Objects.requireNonNull(
                        Objects.requireNonNull(
                                Objects.requireNonNull(restModel.getTransfertCommeGroupe())
                                    .getPossessions())
                            .getFirst()
                            .getFluxArgent())
                    .getArgent()));
    Argent versArgent =
        argentMapper.toObjectModel(
            Objects.requireNonNull(
                Objects.requireNonNull(
                        Objects.requireNonNull(
                                Objects.requireNonNull(restModel.getTransfertCommeGroupe())
                                    .getPossessions())
                            .getLast()
                            .getFluxArgent())
                    .getArgent()));
    LocalDate debut =
        restModel.getTransfertCommeGroupe().getPossessions().getFirst().getFluxArgent().getDebut();
    LocalDate fin =
        restModel.getTransfertCommeGroupe().getPossessions().getFirst().getFluxArgent().getFin();
    var fluxMensuel =
        restModel
            .getTransfertCommeGroupe()
            .getPossessions()
            .getFirst()
            .getFluxArgent()
            .getFluxMensuel();
    var dateOperation =
        restModel
            .getTransfertCommeGroupe()
            .getPossessions()
            .getFirst()
            .getFluxArgent()
            .getDateDOperation();
    if (restDevise == null) {
      return new TransfertArgent(
          nom, depuisArgent, versArgent, debut, fin, fluxMensuel, dateOperation);
    }
    var devise = new DeviseMapper().toObjectModel(restDevise);
    return new TransfertArgent(
        nom, depuisArgent, versArgent, debut, fin, fluxMensuel, dateOperation, devise);
  }
}

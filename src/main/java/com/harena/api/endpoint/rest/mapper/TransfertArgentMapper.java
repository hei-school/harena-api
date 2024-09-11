package com.harena.api.endpoint.rest.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.*;

import java.time.LocalDate;
import java.util.Set;

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
        .transfertCommeGroupe(restGroupePossession);
  }

  @Override
  public TransfertArgent toObjectModel(
      com.harena.api.endpoint.rest.model.TransfertArgent restModel) {
    var nom = restModel.getNom();
    Argent depuisArgent = argentMapper.toObjectModel(restModel.getDepuisArgent());
    Argent versArgent = argentMapper.toObjectModel(restModel.getVersArgent());
    var restDevise = restModel.getDevise();
    LocalDate debut = restModel.getDebut();
    LocalDate fin = restModel.getFin();
    var fluxMensuel = restModel.getFluxMensuel();
    var dateOperation = restModel.getDateDOperation();
    if (restDevise == null) {
      return new TransfertArgent(
          nom, depuisArgent, versArgent, debut, fin, fluxMensuel, dateOperation);
    }
    var devise = new DeviseMapper().toObjectModel(restDevise);
    return new TransfertArgent(
        nom, depuisArgent, versArgent, debut, fin, fluxMensuel, dateOperation, devise);
  }
}

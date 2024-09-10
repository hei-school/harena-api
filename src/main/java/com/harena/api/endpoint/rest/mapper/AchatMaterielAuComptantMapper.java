package com.harena.api.endpoint.rest.mapper;

import static java.util.Objects.requireNonNullElse;

import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.*;

@Component
@RequiredArgsConstructor
class AchatMaterielAuComptantMapper
    implements Mapper<
        AchatMaterielAuComptant, com.harena.api.endpoint.rest.model.AchatMaterielAuComptant> {
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
  public com.harena.api.endpoint.rest.model.AchatMaterielAuComptant toRestModel(
      AchatMaterielAuComptant objectModel) {
    // The worst things ! cause field: achatCommeGroupe don't have getter
    GroupePossession groupePossession =
        (GroupePossession) getPrivateFieldValue("achatCommeGroupe", objectModel);
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

    return new com.harena.api.endpoint.rest.model.AchatMaterielAuComptant()
        .nom(objectModel.getNom())
        .t(objectModel.getT())
        .valeurComptable(objectModel.getValeurComptable())
        .devise(deviseMapper.toRestModel(objectModel.getDevise()))
        .achatCommeGroupe(restGroupePossession);
  }

  @Override
  public AchatMaterielAuComptant toObjectModel(
      com.harena.api.endpoint.rest.model.AchatMaterielAuComptant restModel) {
    var nom = restModel.getNom();
    var tTime = restModel.getT();
    int valeurComptableALAchat = requireNonNullElse(restModel.getValeurComptable(), 0);
    var restDevise = restModel.getDevise();
    Double tauxAppreciationAnnuelle =
        Objects.requireNonNull(
                Objects.requireNonNull(
                        Objects.requireNonNull(restModel.getAchatCommeGroupe()).getPossessions())
                    .getFirst()
                    .getMateriel())
            .getTauxDappreciationAnnuel();
    Argent financeur =
        argentMapper.toObjectModel(
            restModel.getAchatCommeGroupe().getPossessions().getLast().getFluxArgent().getArgent());
    if (restDevise == null) {
      return new AchatMaterielAuComptant(
          nom, tTime, valeurComptableALAchat, tauxAppreciationAnnuelle, financeur);
    }
    var devise = new DeviseMapper().toObjectModel(restDevise);
    return new AchatMaterielAuComptant(
        nom, tTime, valeurComptableALAchat, tauxAppreciationAnnuelle, financeur, devise);
  }
}

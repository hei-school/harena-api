package com.harena.api.endpoint.rest.mapper;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import school.hei.patrimoine.modele.possession.*;

import java.util.Objects;
import java.util.Set;

import static com.harena.api.endpoint.rest.model.Possession.TypeEnum.FLUX_ARGENT;
import static com.harena.api.endpoint.rest.model.Possession.TypeEnum.MATERIEL;
import static java.util.Objects.requireNonNullElse;

@Component
@RequiredArgsConstructor
class AchatMaterielAuComptantMapper
    implements Mapper<
        AchatMaterielAuComptant, com.harena.api.endpoint.rest.model.AchatMaterielAuComptant> {
  private final DeviseMapper deviseMapper;
  private final ArgentMapper argentMapper;
  private final FluxAgentMapper fluxAgentMapper;
  private final MaterielMapper materielMapper;


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
            possessions.stream().map(item -> {
              var possession = new com.harena.api.endpoint.rest.model.Possession();
              if (item instanceof FluxArgent fluxArgent) {
                possession.setType(FLUX_ARGENT);
                var flux = fluxAgentMapper.toRestModel(fluxArgent);
                possession.setFluxArgent(flux);
              } else if (item instanceof Materiel materiel) {
                possession.setType(MATERIEL);
                var material = materielMapper.toRestModel(materiel);
                possession.setMateriel(material);
              }
              return possession;
            }).toList());

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
    var tauxAppreciationAnnuelle = restModel.getTauxDappreciationAnnuel();
    Argent financeur = argentMapper.toObjectModel(Objects.requireNonNull(restModel.getFinanceur()));
    if (restDevise == null) {
      return new AchatMaterielAuComptant(
          nom, tTime, valeurComptableALAchat, tauxAppreciationAnnuelle, financeur);
    }
    var devise = new DeviseMapper().toObjectModel(restDevise);
    return new AchatMaterielAuComptant(
        nom, tTime, valeurComptableALAchat, tauxAppreciationAnnuelle, financeur, devise);
  }
}

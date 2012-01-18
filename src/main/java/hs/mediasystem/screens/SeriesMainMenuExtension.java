package hs.mediasystem.screens;

import hs.mediasystem.fs.SeriesMediaTree;

import java.nio.file.Paths;

import javafx.scene.image.Image;

import javax.inject.Inject;
import javax.inject.Provider;

public class SeriesMainMenuExtension implements MainMenuExtension {
  private final Provider<SelectMediaPresentation> selectMediaPresentationProvider;

  @Inject
  public SeriesMainMenuExtension(Provider<SelectMediaPresentation> selectMediaPresentationProvider) {
    this.selectMediaPresentationProvider = selectMediaPresentationProvider;
  }

  @Override
  public String getTitle() {
    return "Series";
  }

  @Override
  public Image getImage() {
    return new Image("images/aktion.png");
  }

  @Override
  public void select(ProgramController controller) {
    SelectMediaPresentation presentation = selectMediaPresentationProvider.get();

    presentation.setMediaTree(new SeriesMediaTree(Paths.get(controller.getIni().getValue("general", "series.path"))));

    controller.showScreen(presentation.getView());
  }
}

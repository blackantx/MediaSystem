package hs.mediasystem.screens.collection;

import hs.mediasystem.screens.MediaNode;
import hs.mediasystem.screens.MediaNodeEvent;
import hs.mediasystem.screens.ProgramController;
import hs.mediasystem.util.DialogPane;
import hs.mediasystem.util.GridPaneUtil;

import java.util.Set;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;

public class CollectionSelectorPresentation {
  public final ObjectProperty<MediaNode> focusedMediaNode = new SimpleObjectProperty<>();
  public final ObjectProperty<MediaNode> rootMediaNode = new SimpleObjectProperty<>();
  public final ObjectProperty<Node> defaultInputFocus = new SimpleObjectProperty<>();

  public final EventHandler<MediaNodeEvent> onSelect = new SelectEventHandler();
  public final EventHandler<MediaNodeEvent> onInfoSelect = new InfoEventHandler();

  private final ProgramController controller;
  private final Set<DetailPaneDecoratorFactory> detailPaneDecoratorFactories;

  @Inject
  public CollectionSelectorPresentation(ProgramController controller, Set<DetailPaneDecoratorFactory> detailPaneDecoratorFactories) {
    this.controller = controller;
    this.detailPaneDecoratorFactories = detailPaneDecoratorFactories;
  }

  private DialogPane createInformationDialog(final MediaNode mediaNode, Set<DetailPaneDecoratorFactory> detailPaneDecoratorFactories) {
    final AbstractDetailPane detailPane = new AbstractDetailPane(detailPaneDecoratorFactories) { // TODO provider detailPaneDecoratorFactories!
      {
        interactive.set(true);
        getStylesheets().add("controls.css");
      }

      @Override
      protected void initialize(DecoratablePane decoratablePane) {
        BorderPane borderPane = new BorderPane();
        GridPane gridPane = GridPaneUtil.create(new double[] {33, 34, 33}, new double[] {100});
        gridPane.setHgap(20);

        HBox hbox = new HBox();
        hbox.setId("link-area");

        decoratablePane.getChildren().add(borderPane);

        borderPane.setCenter(gridPane);
        borderPane.setBottom(hbox);

        gridPane.add(new VBox() {{
          setId("title-image-area");
        }}, 0, 0);

        gridPane.add(new VBox() {{
          getChildren().add(new VBox() {{
            setId("title-area");
          }});

          getChildren().add(new VBox() {{
            setId("description-area");
          }});
        }}, 1, 0);

        gridPane.add(new VBox() {{
          setId("action-area");
          setSpacing(2);
        }}, 2, 0);
      }
    };

    detailPane.content.set(mediaNode.getMedia());

    DialogPane dialogPane = new DialogPane() {
      @Override
      public void close() {
        super.close();

        detailPane.content.set(null);
      }
    };

    dialogPane.getChildren().add(detailPane);

    return dialogPane;
  }

  class SelectEventHandler implements EventHandler<MediaNodeEvent> {
    @Override
    public void handle(MediaNodeEvent event) {
      if(event.getMediaNode().getMediaRoot() != null) {
        controller.setLocation(new CollectionLocation(event.getMediaNode().getMediaRoot()));
      }
      else {
        controller.play(event.getMediaNode().getMediaItem());
      }
      event.consume();
    }
  }

  class InfoEventHandler implements EventHandler<MediaNodeEvent> {
    @Override
    public void handle(MediaNodeEvent event) {
      controller.showDialog(createInformationDialog(event.getMediaNode(), detailPaneDecoratorFactories));

      event.consume();
    }
  }
}

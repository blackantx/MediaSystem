package hs.mediasystem.screens;

import hs.mediasystem.framework.Media;
import hs.mediasystem.util.MapBindings;
import hs.mediasystem.util.WeakBinder;

public class StandardCell extends DuoLineCell implements MediaNodeCell {
  private final WeakBinder binder = new WeakBinder();

  @Override
  public void attach(MediaNode mediaNode) {
    binder.bind(titleProperty(), MapBindings.selectString(mediaNode.dataMapProperty(), Media.class, "title"));
    binder.bind(subtitleProperty(), MapBindings.selectString(mediaNode.dataMapProperty(), Media.class, "subtitle"));
    binder.bind(extraInfoProperty(), MediaItemFormatter.releaseYearBinding(mediaNode));

    collectionSizeProperty().set(mediaNode.getChildren().size());

    binder.bind(ratingProperty(), MapBindings.selectDouble(mediaNode.dataMapProperty(), Media.class, "rating").divide(10));
    binder.bind(viewedProperty(), MapBindings.selectBoolean(mediaNode.mediaData, "viewed"));
  }

  @Override
  public void detach() {
    binder.unbindAll();
  }
}
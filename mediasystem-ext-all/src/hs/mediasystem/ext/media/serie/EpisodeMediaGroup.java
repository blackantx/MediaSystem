package hs.mediasystem.ext.media.serie;

import hs.mediasystem.MediaRootType;
import hs.mediasystem.framework.MediaItem;
import hs.mediasystem.framework.MediaRoot;
import hs.mediasystem.screens.AbstractMediaGroup;
import hs.mediasystem.screens.MediaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

@Named @MediaRootType(Season.class)
public class EpisodeMediaGroup extends AbstractMediaGroup {

  public EpisodeMediaGroup() {
    super("episodeNumber", "Episode", true);
  }

  @Override
  public List<MediaNode> getMediaNodes(MediaRoot parentMediaRoot, List<? extends MediaItem> mediaItems) {
    Collections.sort(mediaItems, EpisodeComparator.INSTANCE);
    List<MediaNode> nodes = new ArrayList<>();

    for(MediaItem mediaItem : mediaItems) {
      nodes.add(new MediaNode(mediaItem));
    }

    return nodes;
  }
}

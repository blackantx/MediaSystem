package hs.mediasystem.ext.media.serie;

import hs.mediasystem.MediaRootType;
import hs.mediasystem.framework.MediaRoot;
import hs.mediasystem.screens.AbstractMediaGroup;
import hs.mediasystem.screens.MediaNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Named;

@Named @MediaRootType(Serie.class)
public class SeasonMediaGroup extends AbstractMediaGroup<Episode> {

  public SeasonMediaGroup() {
    super("episodeNumber-group-season", "Season", true);
  }

  private static String determineSeasonName(Episode episode) {
    Integer season = episode.season.get();

    return season == null || season == 0 ? "Specials" : "Season " + season;
  }

  private static String determineShortSeasonName(Episode episode) {
    Integer season = episode.season.get();

    return season == null || season == 0 ? "Sp." : "" + season;
  }

  @Override
  public List<MediaNode> getMediaNodes(MediaRoot parentMediaRoot, List<? extends Episode> episodes) {
    Collections.sort(episodes, EpisodeComparator.INSTANCE);
    List<MediaNode> nodes = new ArrayList<>();
    String previousSeasonName = null;
    MediaNode seasonNode = null;

    for(Episode episode : episodes) {
      String seasonName = determineSeasonName(episode);

      if(seasonNode == null || !seasonName.equals(previousSeasonName)) {
        seasonNode = new MediaNode("season[" + seasonName + "]", seasonName, determineShortSeasonName(episode), false);

        nodes.add(seasonNode);
      }

      seasonNode.add(new MediaNode(episode));

      previousSeasonName = seasonName;
    }

    return nodes;
  }
}

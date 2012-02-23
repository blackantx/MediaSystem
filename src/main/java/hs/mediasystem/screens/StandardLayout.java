package hs.mediasystem.screens;

import hs.mediasystem.framework.CellProvider;
import hs.mediasystem.framework.Groups;
import hs.mediasystem.framework.MediaItem;
import hs.mediasystem.fs.EpisodeComparator;
import hs.mediasystem.fs.EpisodeGroup;
import hs.mediasystem.fs.MediaItemComparator;
import hs.mediasystem.fs.Season;
import hs.mediasystem.fs.SeasonGrouper;
import hs.mediasystem.fs.TitleGrouper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class StandardLayout {

  public CellProvider<MediaNode> getCellProvider(MediaItem parent) {
    String mediaType = parent.getMediaType();

    if(mediaType.equals("MOVIE_ROOT")) {
      return new MovieCellProvider();
    }
    else if(mediaType.equals("SERIE_ROOT")) {
      return new BannerCellProvider();
    }

    return new SeasonAndEpisodeCellProvider();
  }

  public List<MediaNode> getChildren(MediaItem parent) {
    List<? extends MediaItem> children = parent.children();
    List<MediaNode> output = new ArrayList<>();

    if(parent.getMediaType().equals("MOVIE_ROOT")) {
      Collection<List<MediaItem>> groupedItems = Groups.group(children, new TitleGrouper());

      for(List<MediaItem> group : groupedItems) {
        if(group.size() > 1) {
          Collections.sort(group, MediaItemComparator.INSTANCE);
          EpisodeGroup g = new EpisodeGroup(parent.getMediaTree(), group);

          List<MediaNode> nodeChildren = new ArrayList<>();
          for(MediaItem item : group) {
            nodeChildren.add(new MediaNode(this, item));
          }

          output.add(new MediaNode(this, g, nodeChildren));
        }
        else {
          output.add(new MediaNode(this, group.get(0)));
        }
      }
    }
    else if(parent.getMediaType().equals("SERIE")) {
      Collection<List<MediaItem>> groupedItems = Groups.group(children, new SeasonGrouper());

      for(List<MediaItem> group : groupedItems) {
        if(group.size() > 1) { // TODO really?  We want to add seasons with just 1 episode as a seperate one and not as a Season??  Test this.
          MediaItem episodeOne = group.get(0);
          Season s;

          if(episodeOne.getSeason() == null) {
            s = new Season(parent.getMediaTree(), parent.getTitle(), 0);
          }
          else {
            s = new Season(parent.getMediaTree(), parent.getTitle(), episodeOne.getSeason());
          }

          Collections.sort(group, EpisodeComparator.INSTANCE);
          List<MediaNode> nodeChildren = new ArrayList<>();

          for(MediaItem item : group) {
            nodeChildren.add(new MediaNode(this, item));
          }

          output.add(new MediaNode(this, s, nodeChildren));
        }
        else {
          output.add(new MediaNode(this, group.get(0)));
        }
      }
    }
    else {
      for(MediaItem child : children) {
        output.add(new MediaNode(this, child));
      }
    }

    Collections.sort(output, MediaNodeComparator.INSTANCE);

    return output;
  }

  public boolean expandTopLevel(MediaItem root) {
    return root.getMediaType().equals("SERIE");
  }

  public boolean hasChildren(MediaItem mediaItem) {
    return !mediaItem.isLeaf();
  }

  public boolean isRoot(MediaItem mediaItem) {
    String mediaType = mediaItem.getMediaType();

    return mediaType.equals("MOVIE_ROOT") || mediaType.equals("SERIE_ROOT") || mediaType.equals("SERIE");
  }

  public MediaNode wrap(MediaItem root) {
    return new MediaNode(this, root);
  }
}

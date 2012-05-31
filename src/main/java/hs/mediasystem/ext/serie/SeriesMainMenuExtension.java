package hs.mediasystem.ext.serie;

import hs.mediasystem.db.TypeBasedItemEnricher;
import hs.mediasystem.enrich.EnrichCache;
import hs.mediasystem.framework.MediaItem;
import hs.mediasystem.framework.MediaNodeCellProviderRegistry;
import hs.mediasystem.fs.MediaRootType;
import hs.mediasystem.media.Media;
import hs.mediasystem.screens.MainMenuExtension;
import hs.mediasystem.screens.Navigator.Destination;
import hs.mediasystem.screens.ProgramController;
import hs.mediasystem.screens.StandardLayout;
import hs.mediasystem.screens.StandardLayout.MediaGroup;
import hs.mediasystem.screens.selectmedia.BannerCell;
import hs.mediasystem.screens.selectmedia.SelectMediaPresentation;
import hs.mediasystem.screens.selectmedia.StandardView;

import java.nio.file.Paths;

import javafx.scene.image.Image;

import javax.inject.Inject;
import javax.inject.Provider;

public class SeriesMainMenuExtension implements MainMenuExtension {
  private final Provider<SelectMediaPresentation> selectMediaPresentationProvider;
  private final EnrichCache enrichCache;

  @Inject
  public SeriesMainMenuExtension(Provider<SelectMediaPresentation> selectMediaPresentationProvider, EnrichCache enrichCache, SerieEnricher serieEnricher, EpisodeEnricher episodeEnricher) {
    this.selectMediaPresentationProvider = selectMediaPresentationProvider;
    this.enrichCache = enrichCache;

    TvdbSerieEnricher tvdbSerieEnricher = new TvdbSerieEnricher();

    TypeBasedItemEnricher.registerEnricher(hs.mediasystem.ext.serie.Serie.class, tvdbSerieEnricher);
    TypeBasedItemEnricher.registerEnricher(Episode.class, new TvdbEpisodeEnricher(tvdbSerieEnricher));

    StandardView.registerLayout(SeriesMediaTree.class, MediaRootType.SERIES);
    StandardView.registerLayout(SerieItem.class, MediaRootType.SERIE_EPISODES);
    MediaNodeCellProviderRegistry.register(MediaNodeCellProviderRegistry.HORIZONTAL_CELL, hs.mediasystem.ext.serie.Serie.class, new Provider<BannerCell>() {
      @Override
      public BannerCell get() {
        return new BannerCell();
      }
    });
    MediaNodeCellProviderRegistry.register(MediaNodeCellProviderRegistry.HORIZONTAL_CELL, Episode.class, new Provider<EpisodeCell>() {
      @Override
      public EpisodeCell get() {
        return new EpisodeCell();
      }
    });

    StandardLayout.registerMediaGroup(SerieItem.class, new MediaGroup(new SeasonGrouper(), EpisodeComparator.INSTANCE, true, true) {
      @Override
      public Media createMediaFromFirstItem(MediaItem item) {
        Integer season = item.get(Episode.class).getSeason();

        return new Media(season == null || season == 0 ? "Specials" : "Season " + season);
      }

      @Override
      public String getShortTitle(MediaItem item) {
        Integer season = item.get(Episode.class).getSeason();

        return season == null || season == 0 ? "Sp." : "" + season;
      }
    });

    enrichCache.registerEnricher(hs.mediasystem.ext.serie.Serie.class, serieEnricher);
    enrichCache.registerEnricher(Episode.class, episodeEnricher);
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
  public Destination getDestination(final ProgramController controller) {
    return new Destination(getTitle()) {
      private SelectMediaPresentation presentation;
      private SeriesMediaTree mediaTree;

      @Override
      protected void init() {
        presentation = selectMediaPresentationProvider.get();
      }

      @Override
      protected void intro() {
        controller.showScreen(presentation.getView());
        if(mediaTree == null) {
          mediaTree = new SeriesMediaTree(enrichCache, Paths.get(controller.getIni().getValue("general", "series.path")));
          presentation.setMediaTree(mediaTree);
        }
      }
    };
  }

  @Override
  public double order() {
    return 0.2;
  }
}
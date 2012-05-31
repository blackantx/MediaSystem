package hs.mediasystem.ext.serie;

import hs.mediasystem.db.Casting;
import hs.mediasystem.db.EnricherMatch;
import hs.mediasystem.db.Identifier;
import hs.mediasystem.db.IdentifyException;
import hs.mediasystem.db.Item;
import hs.mediasystem.db.ItemEnricher;
import hs.mediasystem.db.ItemNotFoundException;
import hs.mediasystem.db.Person;
import hs.mediasystem.db.MediaData.MatchType;
import hs.mediasystem.media.Media;

import java.util.List;

import com.moviejukebox.thetvdb.TheTVDB;
import com.moviejukebox.thetvdb.model.Actor;
import com.moviejukebox.thetvdb.model.Series;

public class TvdbSerieEnricher implements ItemEnricher {

  @Override
  public String getProviderCode() {
    return "TVDB";
  }

  @Override
  public EnricherMatch identifyItem(final Media media) throws IdentifyException {
    synchronized(TheTVDB.class) {
      TheTVDB tvDB = new TheTVDB("587C872C34FF8028");

      List<Series> results = tvDB.searchSeries(media.getTitle(), "en");

      System.out.println("TVDB results: " + results);

      if(results.isEmpty()) {
        throw new IdentifyException("Cannot identify Serie with name: " + media.getTitle());
      }

      return new EnricherMatch(new Identifier("Serie", getProviderCode(), results.get(0).getId()), MatchType.NAME, 1.0f);
    }
  }

  @Override
  public Item loadItem(String identifier) throws ItemNotFoundException {
    synchronized(TheTVDB.class) {
      TheTVDB tvDB = new TheTVDB("587C872C34FF8028");

      final Series series = tvDB.getSeries(identifier, "en");

      if(series == null) {
        throw new ItemNotFoundException(identifier);
      }

      Item item = new Item();

      item.setTitle(series.getSeriesName());
      item.setRating(Float.valueOf(series.getRating()));
      item.setPlot(series.getOverview());

      item.setBackgroundURL(series.getFanart());
      item.setBannerURL(series.getBanner());
      item.setPosterURL(series.getPoster());

      item.setGenres(series.getGenres().toArray(new String[series.getGenres().size()]));
      item.setLanguage(series.getLanguage());
      item.setRuntime(Integer.parseInt(series.getRuntime()));

      List<Actor> actors = tvDB.getActors(identifier);

      for(Actor actor : actors) {
        Person person = new Person();

        person.setName(actor.getName());
        person.setPhotoURL(actor.getImage());

        Casting casting = new Casting();

        casting.setItem(item);
        casting.setPerson(person);
        casting.setRole("actor");
        casting.setCharacterName(actor.getRole());
        casting.setIndex(actor.getSortOrder());

        item.getCastings().add(casting);
      }

      System.out.println(">>> Status of serie : " + series.getStatus());  // "Ended", "Continuing"
      System.out.println(">>> First aired : " +  series.getFirstAired());

      return item;
    }
  }
}
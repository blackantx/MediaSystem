package hs.mediasystem.db;

import java.util.Date;

public class Item {
  public static final Source<byte[]> NULL = new MemorySource<>(null);

  private int id;

  private Identifier identifier;

  private String title;
  private String subtitle;
  private int version;
  private String imdbId;
  private String plot;
  private Source<byte[]> poster;
  private Source<byte[]> background;
  private Source<byte[]> banner;
  private Float rating;
  private Integer releaseYear;
  private Date releaseDate;
  private int runtime;
  private Integer season;
  private Integer episode;
  private String[] genres = new String[] {};
  private String language;
  private String tagline;

  public Identifier getIdentifier() {
    return identifier;
  }

  public void setIdentifier(Identifier identifier) {
    this.identifier = identifier;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getVersion() {
    return version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public String getImdbId() {
    return imdbId;
  }

  public void setImdbId(String imdbId) {
    this.imdbId = imdbId;
  }

  public String getPlot() {
    return plot;
  }

  public void setPlot(String plot) {
    this.plot = plot;
  }

  public Source<byte[]> getPoster() {
    return poster;
  }

  public void setPoster(Source<byte[]> poster) {
    this.poster = poster;
  }

  public Source<byte[]> getBackground() {
    return background;
  }

  public void setBackground(Source<byte[]> background) {
    this.background = background;
  }

  public Source<byte[]> getBanner() {
    return banner;
  }

  public void setBanner(Source<byte[]> banner) {
    this.banner = banner;
  }

  public Float getRating() {
    return rating;
  }

  public void setRating(Float rating) {
    this.rating = rating;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }

  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  public int getRuntime() {
    return runtime;
  }

  public void setRuntime(int runtime) {
    this.runtime = runtime;
  }

  public Integer getSeason() {
    return season;
  }

  public void setSeason(Integer season) {
    this.season = season;
  }

  public Integer getEpisode() {
    return episode;
  }

  public void setEpisode(Integer episode) {
    this.episode = episode;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public Integer getReleaseYear() {
    return releaseYear;
  }

  public void setReleaseYear(Integer releaseYear) {
    this.releaseYear = releaseYear;
  }

  public String[] getGenres() {
    return genres;
  }

  public void setGenres(String[] genres) {
    this.genres = genres == null ? new String[] {} : genres;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getTagline() {
    return tagline;
  }

  public void setTagline(String tagline) {
    this.tagline = tagline;
  }

  @Override
  public String toString() {
    return "('" + title + "', Item[id=" + id + ", subtitle=" + subtitle + ", identifier=" + identifier + "])";
  }
}

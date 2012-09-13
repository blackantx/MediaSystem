package hs.mediasystem.framework;

import hs.mediasystem.entity.Entity;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public class Casting extends Entity<Casting> {
  public final StringProperty role = string();
  public final StringProperty characterName = string();
  public final IntegerProperty index = integer();

  public final ObjectProperty<Person> person = object("person");
  public final ObjectProperty<Media<?>> media = object("media");
}
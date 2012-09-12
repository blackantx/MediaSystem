package hs.mediasystem.screens;

import hs.mediasystem.dao.MediaData;
import hs.mediasystem.entity.SimpleEntityProperty;
import hs.mediasystem.framework.Media;
import hs.mediasystem.framework.MediaItem;
import hs.mediasystem.framework.MediaRoot;

import java.util.Collections;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

public class MediaNode {
  public final SimpleEntityProperty<MediaData> mediaData;

  private MediaItem mediaItem;

  public MediaItem getMediaItem() { return mediaItem; }

  private final ObjectProperty<ObservableMap<Class<?>, Object>> dataMap;
  public ObjectProperty<ObservableMap<Class<?>, Object>> dataMapProperty() { return dataMap; }

  private final String id;
  private final String shortTitle;
  private final boolean isLeaf;

  private MediaNode parent;
  private List<MediaNode> children;
  private boolean showTopLevelExpanded;

  public MediaNode(MediaItem mediaItem) {
    assert mediaItem != null;

    this.id = mediaItem.getId();
    this.mediaItem = mediaItem;
    this.mediaData = mediaItem.mediaData;
    this.dataMap = mediaItem.dataMapProperty();

    this.shortTitle = "";
    this.isLeaf = true;
    this.dataType = mediaItem.getMedia().getClass();
    this.showTopLevelExpanded = false;
  }

  public MediaNode(String id, Media<?> media, String shortTitle) {
    assert id != null;
    assert !id.contains("/");
    assert !id.contains(":");

    ObservableMap<Class<?>, Object> data = FXCollections.observableHashMap();
    this.dataMap = new SimpleObjectProperty<>(data);

    data.put(Media.class, media);

    this.id = id;
    this.mediaData = new SimpleEntityProperty<>(this, "mediaData");
    this.showTopLevelExpanded = false;
    this.shortTitle = shortTitle == null ? media.title.get() : shortTitle;

    this.isLeaf = false;
    this.dataType = Media.class;
  }

  private MediaRoot mediaRoot;
  private final Class<?> dataType;
  private Callback<MediaRoot, List<MediaNode>> childrenCallback;

  public MediaNode(MediaRoot mediaRoot, boolean showTopLevelExpanded, Callback<MediaRoot, List<MediaNode>> childrenCallback) {
    this(mediaRoot.getId(), new Media<>(mediaRoot.getRootName()), null);

    this.mediaRoot = mediaRoot;
    this.showTopLevelExpanded = showTopLevelExpanded;
    this.childrenCallback = childrenCallback;
  }

  public MediaRoot getMediaRoot() {
    return mediaRoot;
  }

  public Class<?> getDataType() {
    return dataType;
  }

  public String getId() {
    return id;
  }

  public MediaNode getParent() {
    return parent;
  }

  public String getShortTitle() {
    return shortTitle;
  }

  public int indexOf(MediaNode child) {
    return getChildren().indexOf(child);
  }

  public void setChildren(List<MediaNode> children) {
    for(MediaNode child : children) {
      if(child.parent != null) {
        throw new IllegalStateException("cannot add child twice: " + child);
      }

      child.parent = this;
    }

    this.children = children;
  }

  public MediaNode findMediaNode(String id) {
    for(MediaNode node : getChildren()) {
      if(node.getId().equalsIgnoreCase(id)) {
        return node;
      }
      else if(!node.isLeaf()) {
        MediaNode childNode = node.findMediaNode(id);

        if(childNode != null) {
          return childNode;
        }
      }
    }

    return null;
  }

  public List<MediaNode> getChildren() {
    if(children == null) {
      if(mediaRoot == null) {
        List<MediaNode> emptyList = Collections.emptyList();
        setChildren(emptyList);
      }
      else {
        setChildren(childrenCallback.call(mediaRoot));
      }
    }

    return Collections.unmodifiableList(children);
  }

  /**
   * Returns <code>true</code> if this MediaNode is a leaf node.  Leaf nodes are either points that cause an action to be taken
   * (like playing a media) or that cause navigation to occur to a new display or layout.
   *
   * @return <code>true</code> if this MediaNode is a leaf node
   */
  public boolean isLeaf() {
    return isLeaf;
  }

  // Whether or not the top most level of the tree should be displayed as tabs
  public boolean expandTopLevel() {
    return showTopLevelExpanded;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(this == obj) {
      return true;
    }
    if(obj == null || getClass() != obj.getClass()) {
      return false;
    }

    MediaNode other = (MediaNode)obj;

    return id.equals(other.id);
  }

  public Media<?> getMedia() {
    return (Media<?>)dataMap.get().get(Media.class);
  }

  @Override
  public String toString() {
    return "MediaNode[id='" + id + "']";
  }
}

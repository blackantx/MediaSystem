package hs.mediasystem.players.vlc;

import hs.mediasystem.Controller;
import hs.mediasystem.ControllerFactory;
import hs.ui.frames.Frame;

import java.awt.Color;
import java.awt.Window;

import javax.swing.JFrame;

public class VLCControllerFactory implements ControllerFactory {

  @Override
  public Controller create() {
    Frame overlayFrame = new Frame();
    
  //  ((JFrame)overlayFrame.getContainer()).setLocationByPlatform(true);
    ((JFrame)overlayFrame.getContainer()).setAlwaysOnTop(true);
    ((JFrame)overlayFrame.getContainer()).setUndecorated(true);
    ((Window)overlayFrame.getContainer()).setBackground(new Color(0, 0, 0, 1));
    
  //  overlayFrame.visible().set(true);
  
    overlayFrame.maximized().set(true);
    overlayFrame.visible().set(true);
  
    return new Controller(new VLCPlayer(), overlayFrame); 
  }
}
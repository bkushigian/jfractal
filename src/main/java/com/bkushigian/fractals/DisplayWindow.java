package com.bkushigian.fractals;

import java.awt.*;
import javax.swing.*;

public  class DisplayWindow extends JFrame {

  private Container c;

  public DisplayWindow(){
    super("Display");
    c = this.getContentPane();
  }

  public void addPanel(JPanel p){
    c.add(p);
  }

  public void showFrame(){
    this.pack();
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}

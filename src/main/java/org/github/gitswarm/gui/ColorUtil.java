package org.github.gitswarm.gui;

import javafx.scene.paint.Color;

public class ColorUtil {

   private static double toSubColor(int value) {
      double val = 0;

      if (value > 0) {
         val = value / 255d;
      }

      return val;
   }

   public static Color toFxColor(java.awt.Color color) {
      return new Color(toSubColor(color.getRed()), toSubColor(color.getGreen()), toSubColor(color.getBlue()), toSubColor(color.getAlpha()));
   }

   public static java.awt.Color toAwtColor(Color color) {
      return new java.awt.Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
   }
}

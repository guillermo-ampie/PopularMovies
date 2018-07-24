package com.ampie_guillermo.popularmovies.utils;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import com.ampie_guillermo.popularmovies.R;

/**
 * DrawablePlaceholderSingleton: class to store the placeholders used in loading the images
 * Reference: How to make the perfect Singleton?:
 * https://medium.com/exploring-code/how-to-make-the-perfect-singleton-de6b951dfdb0
 * Reference: Singleton Design Pattern in Java
 * https://howtodoinjava.com/design-patterns/creational/singleton-design-pattern-in-java/
 */
public final class DrawablePlaceholderSingleton {

  private static volatile DrawablePlaceholderSingleton sinstance;
  private final Drawable drawablePlaceHolder;
  private final Drawable drawableErrorPlaceholder;

  private DrawablePlaceholderSingleton(final Resources resources) {

    if (sinstance != null) {
      throw new AssertionError(resources.getString(R.string.error_forbidden_call));
    }
    /**
     *   The hack with the variable "drawablePlaceholder" and "drawableErrorPlaceholder" is needed
     *   to support -vector drawables- on API level < 21 (Lollipop)
     *   Reference: https://github.com/square/picasso/issues/1109, see
     *   entry: "ncornette commented on Jun 27, 2016"
     */
    drawablePlaceHolder =
        ResourcesCompat.getDrawable(resources,
            R.drawable.ic_movie_black_237x180dp,
            null);
    drawableErrorPlaceholder =
        ResourcesCompat.getDrawable(resources,
            R.drawable.ic_broken_image_black_237x180dp,
            null);
  }

  public static DrawablePlaceholderSingleton getInstance(final Resources resources) {
    if (sinstance == null) {
      synchronized (DrawablePlaceholderSingleton.class) {
        if (sinstance == null) {
          sinstance = new DrawablePlaceholderSingleton(resources);
        }
      }
    }
    return sinstance;
  }

  public Drawable getDrawablePlaceHolder() {
    return drawablePlaceHolder;
  }

  public Drawable getDrawableErrorPlaceholder() {
    return drawableErrorPlaceholder;
  }
}

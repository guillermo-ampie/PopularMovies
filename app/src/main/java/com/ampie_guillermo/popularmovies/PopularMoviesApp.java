package com.ampie_guillermo.popularmovies;

import android.app.Application;
import com.squareup.leakcanary.LeakCanary;

public class PopularMoviesApp extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
//    setupLeakCanary();
  }

  protected void setupLeakCanary() {
//    enabledStrictMode();
    if (LeakCanary.isInAnalyzerProcess(this)) {
      // This process is dedicated to LeakCanary for heap analysis.
      // You should not init your app in this process.
      return;
    }
    LeakCanary.install(this);
  }
}

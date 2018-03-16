package com.ampie_guillermo.popularmovies.utils;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;
import com.ampie_guillermo.popularmovies.BuildConfig;
import java.text.MessageFormat;

/**
 * MyPMErrorUtils: A few method to check / notify / register error conditions
 */

public enum MyPMErrorUtils {
  ;

  // TODO: 3/15/18 Review the @UiThread annotations process
  @UiThread
  public static void showErrorMessage(String LOG_TAG, Context context, int errorResId,
      Exception exception) {
    String errorMessage = MessageFormat.format("{0}: {1}",
        context.getString(errorResId),
        exception.getMessage());

    // Show & log error message
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    Log.e(LOG_TAG, errorMessage);

  }

  @UiThread
  public static void showErrorMessage(String LOG_TAG, Context context, int errorResId,
      String errorCondition) {
    String errorMessage = MessageFormat.format("{0}: {1}",
        context.getString(errorResId),
        errorCondition);

    // Show & log error message
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    Log.e(LOG_TAG, errorMessage);

  }

  @UiThread
  public static void showErrorMessage(String LOG_TAG, Context context, int errorResId) {
    String errorMessage = MessageFormat.format("{0}",
        context.getString(errorResId));

    // Show & log error message
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    Log.e(LOG_TAG, errorMessage);

  }

  public static void validateIndexInCollection(int index, int size) {
    // The collection must contain at least one element
    if (BuildConfig.DEBUG) {
      if (!((size >= 1) && ((index >= 0) && (index <= (size - 1))))) {
        // TODO: 3/15/18 -- Include a detailed error message?. Are we reimplementing
        // -- IndexOutOfBoundException?"
        throw new AssertionError();
      }
    }
  }
}

package com.ampie_guillermo.popularmovies.utils;

import android.content.Context;
import android.support.annotation.UiThread;
import android.util.Log;
import android.widget.Toast;
import com.ampie_guillermo.popularmovies.BuildConfig;
import java.text.MessageFormat;

/**
 * MyPMErrorUtils: A few method to check / notify / register error conditions.
 */

public enum MyPMErrorUtils {
  ;

  public static String logErrorMessage(final String logTag,
      final Context context,
      final int errorResId,
      final String exceptionMessage) {

    final String errorMessage =
        buildErrorMessage(context.getString(errorResId), exceptionMessage);

    Log.e(logTag, errorMessage);
    return errorMessage;
  }

  // TODO: 3/15/18 Review the @UiThread annotations process

  @UiThread
  public static void showErrorMessage(final String logTag,
      final Context context,
      final int errorResId,
      final String errorConditionMessage) {

    final String errorMessage =
        logErrorMessage(logTag, context, errorResId, errorConditionMessage);

    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
  }

  @UiThread
  public static void showErrorMessage(final String logTag, final Context context, final int errorResId) {
    final String errorMessage = MessageFormat.format("{0}",
        context.getString(errorResId));

    // Show & log error message
    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
    Log.e(logTag, errorMessage);
  }

  public static void validateIndexInCollection(final int index, final int size) {
    // The collection must contain at least one element
    if (BuildConfig.DEBUG) {
      if (!((size >= 1) && ((index >= 0) && (index <= (size - 1))))) {
        // TODO: 3/15/18 -- Include a detailed error message?. Are we reimplementing
        // -- IndexOutOfBoundException?"
        throw new AssertionError();
      }
    }
  }

  private static String buildErrorMessage(final String ErrorMessage,
      final String ExceptionMessage) {
    return MessageFormat.format("{0}: {1}", ErrorMessage, ExceptionMessage);
  }
}

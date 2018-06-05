package com.ampie_guillermo.popularmovies.utils;

/**
 * UiErrorHelper: A super simple class to store an error that will be shown in the UI.
 */

public class UiErrorHelper {


  private Boolean mErrorEnabled;
  private Boolean mExceptionErrorConditionEnabled;
  private int mErrorMsgResId;
  private String mExceptionErrorMsg;

  public UiErrorHelper() {
    mErrorEnabled = Boolean.FALSE;
    mExceptionErrorConditionEnabled = Boolean.FALSE;
    mErrorMsgResId = -1;
    mExceptionErrorMsg = "";
  }

  public UiErrorHelper(UiErrorHelper errorHelper) {
    mErrorEnabled = errorHelper.mErrorEnabled;
    mExceptionErrorConditionEnabled = errorHelper.mExceptionErrorConditionEnabled;
    mErrorMsgResId = errorHelper.mErrorMsgResId;
    mExceptionErrorMsg = errorHelper.mExceptionErrorMsg;
  }

  public UiErrorHelper(int errorMsgResId) {
    mErrorEnabled = Boolean.TRUE;
    mExceptionErrorConditionEnabled = Boolean.FALSE;
    mErrorMsgResId = errorMsgResId;
    mExceptionErrorMsg = "";
  }

  public UiErrorHelper(int errorMsgResId, String exceptionErrorMsg) {
    mErrorEnabled = Boolean.TRUE;
    mExceptionErrorConditionEnabled = Boolean.TRUE;
    mErrorMsgResId = errorMsgResId;
    mExceptionErrorMsg = exceptionErrorMsg;
  }

  public void setErrorResId(int errorMsgResId) {
    mErrorEnabled = Boolean.TRUE;
    mErrorMsgResId = errorMsgResId;
  }

  public void setExceptionErrorMsg(int errorMsgResId, String exceptionErrorMsg) {
    setErrorResId(errorMsgResId);
    mExceptionErrorConditionEnabled = Boolean.TRUE;
    mExceptionErrorMsg = exceptionErrorMsg;
  }

  public void clear() {
    mErrorEnabled = Boolean.FALSE;
    mExceptionErrorConditionEnabled = Boolean.FALSE;
    mErrorMsgResId = -1;
    mExceptionErrorMsg = "";
  }

  public boolean isErrorEnabled() {
    return mErrorEnabled.booleanValue();
  }

  public boolean isExceptionErrorConditionEnabled() {
    return mExceptionErrorConditionEnabled.booleanValue();
  }

  public int getErrorMsgResId() {
    return mErrorMsgResId;
  }

  public String getExceptionErrorMsg() {
    return mExceptionErrorMsg;
  }
}

package com.ampie_guillermo.popularmovies.utils;

/**
 * UiErrorHelper: A super simple class to store an error that will be shown in the UI.
 */

public class UiErrorHelper {


  private Boolean errorEnabled;
  private Boolean exceptionErrorConditionEnabled;
  private int errorMsgResId;
  private String exceptionErrorMsg;

  public UiErrorHelper() {
    errorEnabled = Boolean.FALSE;
    exceptionErrorConditionEnabled = Boolean.FALSE;
    errorMsgResId = -1;
    exceptionErrorMsg = "";
  }

  public UiErrorHelper(UiErrorHelper errorHelper) {
    errorEnabled = errorHelper.errorEnabled;
    exceptionErrorConditionEnabled = errorHelper.exceptionErrorConditionEnabled;
    errorMsgResId = errorHelper.errorMsgResId;
    exceptionErrorMsg = errorHelper.exceptionErrorMsg;
  }

  public UiErrorHelper(int errorMsgResId) {
    errorEnabled = Boolean.TRUE;
    exceptionErrorConditionEnabled = Boolean.FALSE;
    this.errorMsgResId = errorMsgResId;
    exceptionErrorMsg = "";
  }

  public UiErrorHelper(int errorMsgResId, String exceptionErrorMsg) {
    errorEnabled = Boolean.TRUE;
    exceptionErrorConditionEnabled = Boolean.TRUE;
    this.errorMsgResId = errorMsgResId;
    this.exceptionErrorMsg = exceptionErrorMsg;
  }

  public void setErrorResId(int errorMsgResId) {
    errorEnabled = Boolean.TRUE;
    this.errorMsgResId = errorMsgResId;
  }

  public void setExceptionErrorMsg(int errorMsgResId, String exceptionErrorMsg) {
    setErrorResId(errorMsgResId);
    exceptionErrorConditionEnabled = Boolean.TRUE;
    this.exceptionErrorMsg = exceptionErrorMsg;
  }

  public void clear() {
    errorEnabled = Boolean.FALSE;
    exceptionErrorConditionEnabled = Boolean.FALSE;
    errorMsgResId = -1;
    exceptionErrorMsg = "";
  }

  public boolean isErrorEnabled() {
    return errorEnabled.booleanValue();
  }

  public boolean isExceptionErrorConditionEnabled() {
    return exceptionErrorConditionEnabled.booleanValue();
  }

  public int getErrorMsgResId() {
    return errorMsgResId;
  }

  public String getExceptionErrorMsg() {
    return exceptionErrorMsg;
  }
}

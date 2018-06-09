package com.ampie_guillermo.popularmovies.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import com.sdsmdg.harjot.vectormaster.VectorMasterView;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import java.util.Objects;

/**
 * VectorAnimationSelectWithPath: this class is based on "Example 3 (Simple color animation
 * using ValueAnimator)" from library "VectorMaster"
 * Url: https://github.com/harjot-oberai/VectorMaster
 */
public class VectorAnimationSelectWithPath {

  private final VectorMasterView mVectorMasterView;
  private final PathModel mOutline;
  private final int mStartColor;
  private final int mEndColor;
  private boolean mIsSelected;
  private OnSelectedEventListener mListener;

  public VectorAnimationSelectWithPath(final VectorMasterView vector,
      final String path,
      final int startColor,
      final int endColor) {
    mVectorMasterView = vector;
    mOutline = mVectorMasterView.getPathModelByName(path);
    mIsSelected = false;
    mStartColor = startColor;
    mEndColor = endColor;
  }

  public void setStrokeColor(int strokeColor) {
    mOutline.setStrokeColor(strokeColor);
  }

  public void setSelected(final boolean isSelected) {
    mIsSelected = isSelected;
    setupAnimation(0L);
  }

  private void setupAnimation(final long duration) {
    // initialize valueAnimator and pass start and end color values
    final ValueAnimator valueAnimator;
    if (mIsSelected) {
      valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mStartColor, mEndColor);
    } else {
      valueAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), mEndColor, mStartColor);
    }
    valueAnimator.setDuration(duration);

    valueAnimator.addUpdateListener(valueAnimator1 -> {
      // set fill color and update view
      mOutline.setFillColor((Integer) valueAnimator1.getAnimatedValue());
      mVectorMasterView.update();
    });

    valueAnimator.start();
  }

  public void registerOnSelectedEventListener(@NonNull final OnSelectedEventListener listener) {
    mListener = listener;
  }

  public void startAnimation(final long duration) {
    mVectorMasterView.setOnClickListener(view -> {
      mIsSelected = !mIsSelected; // View has just been clicked, so toggle state!
      setupAnimation(duration);
      // Better safe than sorry: already checked for @NonNull in registerOnSelectedEventListener()
      Objects.requireNonNull(mListener).onSelected(mIsSelected);
    });
  }

  @FunctionalInterface
  public interface OnSelectedEventListener {

    void onSelected(boolean isSelected);
  }
}

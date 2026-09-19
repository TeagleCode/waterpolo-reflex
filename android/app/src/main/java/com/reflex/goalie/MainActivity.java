package com.reflex.goalie;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * A single fullscreen WebView hosting the drill. Nothing is loaded from the network -
 * the whole app ships in assets/www - so the APK needs no permissions.
 */
public class MainActivity extends Activity {

  private WebView web;

  @Override
  protected void onCreate(Bundle state) {
    super.onCreate(state);

    // A goalie can't tap the screen to keep it alive mid-drill.
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    web = new WebView(this);
    WebSettings s = web.getSettings();
    s.setJavaScriptEnabled(true);
    s.setDomStorageEnabled(true);                   // settings + session history
    s.setMediaPlaybackRequiresUserGesture(false);   // the beep cue
    s.setAllowFileAccess(true);

    web.setOverScrollMode(View.OVER_SCROLL_NEVER);  // no blue glow when you tap near an edge
    web.setBackgroundColor(0xFF072B4A);
    web.setLongClickable(false);
    web.setHapticFeedbackEnabled(false);
    web.loadUrl("file:///android_asset/www/index.html");

    setContentView(web);
    goImmersive();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) goImmersive();
  }

  private void goImmersive() {
    getWindow().getDecorView().setSystemUiVisibility(
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
      | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
      | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
      | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
      | View.SYSTEM_UI_FLAG_FULLSCREEN
      | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
  }

  /** Back stops a running drill or returns to the home screen before it exits the app. */
  @Override
  @SuppressWarnings("deprecation")
  public void onBackPressed() {
    web.evaluateJavascript("window.reflexBack ? window.reflexBack() : false", value -> {
      if (!"true".equals(value)) finish();
    });
  }
}

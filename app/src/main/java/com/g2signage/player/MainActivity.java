package com.g2signage.player;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Activity principal — abre um WebView em tela cheia (modo quiosque)
 * carregando a URL do player. Sem login, sem menu, direto no conteúdo.
 */
public class MainActivity extends Activity {

    private WebView webView;
    private final Handler retryHandler = new Handler();
    private boolean isShowingOffline = false;
    private boolean triedCacheFallback = false;

    private static final int RETRY_DELAY_MS = 10000; // 10s

    private final Runnable retryRunnable = new Runnable() {
        @Override
        public void run() {
            if (isShowingOffline) {
                webView.loadUrl(getString(R.string.player_url));
                retryHandler.postDelayed(this, RETRY_DELAY_MS);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Mantém a tela sempre acesa
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);        // localStorage — essencial para o player_id
        settings.setDatabaseEnabled(true);
        settings.setMediaPlaybackRequiresUserGesture(false); // autoplay de vídeo
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    // Sem rede: tenta primeiro carregar o app pelo cache do WebView
                    // (a playlist e as mídias ficam salvas no dispositivo).
                    if (!triedCacheFallback) {
                        triedCacheFallback = true;
                        view.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                        view.loadUrl(getString(R.string.player_url));
                        return;
                    }
                    isShowingOffline = true;
                    view.loadUrl("file:///android_asset/offline.html");
                    retryHandler.postDelayed(retryRunnable, RETRY_DELAY_MS);
                }
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url != null && url.startsWith("http")) {
                    isShowingOffline = false;
                    triedCacheFallback = false;
                    view.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                    retryHandler.removeCallbacks(retryRunnable);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl(getString(R.string.player_url));
    }

    /** Modo imersivo — esconde status bar e navegação. */
    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Impede o botão de voltar de fechar o app (modo quiosque)
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
        hideSystemUI();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onDestroy() {
        retryHandler.removeCallbacks(retryRunnable);
        webView.destroy();
        super.onDestroy();
    }
}
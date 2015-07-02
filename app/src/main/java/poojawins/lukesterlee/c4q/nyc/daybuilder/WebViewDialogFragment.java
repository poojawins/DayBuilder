package poojawins.lukesterlee.c4q.nyc.daybuilder;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Luke on 6/30/2015.
 */
public class WebViewDialogFragment extends DialogFragment {

    private View mDialogView;
    private String mUrl;
    private TextView mTextView;
    private WebView mWebView;
    private Button mButton;

    private static final String ARGUMENT_URL_KEY = "url";


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle argument = getArguments();
        mUrl = argument.getString(ARGUMENT_URL_KEY);


        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        mDialogView = inflater.inflate(R.layout.dialog_webview, null);

        final ProgressBar progressBar = (ProgressBar) mDialogView.findViewById(R.id.progressBar);
        progressBar.setMax(100);


        mTextView = (TextView) mDialogView.findViewById(R.id.textView_webView_title);
        mWebView = (WebView) mDialogView.findViewById(R.id.webView);
        mWebView.setHorizontalScrollBarEnabled(false);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                mTextView.setText(title);
            }
        });

        mWebView.loadUrl(mUrl);
        mButton = (Button) mDialogView.findViewById(R.id.button_webView_close);
        builder.setCancelable(true);
        builder.setView(mDialogView);

        return builder.create();

    }

    @Override
    public void onResume() {
        super.onResume();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}

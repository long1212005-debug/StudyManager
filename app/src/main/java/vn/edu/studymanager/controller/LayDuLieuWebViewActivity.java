package vn.edu.studymanager.controller;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import vn.edu.studymanager.R;

public class LayDuLieuWebViewActivity extends AppCompatActivity {

    private WebView webView;
    private Button btnScrapeData;
    private ProgressBar progressBar;

    private static final String TAG = "WEBVIEW_SCRAPER";
    private static final String HAUI_STUDY_RESULT_URL = "https://sv.haui.edu.vn/student/result/studyresults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lay_du_lieu_web_view);

        webView = findViewById(R.id.webView);
        btnScrapeData = findViewById(R.id.btnScrapeData);
        progressBar = findViewById(R.id.progressBar);

        setupWebView();

        btnScrapeData.setOnClickListener(v -> injectScraperScript());
    }

    private void setupWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);           // Bắt buộc để chạy JS
        settings.setDomStorageEnabled(true);          // Lưu session đăng nhập của web
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        // Đăng ký JavaScript Interface làm cầu nối giữa JS và Java
        webView.addJavascriptInterface(new WebDataBridge(), "AndroidBridge");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                Log.d(TAG, "Đã tải xong URL: " + url);
            }
        });

        // Tải trang đăng nhập / kết quả học tập HaUI
        webView.loadUrl(HAUI_STUDY_RESULT_URL);
    }

    /**
     * Tiêm mã JS bóc tách DOM trang bảng điểm HaUI
     * Cấu trúc HTML của HaUI: Các dòng dữ liệu nằm trong thẻ <tr class="kTableRow"> hoặc <tr class="kTableAltRow">
     * Cột 1: Tên môn, Cột 4, 5, 6: Điểm TX1, TX2, TX3
     */
    private void injectScraperScript() {
        String jsScript = "javascript:(function() { " +
                "   var rows = document.querySelectorAll('tr.kTableRow, tr.kTableAltRow'); " +
                "   var results = []; " +
                "   rows.forEach(function(row) { " +
                "       var cols = row.querySelectorAll('td'); " +
                "       if (cols.length >= 7) { " +
                "           var item = { " +
                "               tenMon: cols[1].innerText.trim(), " +
                "               tx1: cols[4].innerText.replace(/\\u00a0/g, '').trim(), " +
                "               tx2: cols[5].innerText.replace(/\\u00a0/g, '').trim(), " +
                "               tx3: cols[6].innerText.replace(/\\u00a0/g, '').trim() " +
                "           }; " +
                "           if (item.tenMon !== '') { " +
                "               results.push(item); " +
                "           } " +
                "       } " +
                "   }); " +
                "   AndroidBridge.onDataReceived(JSON.stringify(results)); " +
                "})();";

        webView.loadUrl(jsScript);
    }

    /**
     * Lớp cầu nối JavaScript Bridge nhận dữ liệu trả về từ WebView
     */
    public class WebDataBridge {
        @JavascriptInterface
        public void onDataReceived(String jsonData) {
            Log.d(TAG, "Dữ liệu JSON thô nhận được: " + jsonData);

            // Vì hàm này chạy ở luồng phụ (JavaBridge thread), cần post về UI Thread để hiển thị
            runOnUiThread(() -> {
                try {
                    JSONArray array = new JSONArray(jsonData);
                    if (array.length() == 0) {
                        Toast.makeText(LayDuLieuWebViewActivity.this,
                                "Chưa tìm thấy bảng điểm! Hãy chắc chắn bạn đã đăng nhập và vào đúng trang xem điểm.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    StringBuilder builder = new StringBuilder();
                    builder.append("Bóc tách thành công ").append(array.length()).append(" môn học:\n\n");

                    // Lấy thử 5 môn đầu tiên hiển thị lên Dialog
                    int previewCount = Math.min(array.length(), 5);
                    for (int i = 0; i < previewCount; i++) {
                        JSONObject obj = array.getJSONObject(i);
                        builder.append("• ").append(obj.getString("tenMon"))
                                .append("\n  - TX1: ").append(obj.optString("tx1", "-"))
                                .append(" | TX2: ").append(obj.optString("tx2", "-"))
                                .append(" | TX3: ").append(obj.optString("tx3", "-"))
                                .append("\n\n");
                    }
                    if (array.length() > 5) {
                        builder.append("... và còn ").append(array.length() - 5).append(" môn khác.");
                    }

                    // Hiển thị Dialog minh chứng kết quả
                    new AlertDialog.Builder(LayDuLieuWebViewActivity.this)
                            .setTitle("KẾT QUẢ BÓC TÁCH DOM THÀNH CÔNG")
                            .setMessage(builder.toString())
                            .setPositiveButton("Đóng", null)
                            .show();

                } catch (Exception e) {
                    Log.e(TAG, "Lỗi phân tích JSON: " + e.getMessage());
                    Toast.makeText(LayDuLieuWebViewActivity.this, "Lỗi xử lý dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
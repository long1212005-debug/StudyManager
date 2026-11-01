package vn.edu.studymanager.controller;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.studymanager.R;

public class MainActivity extends AppCompatActivity {

    private Button btnOpenWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Khởi tạo ánh xạ View
        initViews();

        //Đăng ký các sự kiện tương tác
        setupEvents();
    }

    /**
     * Ánh xạ các thành phần từ XML vào Java
     */
    private void initViews() {
        btnOpenWebView = findViewById(R.id.btnOpenWebView);
    }

    /**
     * Cài đặt sự kiện click cho các nút
     */
    private void setupEvents() {
        btnOpenWebView.setOnClickListener(v -> {
            // Thông báo ngắn để xác nhận click
            Toast.makeText(this, "Đang khởi tạo WebView HaUI...", Toast.LENGTH_SHORT).show();

            // Khởi tạo Intent chuyển hướng từ MainActivity sang LayDuLieuWebViewActivity
            Intent intent = new Intent(MainActivity.this, LayDuLieuWebViewActivity.class);
            startActivity(intent);
        });
    }
}
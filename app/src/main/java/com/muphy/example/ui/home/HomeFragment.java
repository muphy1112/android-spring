package com.muphy.example.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.muphy.example.R;
import com.muphy.example.databinding.FragmentHomeBinding;

import me.muphy.spring.common.Constants;
import me.muphy.spring.util.EnvironmentUtils;
import me.muphy.spring.util.ValidateUtils;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfigHomeFragment.
     */
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });
        binding.imageViewControlSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = binding.editTextTextControlUrl.getEditableText().toString();
                if (ValidateUtils.isUrl(url)) {
                    binding.webView.loadUrl(url);
                } else {
                    binding.webView.loadUrl("http://127.0.0.1:8080/404.html");
                }
                Toast.makeText(v.getContext(), "正在加载...", Toast.LENGTH_LONG).show();
            }
        });
        String port = EnvironmentUtils.getPropertyWithDefaultValue(Constants.SERVER_PORT, "8080");
        String url = "http://127.0.0.1:" + port + "/test/urls";
        binding.editTextTextControlUrl.setText(url);
        binding.webView.loadUrl(url);

        Handler handler = new MyHandler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //String url = binding.editTextTextControlUrl.getEditableText().toString();
                String url = "http://127.0.0.1:" + port + "/test/log?timeout=30000";
                binding.editTextTextControlUrl.setText(url);
                binding.webView.loadUrl(url);
            }
        }, 8000);//3秒后执行Runnable中的run方法
    }

    public class MyHandler extends Handler {
        public MyHandler() {

        }
    }
}
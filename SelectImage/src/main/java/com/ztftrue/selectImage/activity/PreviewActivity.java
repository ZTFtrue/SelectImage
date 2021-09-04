package com.ztftrue.selectImage.activity;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ztftrue.selectImage.R;
import com.ztftrue.selectImage.fragment.PreviewFragment;

import java.util.ArrayList;

public class PreviewActivity extends AppCompatActivity {
    public final static String IMAGE_URL = "IMAGE_URL";
    public final static String IMAGE_POSITION = "IMAGE_POSITION";
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);
        arrayList = getIntent().getStringArrayListExtra(IMAGE_URL);
        int p = getIntent().getIntExtra(IMAGE_POSITION, 0);
        frameLayout.setId(R.id.select_image_photo_frame_layout);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_image_photo_frame_layout, PreviewFragment.newInstance(arrayList, true, p, null, null))
                    .commitNow();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
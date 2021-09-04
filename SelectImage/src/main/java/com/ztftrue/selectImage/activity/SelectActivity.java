package com.ztftrue.selectImage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.ztftrue.selectImage.R;
import com.ztftrue.selectImage.UserEventListener;
import com.ztftrue.selectImage.fragment.SelectFragment;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity implements UserEventListener {
    SelectFragment selectFragment;
    public final static int SELECT_RESULT_CODE = 1;
    public final static String SELECT_RESULT_NAME = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FrameLayout frameLayout = new FrameLayout(this);
        frameLayout.setLayoutParams(new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT));
        setContentView(frameLayout);
        frameLayout.setId(R.id.select_image_photo_frame_layout);
        if (savedInstanceState == null) {
            selectFragment = SelectFragment.newInstance(3, true, true, null);
            selectFragment.setUserEventListener(this);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.select_image_photo_frame_layout, selectFragment)
                    .commitNow();
        }
    }

    @Override
    public void onUserEventListener(View view) {
        if (view.getId() != R.id.select_image_add_photo) {
            Intent intent = new Intent(SelectActivity.this, PreviewActivity.class);
            intent.putExtra(PreviewActivity.IMAGE_URL, selectFragment.getImagePaths());
            intent.putExtra(PreviewActivity.IMAGE_POSITION, (int) view.getTag());
            startActivity(intent);
        }
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent();
        intent.putStringArrayListExtra(SELECT_RESULT_NAME, selectFragment.getImagePaths());
        setResult(SELECT_RESULT_CODE, intent);
    }

    @Override
    public void removeImage(int position) {

    }

    @Override
    public void onImagePathsChange(ArrayList<String> stringArrayList, int position) {

    }
}
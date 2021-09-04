package com.ztftrue.selectImage;

import android.view.View;

import java.util.ArrayList;

public interface UserEventListener {
    void onUserEventListener(View view);

    void removeImage(int position);

    void onImagePathsChange(ArrayList<String> stringArrayList, int position);

}

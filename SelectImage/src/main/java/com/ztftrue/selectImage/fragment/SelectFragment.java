package com.ztftrue.selectImage.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ztftrue.selectImage.ErrorCode;
import com.ztftrue.selectImage.R;
import com.ztftrue.selectImage.UserEventListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SelectFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SelectFragment extends Fragment implements UserEventListener {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_COLS = "ARG_COLS";
    private static final String ARG_CAN_DELETE = "ARG_CAN_DELETE";
    private static final String ARG_TAKE_PHOTO = "ARG_TAKE_PHOTO";

    public final static int REQUEST_CAMERA_CODE = 1;
    public final static int FILE_CHOOSER_RESULT_CODE = 2;
    public final static int CAMERA_IMAGE_REQUEST = 3;
    private final ArrayList<String> realPaths = new ArrayList<>();
    ImageItemRecyclerViewAdapter imageItemRecyclerViewAdapter;
    String[] items;
    private int cols = 3;
    private boolean takePhoto = true;
    private boolean canDelete = true;
    Activity activity;
    ErrorEventListener errorEventListener;
    UserEventListener userEventListener;

    public SelectFragment() {
        // Required empty public constructor
    }

    public void updateRealPaths(ArrayList<String> paths) {
        realPaths.addAll(paths);
        imageItemRecyclerViewAdapter.notifyDataSetChanged();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param cols      Parameter .
     * @param canDelete Parameter .
     * @param takePhoto can take photo
     * @return A new instance of fragment BlankFragment.
     */
    public static SelectFragment newInstance(int cols, boolean canDelete, boolean takePhoto, ErrorEventListener errorEventListener) {
        SelectFragment fragment = new SelectFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLS, cols);
        args.putBoolean(ARG_CAN_DELETE, canDelete);
        args.putBoolean(ARG_TAKE_PHOTO, takePhoto);
        fragment.setArguments(args);
        fragment.setErrorEventListener(errorEventListener);
        return fragment;
    }

    public void setErrorEventListener(ErrorEventListener errorEventListener) {
        this.errorEventListener = errorEventListener;
    }

    public void setUserEventListener(UserEventListener userEventListener) {
        this.userEventListener = userEventListener;
    }

    public ArrayList<String> getImagePaths() {
        return realPaths;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cols = getArguments().getInt(ARG_COLS, 3);
            canDelete = getArguments().getBoolean(ARG_CAN_DELETE, true);
            takePhoto = getArguments().getBoolean(ARG_TAKE_PHOTO, true);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(activity);
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        recyclerView.setLayoutManager(new GridLayoutManager(activity, cols));
        imageItemRecyclerViewAdapter = new ImageItemRecyclerViewAdapter(realPaths, this, canDelete, true);
        recyclerView.setAdapter(imageItemRecyclerViewAdapter);
        ((ViewGroup) view).addView(recyclerView);
        PackageManager pm = activity.getPackageManager();
        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) && takePhoto) {
            items = new String[]{getResources().getString(R.string.select_image_select_from_storage), getResources().getString(R.string.select_image_take_photo),};
        } else {
            items = new String[]{getResources().getString(R.string.select_image_select_from_storage)};
        }
    }

    public void openDialogForSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle("")
                .setItems(items, (dialogInterface, i) -> {
                    switch (i) {
                        case 0:
                            dialogInterface.dismiss();
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            intent.setType("image/*");//图片上传
                            //        i.setType("file/*");//文件上传
                            //        i.setType("*/*");//文件上传
                            startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILE_CHOOSER_RESULT_CODE);
                            break;
                        case 1:
                            dialogInterface.dismiss();
                            takePhoto();
                            break;
                    }
                }).setNegativeButton(android.R.string.cancel, (dialog, which) -> {

                });
        builder.create().show();
    }

    private void takePhoto() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
            return;
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, CAMERA_IMAGE_REQUEST);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            // display error state to the user
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePhoto();
            } else {
                sendError(ErrorCode.NO_CAMERA_PERMISSION);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> imagesEncodedList = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case CAMERA_IMAGE_REQUEST: {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    saveBitmap(photo);
                    photo.recycle();
                    break;
                }
                case FILE_CHOOSER_RESULT_CODE: {
                    if (data.getData() != null) {
                        Uri result = data.getData();
                        String dataString = result == null ? data.getDataString() : result.toString();
                        if (!TextUtils.isEmpty(dataString)) {
                            imagesEncodedList.clear();
                            imagesEncodedList.add(dataString);
                            dealFile(imagesEncodedList);
                        }
                    } else if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            imagesEncodedList.add(uri.toString());
                        }
                        dealFile(imagesEncodedList);
                    }
                    break;
                }
            }
        }
    }

    private void sendError(int errorCode) {
        if (this.errorEventListener != null) {
            this.errorEventListener.OnErrorEventListener(errorCode);
        }
    }

    private void saveBitmap(Bitmap bmp) {
        String fileName = System.currentTimeMillis() + ".png";
        String download = activity.getExternalCacheDir().toString() + File.separator + fileName;
        try (FileOutputStream out = new FileOutputStream(download)) {
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            realPaths.add(download);
            imageItemRecyclerViewAdapter.notifyDataSetChanged();
            if (this.userEventListener != null) {
                this.userEventListener.onImagePathsChange(realPaths, realPaths.size() - 1);
            }
        } catch (IOException e) {
            sendError(ErrorCode.SAVE_FILE_EXCEPTION);
            e.printStackTrace();
        }
    }

    private void dealFile(ArrayList<String> stringArrayList) {
        int i = 0;
        int firstPosition = realPaths.size();
        for (String path : stringArrayList) {
            try {
                String fileName = String.valueOf(System.currentTimeMillis()) + i + ".png";
                i++;
                realPaths.add(initFile(path, fileName));
                imageItemRecyclerViewAdapter.notifyDataSetChanged();
            } catch (IOException e) {
                sendError(ErrorCode.SAVE_FILE_EXCEPTION);
                e.printStackTrace();
            }
        }
        if (this.userEventListener != null) {
            this.userEventListener.onImagePathsChange(realPaths, firstPosition);
        }
    }

    private String initFile(String fileUrl, String fileName) throws IOException {
        String download = activity.getExternalCacheDir().toString() + File.separator + fileName;
        if (fileUrl.startsWith("content://")) {
            Uri uri = Uri.parse(fileUrl);
            ParcelFileDescriptor parcelFileDescriptor = activity.getContentResolver().openFileDescriptor(uri, "r");
            parcelFileDescriptor.getFileDescriptor().sync();
            FileInputStream inputStream = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
            copyFile(inputStream, download);
            inputStream.close();
        } else {
            FileInputStream fileInputStream = new FileInputStream(new File(fileUrl));
            copyFile(fileInputStream, download);
            fileInputStream.close();
        }
        return download;
    }

    private void copyFile(FileInputStream inputStream, String download) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Files.copy(inputStream, Paths.get(download));
        } else {
            try (FileOutputStream fos = new FileOutputStream(download)) {
                byte[] bytes = new byte[1024];
                int temp;
                while ((temp = inputStream.read(bytes)) != -1) {
                    fos.write(bytes, 0, temp);
                }
            }
        }
    }

    @Override
    public void onUserEventListener(View view) {
        if (view.getId() == R.id.select_image_add_photo) {
            openDialogForSelect();
        } else {
            userEventListener.onUserEventListener(view);
        }
    }

    @Override
    public void removeImage(int position) {
        new File(realPaths.get(position)).delete();
        realPaths.remove(position);
        imageItemRecyclerViewAdapter.notifyItemRemoved(position);
        imageItemRecyclerViewAdapter.notifyDataSetChanged();
        if (this.userEventListener != null) {
            this.userEventListener.onImagePathsChange(realPaths, position);
        }
    }

    @Override
    public void onImagePathsChange(ArrayList<String> stringArrayList, int position) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity = getActivity();
    }


    public interface ErrorEventListener {
        void OnErrorEventListener(int errorCode);
    }


    public void deleteImageCache() {
        for (String path : realPaths) {
            new File(path).delete();
        }
        realPaths.clear();
    }
}
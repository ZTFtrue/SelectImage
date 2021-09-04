package com.ztftrue.selectImage.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.ztftrue.selectImage.UserEventListener;

import java.util.ArrayList;

public class PreviewFragment extends Fragment {

    private static final String ARG_IMAGE_PATHS = "ARG_IMAGE_PATHS";
    private static final String ARG_CAN_CAN_DEIT = "ARG_CAN_CAN_DEIT";
    private static final String CURRENT_POSITION = "CURRENT_POSITION";

    private ArrayList<String> paths = new ArrayList<>();
    private int position = 0;
    private boolean canEdit = false;
    SelectFragment.ErrorEventListener errorEventListener;
    UserEventListener userEventListener;

    public static PreviewFragment newInstance() {
        return new PreviewFragment();
    }

    public static PreviewFragment newInstance(ArrayList<String> paths, boolean canEdit, int possition, SelectFragment.ErrorEventListener errorEventListener, UserEventListener userEventListener) {
        PreviewFragment fragment = new PreviewFragment();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_IMAGE_PATHS, paths);
        args.putBoolean(ARG_CAN_CAN_DEIT, canEdit);
        args.putInt(CURRENT_POSITION, possition);
        fragment.setArguments(args);
        fragment.setErrorEventListener(errorEventListener);
        fragment.setUserEventListener(userEventListener);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paths = getArguments().getStringArrayList(ARG_IMAGE_PATHS);
            if (paths == null) {
                paths = new ArrayList<>();
            }
            canEdit = getArguments().getBoolean(ARG_CAN_CAN_DEIT, true);
            position = getArguments().getInt(CURRENT_POSITION, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setLayoutParams(new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return frameLayout;
    }

    public void setErrorEventListener(SelectFragment.ErrorEventListener errorEventListener) {
        this.errorEventListener = errorEventListener;
    }


    public void setUserEventListener(UserEventListener userEventListener) {
        this.userEventListener = userEventListener;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = new RecyclerView(getContext());
        recyclerView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        recyclerView.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                PhotoView photo_view = new PhotoView(parent.getContext());
                photo_view.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                photo_view.setOnClickListener(v -> {
                    if (userEventListener != null) {
                        userEventListener.onUserEventListener(v);
                    }
                });
                return new RecyclerView.ViewHolder(photo_view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                Glide.with(holder.itemView.getContext()).load(paths.get(position)).into((PhotoView) holder.itemView);
            }

            @Override
            public int getItemCount() {
                return paths.size();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.scrollToPosition(position);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(recyclerView);
        ((ViewGroup) view).addView(recyclerView);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


}
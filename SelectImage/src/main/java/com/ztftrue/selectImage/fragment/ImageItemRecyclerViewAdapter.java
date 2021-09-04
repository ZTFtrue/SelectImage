package com.ztftrue.selectImage.fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ztftrue.selectImage.R;
import com.ztftrue.selectImage.UserEventListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link String}.
 */
public class ImageItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<String> mValues;
    private final UserEventListener removeImage;
    private boolean canDelete = true;
    private boolean showAdd = true;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    /**
     * none button control
     *
     * @param items         data
     * @param eventListener User event listener ,remove image,preview image
     */
    public ImageItemRecyclerViewAdapter(List<String> items, UserEventListener eventListener) {
        mValues = items;
        this.removeImage = eventListener;
    }

    /**
     *
     * @param items data
     * @param eventListener User event listener ,remove image,preview image
     * @param canDelete  can delete image , default is true
     * @param showAdd show add button
     */
    public ImageItemRecyclerViewAdapter(List<String> items, UserEventListener eventListener, boolean canDelete, boolean showAdd) {
        mValues = items;
        this.removeImage = eventListener;
        this.canDelete = canDelete;
        this.showAdd = showAdd;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycleview_image_item, parent, false);
            return new ViewHolder(view);
        } else {
            ImageView addView;
            addView = new ImageView(parent.getContext());
            addView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            addView.setId(R.id.select_image_add_photo);
            addView.setScaleType(ImageView.ScaleType.FIT_XY);
            addView.setAdjustViewBounds(true);
            addView.setImageResource(android.R.drawable.ic_menu_add);
            addView.setOnClickListener(v -> removeImage.onUserEventListener(addView));
            return new HeaderViewHolder(addView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolder) {
            ViewHolder vholder = ((ViewHolder) holder);
            vholder.mItem = getItem(position);
            Glide.with(((ViewHolder) holder).imageView).load(vholder.mItem).into(vholder.imageView);
            vholder.imageView.setOnClickListener(v -> {
                v.setTag(getValuesPosition(position));
                removeImage.onUserEventListener(v);
            });
            vholder.remove_image.setOnClickListener(v -> {
                if (removeImage != null) {
                    removeImage.removeImage(getValuesPosition(position));
                }
            });
        } else if (holder instanceof HeaderViewHolder) {
        }

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView imageView;
        public final ImageView remove_image;
        public String mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            imageView = view.findViewById(R.id.image);
            remove_image = view.findViewById(R.id.remove_image);
            if (!canDelete) {
                remove_image.setVisibility(View.GONE);
            }
        }
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {


        public HeaderViewHolder(View view) {
            super(view);
        }
    }

    @Override
    public int getItemCount() {
        if (showAdd) {
            return mValues.size() + 1;
        } else {
            return mValues.size();
        }
    }

    public int getValuesPosition(int position) {
        if (showAdd) {
            return position - 1;
        } else {
            return position;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showAdd) {
            if (isPositionHeader(position))
                return TYPE_HEADER;
            return TYPE_ITEM;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private String getItem(int position) {
        return mValues.get(getValuesPosition(position));
    }

}
package com.example.assignmentapplication;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableArrayList;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivityViewModel extends ViewModel {
    private static final int REQUEST_PERMISSION = 1;
    private static final int REQUEST_IMAGE_GALLERY = 2;
    public ObservableArrayList<TitleItem> titleItems = new ObservableArrayList<>();
    private String title;

    public void addTitle(String title) {
        TitleItem titleItem = new TitleItem(title);
        titleItems.add(titleItem);
    }


    public String getTitle() {
        return title;
    }



    public void setTitle(String title) {
        this.title = title;
    }

    public static class TitleItem {
        public String title;
        public ObservableArrayList<String> images;

        public TitleItem(String title) {
            this.title = title;
            this.images = new ObservableArrayList<>();
        }
        public String getImageAt(int position) {
            return images.get(position);
        }

    }


    public void showImageGallery(Context context, TitleItem titleItem) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((MainActivity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        } else {

            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

            PackageManager manager = context.getPackageManager();
            List<ResolveInfo> galleryApps = manager.queryIntentActivities(intent, 0);
            if (galleryApps.size() > 0) {

                ResolveInfo galleryApp = galleryApps.get(0);
                intent.setClassName(galleryApp.activityInfo.packageName, galleryApp.activityInfo.name);
                ((MainActivity) context).startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
            } else {
                Toast.makeText(context, "No gallery app found", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @BindingAdapter("app:items")
    public static void setRecyclerViewItems(RecyclerView recyclerView, ObservableArrayList<MainActivityViewModel.TitleItem> items) {

    }
}

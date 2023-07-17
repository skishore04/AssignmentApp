package com.example.assignmentapplication;


import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.assignmentapplication.databinding.ActivityMainBinding;
import com.example.assignmentapplication.databinding.ItemImageBinding;
import com.example.assignmentapplication.databinding.ItemTitleBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;
    private ActivityMainBinding binding;


    private static final int REQUEST_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        viewModel = new MainActivityViewModel();
        binding.setViewModel(viewModel);
        binding.setLifecycleOwner(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerViewTitles.setLayoutManager(layoutManager);
        TitleAdapter titleAdapter = new TitleAdapter();
        binding.recyclerViewTitles.setAdapter(titleAdapter);


    }
    public void onFabClicked(View view) {
        showAddTitleDialog();
    }
    private void showAddTitleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Title");
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_title, null);
        builder.setView(dialogView);
        final EditText editTextTitle = dialogView.findViewById(R.id.editTextTitle);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String title = editTextTitle.getText().toString();

                viewModel.addTitle(title);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.TitleViewHolder> {

        @NonNull
        @Override
        public TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemTitleBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_title, parent, false);
            return new TitleViewHolder(binding);
        }


        @Override
        public void onBindViewHolder(@NonNull TitleViewHolder holder, int position) {
            ItemTitleBinding binding = holder.binding;
            binding.setTitleItem(viewModel.titleItems.get(position));
            binding.setViewModel(viewModel);
            MainActivityViewModel.TitleItem titleItem = viewModel.titleItems.get(position);
            ImageAdapter imageAdapter = new ImageAdapter(titleItem.images);
            binding.recyclerViewImages.setAdapter(imageAdapter);

            binding.executePendingBindings();
        }


        @Override
        public int getItemCount() {
            return viewModel.titleItems.size();
        }

        public class TitleViewHolder extends RecyclerView.ViewHolder {
            ItemTitleBinding binding;

            public TitleViewHolder(ItemTitleBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
                LinearLayoutManager layoutManager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                binding.recyclerViewImages.setLayoutManager(layoutManager);

                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    ImageAdapter imageAdapter = new ImageAdapter(viewModel.titleItems.get(getAdapterPosition()).images);
                    binding.recyclerViewImages.setAdapter(imageAdapter);

                }
            }
            public void bind(MainActivityViewModel.TitleItem titleItem) {
                binding.setTitleItem(titleItem);
                binding.executePendingBindings();
            }
        }
    }

    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
        private List<String> images;

        public ImageAdapter(List<String> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemImageBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_image, parent, false);
            return new ImageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            ItemImageBinding binding = holder.binding;
            String imagePath = images.get(position);
            binding.setImagePath(imagePath);
            binding.executePendingBindings();
        }

        @Override
        public int getItemCount() {
            return images.size();
        }

        public class ImageViewHolder extends RecyclerView.ViewHolder {
            ItemImageBinding binding;

            public ImageViewHolder(ItemImageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_GALLERY) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();
                    for (int i = 0; i < count; i++) {
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        String imagePath = getImagePathFromUri(imageUri);
                        if (imagePath != null) {
                            int position = viewModel.titleItems.size() - 1;
                            viewModel.titleItems.get(position).images.add(imagePath);
                            binding.recyclerViewTitles.getAdapter().notifyItemChanged(position);
                        }
                    }
                } else if (data.getData() != null) {
                    Uri imageUri = data.getData();
                    String imagePath = getImagePathFromUri(imageUri);
                    if (imagePath != null) {
                        int position = viewModel.titleItems.size() - 1;
                        viewModel.titleItems.get(position).images.add(imagePath);
                        binding.recyclerViewTitles.getAdapter().notifyItemChanged(position);

                    }
                }
            }
        }
    }

    private String getImagePathFromUri(Uri uri) {
        String imagePath = null;
        try {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                imagePath = cursor.getString(columnIndex);
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("ImageGallery", "Error getting image path: " + e.getMessage());
        }
        return imagePath;
    }

}

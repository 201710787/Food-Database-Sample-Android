package com.food_database_sample_android;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bumptech.glide.Glide;
import com.food_database_sample_android.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> list = new ArrayList<>();

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
        RecyclerView recyclerView = findViewById(R.id.charaList) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        AtomicReference<ListAdapter> adapter = new AtomicReference<>(new ListAdapter(list));
        recyclerView.setAdapter(adapter.get()) ;

        adapter.get().setDeleteClickListener((v,pos)->{
            list.remove(pos);
            adapter.get().notifyItemRemoved(pos);
        });

        binding.imageBtn.setOnClickListener(v->{
            if (verifyPermissions()) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                launcher.launch(intent);
            }
        });
        
        binding.foodNameInput.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction()==KeyEvent.ACTION_DOWN&&i==KeyEvent.KEYCODE_ENTER) {
                binding.foodName.setText(binding.foodNameInput.getText().toString());
                binding.foodNameInput.setText("");
                imm.hideSoftInputFromWindow(binding.foodNameInput.getWindowToken(), 0);
            }
            return true;
        });
        binding.foodCharaInput.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction()==KeyEvent.ACTION_DOWN&&i==KeyEvent.KEYCODE_ENTER) {
                adapter.get().addItem(binding.foodCharaInput.getText().toString());
                binding.foodCharaInput.setText("");
                imm.hideSoftInputFromWindow(binding.foodNameInput.getWindowToken(), 0);
            }
            return true;
        });
    }

    public Boolean verifyPermissions() {
        int permissionExternalMemory = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionExternalMemory != PackageManager.PERMISSION_GRANTED) {
            String[] STORAGE_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, STORAGE_PERMISSIONS, 1);
            return false;
        }
        return true;
    }
    ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    if (result.getResultCode() == RESULT_OK)
                    {
                        Log.e("TAG", "result : " + result);
                        Intent intent = result.getData();
                        Log.e("TAG", "intent : " + intent);
                        Uri uri = intent.getData();
                        Log.e("TAG", "uri : " + uri);
                        binding.imageInputResult.setText(uri.toString());
                    }
                }
            });

}
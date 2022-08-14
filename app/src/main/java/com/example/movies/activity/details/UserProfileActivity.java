package com.example.movies.activity.details;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.movies.R;
import com.example.movies.activity.main.MainActivity;
import com.example.movies.bottomsheet.BottomSheetBirthDatePicker;
import com.example.movies.bottomsheet.BottomSheetChangeFirstLastName;
import com.example.movies.bottomsheet.BottomSheetChooseImageOptions;
import com.example.movies.databinding.ActivityUserProfileBinding;
import com.example.movies.databinding.BottomSheetChangeBirthDateBinding;
import com.example.movies.listener.changebirthdate.IOnChangeBirthDate;
import com.example.movies.listener.changename.IOnChangeDisplayName;
import com.example.movies.listener.chooseimageoption.IOnItemImageOptionClick;
import com.example.movies.model.users.UserProfile;
import com.example.movies.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity implements IOnChangeDisplayName, IOnChangeBirthDate, IOnItemImageOptionClick {

    private ActivityUserProfileBinding binding;
    private UserProfile userProfile;//,userProfileTemp;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference reference;
    private FirebaseUser user;
    private BottomSheetChangeFirstLastName bottomSheetChangeFirstLastName;
    private BottomSheetBirthDatePicker bottomSheetBirthDatePicker;
    private BottomSheetChooseImageOptions bottomSheetChooseImageOptions;

    private final ActivityResultLauncher<Intent> getImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Uri uri = Objects.requireNonNull(result.getData()).getData();
                if (uri != null) {
                    //GET URI OF IMAGE
                    try {
                        saveImageProfileUser(uri);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Bitmap bitmap = (Bitmap) result.getData().getExtras().get("data");
                        File file = new File(getApplicationContext().getCacheDir(), "temp");
                        FileOutputStream outputStream = null;
                        try {
                            outputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        try {
                            outputStream.flush();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        saveImageProfileUser(Uri.fromFile(file));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //INITIALIZED VARIABLES
        userProfile = (UserProfile) getIntent().getBundleExtra("bundle").getSerializable("user");
        //userProfileTemp = (UserProfile) getIntent().getBundleExtra("bundle").getSerializable("user");
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        reference = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        bottomSheetChangeFirstLastName = new BottomSheetChangeFirstLastName(userProfile, this);
        bottomSheetBirthDatePicker = new BottomSheetBirthDatePicker(this);
        bottomSheetChooseImageOptions = new BottomSheetChooseImageOptions(this);
        bottomSheetChangeFirstLastName.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
        bottomSheetBirthDatePicker.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
        bottomSheetChooseImageOptions.setStyle(DialogFragment.STYLE_NORMAL, R.style.changeBackgroundOfBottomSheetChangePassword);
        //CHECK CURRENT USER IS AUTHENTICATE BY WHICH PROVIDER.


        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile);
        binding.setUser(userProfile);

        //ON BUTTON CHANGE IMAGE CLICK
        binding.buttonChangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SHOW 2 OPTIONS : CHOOSE PICTURE OR TAKE A PICTURE
                bottomSheetChooseImageOptions.show(getSupportFragmentManager(), "AAA");
            }
        });


        binding.buttonChangeDisplayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetChangeFirstLastName.show(getSupportFragmentManager(), "AAA");
            }
        });

        binding.buttonChangeBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBirthDatePicker.show(getSupportFragmentManager(), "AAA");
            }
        });

    }


    //SAVE IMAGE PROFILE OF USER TO FIREBASE STORAGE
    public void saveImageProfileUser(Uri pathImage) {
        //SAVE TO STORAGE DATABASE
        userProfile.setPathImage(pathImage.toString());
        StorageReference temp = storageReference.child(user.getUid()).child("image");
        InputStream inputStream = null;
        try {
            inputStream = getContentResolver().openInputStream(pathImage);
            temp.putStream(inputStream).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    temp.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userProfile.setPathImage(uri.toString());
                            reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(user.getUid()).child("pathImage").setValue(uri.toString());
                            Toast.makeText(getApplicationContext(), R.string.change_image_success, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), R.string.change_image_fail, Toast.LENGTH_LONG).show();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void changeNameAtDatabase(UserProfile userProfile) {
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(user.getUid()).child("firstName").setValue(userProfile.getFirstName());
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(user.getUid()).child("lastName").setValue(userProfile.getLastName());
    }

    public void changeBirthdateAtDatabase(String day) {
        reference.child(Utils.FIREBASE_USERS_ACCOUNT_FOLDER).child(user.getUid()).child("birthdate").setValue(day);
    }

    @Override
    public void onChangeDisplayName(UserProfile userProfileTemp) {
        userProfile.setFirstName(userProfileTemp.getFirstName());
        userProfile.setLastName(userProfileTemp.getLastName());
        changeNameAtDatabase(userProfileTemp);
    }

    @Override
    public void onChangeBirthDate(String date) {
        userProfile.setBirthdate(date);
        changeBirthdateAtDatabase(date);
    }

    public boolean checkPermissionGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }


    public void requestPermission(String permission) {
        requestPermissions(new String[]{permission}, 1000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getImage.launch(takePictureIntent);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", userProfile);
        Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
        intent.putExtra("bundle", bundle);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    public void onItemImageOptionClick(int pos) {

        if (pos == 1) {
            //CHOOSE IMAGE FROM GALLERY
            Intent intentChooseImage = new Intent();
            intentChooseImage.setType("image/*");
            intentChooseImage.setAction(Intent.ACTION_GET_CONTENT);
            getImage.launch(intentChooseImage);
        } else if (pos == 2) {
            //GET IMAGE FROM CAMERA
            if (checkPermissionGranted(Manifest.permission.CAMERA)) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                getImage.launch(takePictureIntent);
            } else {
                requestPermission(Manifest.permission.CAMERA);
            }
        }
    }
}
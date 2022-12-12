package com.tqb.m_expense.Utils;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.tqb.m_expense.BuildConfig;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class CameraUtil {
    public interface CameraResultListener {
        void onCameraResult(Bitmap bitmap);
    }

    private ActivityResultLauncher<Intent> cameraLauncher;
    private Fragment fragment;
    private File image;
    private CameraResultListener listener;

    private CameraUtil(Fragment fragment) {
        this.fragment = fragment;
        this.cameraLauncher = fragment.registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        listener.onCameraResult(BitmapFactory.decodeFile(image.getAbsolutePath()));
                    }
                });
    }

    public static CameraUtil with(Fragment fragment) {
        return new CameraUtil(fragment);
    }

    public void takePicture(File imageFile, CameraResultListener listener) {
        PermissionUtils.checkPermission(fragment.requireContext(),
                PermissionUtils.CAMERA_PERMISSION,
                new PermissionUtils.PermissionAskListener() {
            @Override
            public void onPermissionPreviouslyDenied() {
                ActivityCompat.requestPermissions(fragment.requireActivity(),
                        new String[]{PermissionUtils.CAMERA_PERMISSION},
                        PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE);
            }

            @Override
            public void onPermissionDisabled() {
                Log.d("CameraUtil", "Camera permission is disabled.");
            }

            @Override
            public void onPermissionAsk() {
                ActivityCompat.requestPermissions(fragment.requireActivity(),
                        new String[]{PermissionUtils.CAMERA_PERMISSION},
                        PermissionUtils.CAMERA_PERMISSION_REQUEST_CODE);
            }

            @Override
            public void onPermissionGranted() {
                CameraUtil.this.image = imageFile;
                CameraUtil.this.listener = listener;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                        FileProvider.getUriForFile(fragment.requireContext(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                image));
                cameraLauncher.launch(intent);
            }
        });
    }

    public void pickFromGallery(File imageFile, CameraResultListener listener) {
        PermissionUtils.checkPermission(fragment.requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE,
                new PermissionUtils.PermissionAskListener() {
                    @Override
                    public void onPermissionAsk() {
                        ActivityCompat.requestPermissions(fragment.requireActivity(),
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                PermissionUtils.GALLERY_PERMISSION_REQUEST_CODE);
                    }

                    @Override
                    public void onPermissionPreviouslyDenied() {
                        ActivityCompat.requestPermissions(fragment.requireActivity(),
                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                PermissionUtils.GALLERY_PERMISSION_REQUEST_CODE);
                    }

                    @Override
                    public void onPermissionDisabled() {
                        Log.d("Permission", "Read external storage permission disabled");
                        // do nothing
                    }

                    @Override
                    public void onPermissionGranted() {
                        CameraUtil.this.image = imageFile;
                        CameraUtil.this.listener = listener;
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                FileProvider.getUriForFile(fragment.requireContext(),
                                        BuildConfig.APPLICATION_ID + ".provider",
                                        image));
                        cameraLauncher.launch(intent);
                    }
                });
    }

    // encode bitmap to base64 string
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    // decode base64 string to bitmap
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    // delete image file
    public static void delete(File imageFile) {
        if (imageFile.exists()) {
            if (imageFile.delete()) {
                Log.d("CameraUtil", "Image file deleted");
            } else {
                Log.d("CameraUtil", "Image file not deleted");
            }
        }
    }
}

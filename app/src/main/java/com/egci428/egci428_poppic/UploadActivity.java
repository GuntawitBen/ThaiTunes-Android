package com.egci428.egci428_poppic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

import retrofit2.Call;


public class UploadActivity extends AppCompatActivity {

    Button uploadImage;
    Button saveButton;
    EditText uploadContent;
    String imageURL;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadImage = findViewById(R.id.uploadImageBTN);
        uploadContent = findViewById(R.id.editTextContent);
        ImageView uploadedImageIV = findViewById(R.id.uploadedIV);
        saveButton = findViewById(R.id.saveBTN);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == UploadActivity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            uploadedImageIV.setImageURI(uri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No Image Selected", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

    }

    public void saveData(){
        if (uri == null || uploadContent.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please select an image and enter content!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert image to Base64
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            byte[] imageBytes = new byte[inputStream.available()];
            inputStream.read(imageBytes);
            String base64Content = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);

            // Get user input for the image content
            String content = uploadContent.getText().toString();
            String fileName = "uploaded_image_" + System.currentTimeMillis() + ".jpg";

            // Call uploadData() with the Base64 content
            uploadData(fileName, content, base64Content);

        } catch (Exception e) {
            Toast.makeText(this, "Failed to process image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadData(String fileName, String commitMessage, String base64Content) {
        GitHubService gitHubService = GitHubService.create();

        // Replace with your GitHub credentials
        String owner = "GuntawitBen";  // Replace with your GitHub username
        String repo = "EGCI428_PopPic";       // Replace with your repository name
        String path = "folder/" + filePath;  // Specify the folder and file name
        String authToken = "Bearer YOUR_PERSONAL_ACCESS_TOKEN";

        // Create GitHubFile object
        Post file = new Post(commitMessage, base64Content, "", "");

        // API call to upload the file
        gitHubService.uploadFile(owner, repo, "images/" + fileName, token, file)
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UploadActivity.this, "Image uploaded successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(UploadActivity.this, "Upload failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(UploadActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


}

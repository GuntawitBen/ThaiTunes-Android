package com.egci428.egci428_poppic

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class NewPostActivity : AppCompatActivity() {

    private lateinit var contentEditText: EditText
    private lateinit var mediaUrlEditText: EditText
    private lateinit var saveButton: Button
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_post)

        // Initialize UI elements
        contentEditText = findViewById(R.id.editTextContent)
        mediaUrlEditText = findViewById(R.id.editTextMediaUrl)
        saveButton = findViewById(R.id.btnSavePost)

        // Set up save button click listener
        saveButton.setOnClickListener {
            // Get content and media URL from EditTexts
            val content = contentEditText.text.toString().trim()
            val mediaUrl = mediaUrlEditText.text.toString().trim()

            // Validate input
            if (content.isEmpty() || mediaUrl.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a post object
            val post = Post(content, mediaUrl, System.currentTimeMillis().toString(), "userId") // Replace "userId" with the actual user ID

            // Save post to Firestore
            savePostToFirestore(post)

        }
    }

    private fun savePostToFirestore(post: Post) {
        // Reference to the Firestore collection
        db.collection("Posts")
            .add(post)
            .addOnSuccessListener {
                Toast.makeText(this, "Post saved successfully", Toast.LENGTH_SHORT).show()
                finish() // Close the activity and return to the previous screen
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error saving post: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

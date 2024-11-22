package com.egci428.egci428_poppic

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.egci428.egci428_poppic.api.RetrofitClient
import com.egci428.egci428_poppic.api.RetrofitClient.getInstance
import com.google.firebase.auth.FirebaseAuth
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginBTN: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usernameText = findViewById(R.id.username)
        passwordText = findViewById(R.id.password)
        loginBTN = findViewById(R.id.loginBTN)

        loginBTN.setOnClickListener{
            login()
        }
    }

    private fun login() {
        val username = usernameText.text.toString().trim()
        val password = passwordText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill in both username and password.", Toast.LENGTH_SHORT).show()
            return
        }

        val call: Call<ResponseBody> = RetrofitClient
            .getInstance()
            .getAPI()
            .login(username, password)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
package com.nama_anda.kasku

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnLogin: Button

    // Kredensial statis (hardcoded)
    private val VALID_USERNAME = "admin"
    private val VALID_PASSWORD = "admin123"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
    }

    private fun setupListeners() {
        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString().trim()
        val password = etPassword.text.toString().trim()

        // Validasi input kosong
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Username dan Password tidak boleh kosong",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Validasi kredensial
        if (username == VALID_USERNAME && password == VALID_PASSWORD) {
            // Login berhasil
            Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show()

            // Pindah ke Dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish() // Tutup LoginActivity agar tidak bisa kembali dengan tombol back
        } else {
            // Login gagal
            Toast.makeText(
                this,
                getString(R.string.login_failed),
                Toast.LENGTH_SHORT
            ).show()

            // Clear password untuk keamanan
            etPassword.text?.clear()
        }
    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed() {
        finishAffinity()
    }
}
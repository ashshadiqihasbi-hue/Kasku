package com.nama_anda.kasku

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.nama_anda.kasku.Database.AppDatabase
import com.nama_anda.kasku.Database.Transaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var rgTransactionType: RadioGroup
    private lateinit var rbIncome: RadioButton
    private lateinit var rbExpense: RadioButton
    private lateinit var etAmount: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var layoutDatePicker: LinearLayout
    private lateinit var tvSelectedDate: TextView
    private lateinit var btnSave: Button

    private lateinit var database: AppDatabase
    private var selectedDate: String = ""
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        initViews()
        setupDatabase()
        setupListeners()
    }

    private fun initViews() {
        rgTransactionType = findViewById(R.id.rgTransactionType)
        rbIncome = findViewById(R.id.rbIncome)
        rbExpense = findViewById(R.id.rbExpense)
        etAmount = findViewById(R.id.etAmount)
        etDescription = findViewById(R.id.etDescription)
        layoutDatePicker = findViewById(R.id.layoutDatePicker)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnSave = findViewById(R.id.btnSave)
    }

    private fun setupDatabase() {
        database = AppDatabase.getDatabase(this)
    }

    private fun setupListeners() {
        // Date picker listener
        layoutDatePicker.setOnClickListener {
            showDatePicker()
        }

        // Save button listener
        btnSave.setOnClickListener {
            saveTransaction()
        }
    }

    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                updateSelectedDate()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateSelectedDate() {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
        selectedDate = dateFormat.format(calendar.time)
        tvSelectedDate.text = selectedDate
        tvSelectedDate.setTextColor(getColor(R.color.text_primary))
    }

    private fun saveTransaction() {
        val amount = etAmount.text.toString().trim()
        val description = etDescription.text.toString().trim()

        // Validasi input
        if (!validateInput(amount, description)) {
            return
        }

        // Tentukan tipe transaksi
        val type = if (rbIncome.isChecked) {
            Transaction.TYPE_INCOME
        } else {
            Transaction.TYPE_EXPENSE
        }

        // Buat objek transaction
        val transaction = Transaction(
            type = type,
            amount = amount.toDouble(),
            description = description,
            date = selectedDate
        )

        // Simpan ke database menggunakan coroutine
        lifecycleScope.launch {
            try {
                database.transactionDao().insertTransaction(transaction)

                // Tampilkan pesan sukses
                runOnUiThread {
                    Toast.makeText(
                        this@AddTransactionActivity,
                        getString(R.string.transaction_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                // Kembali ke Dashboard
                finish()
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@AddTransactionActivity,
                        "Gagal menyimpan transaksi: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun validateInput(amount: String, description: String): Boolean {
        // Validasi amount kosong
        if (amount.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_amount), Toast.LENGTH_SHORT).show()
            etAmount.requestFocus()
            return false
        }

        // Validasi amount = 0
        if (amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
            Toast.makeText(this, getString(R.string.error_zero_amount), Toast.LENGTH_SHORT).show()
            etAmount.requestFocus()
            return false
        }

        // Validasi description kosong
        if (description.isEmpty()) {
            Toast.makeText(
                this,
                getString(R.string.error_empty_description),
                Toast.LENGTH_SHORT
            ).show()
            etDescription.requestFocus()
            return false
        }

        // Validasi tanggal belum dipilih
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, getString(R.string.error_empty_date), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}
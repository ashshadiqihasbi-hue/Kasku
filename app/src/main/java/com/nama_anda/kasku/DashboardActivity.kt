package com.nama_anda.kasku

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nama_anda.kasku.adapter.TransactionAdapter
import com.nama_anda.kasku.Database.AppDatabase
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTotalBalance: TextView
    private lateinit var tvTotalIncome: TextView
    private lateinit var tvTotalExpense: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var rvTransactions: RecyclerView
    private lateinit var fabAddTransaction: FloatingActionButton

    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        setupRecyclerView()
        setupDatabase()
        observeData()
        setupListeners()
    }

    private fun initViews() {
        tvTotalBalance = findViewById(R.id.tvTotalBalance)
        tvTotalIncome = findViewById(R.id.tvTotalIncome)
        tvTotalExpense = findViewById(R.id.tvTotalExpense)
        tvEmptyState = findViewById(R.id.tvEmptyState)
        rvTransactions = findViewById(R.id.rvTransactions)
        fabAddTransaction = findViewById(R.id.fabAddTransaction)
    }

    private fun setupRecyclerView() {
        transactionAdapter = TransactionAdapter()
        rvTransactions.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity)
            adapter = transactionAdapter
        }
    }

    private fun setupDatabase() {
        database = AppDatabase.getDatabase(this)
    }

    private fun observeData() {
        // Observe total income
        lifecycleScope.launch {
            database.transactionDao().getTotalIncome().collect { income ->
                val totalIncome = income ?: 0.0
                tvTotalIncome.text = formatCurrency(totalIncome)
                updateBalance()
            }
        }

        // Observe total expense
        lifecycleScope.launch {
            database.transactionDao().getTotalExpense().collect { expense ->
                val totalExpense = expense ?: 0.0
                tvTotalExpense.text = formatCurrency(totalExpense)
                updateBalance()
            }
        }

        // Observe all transactions
        lifecycleScope.launch {
            database.transactionDao().getAllTransactions().collect { transactions ->
                if (transactions.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                    transactionAdapter.submitList(transactions)
                }
            }
        }
    }

    private fun updateBalance() {
        lifecycleScope.launch {
            var totalIncome = 0.0
            var totalExpense = 0.0

            database.transactionDao().getTotalIncome().collect { income ->
                totalIncome = income ?: 0.0
            }

            database.transactionDao().getTotalExpense().collect { expense ->
                totalExpense = expense ?: 0.0
            }

            val balance = totalIncome - totalExpense
            tvTotalBalance.text = formatCurrency(balance)
        }
    }

    private fun setupListeners() {
        fabAddTransaction.setOnClickListener {
            val intent = Intent(this, AddTransactionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun formatCurrency(amount: Double): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
        formatter.maximumFractionDigits = 0
        return formatter.format(amount)
    }

    private fun showEmptyState() {
        tvEmptyState.visibility = View.VISIBLE
        rvTransactions.visibility = View.GONE
    }

    private fun hideEmptyState() {
        tvEmptyState.visibility = View.GONE
        rvTransactions.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        // Refresh data ketika kembali dari AddTransactionActivity
        observeData()
    }
}
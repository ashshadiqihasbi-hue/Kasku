package com.nama_anda.kasku.Database


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Create - Insert transaksi baru
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    // Read - Ambil semua transaksi, diurutkan dari yang terbaru
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    // Read - Hitung total pemasukan
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    // Read - Hitung total pengeluaran
    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    // Update - Update transaksi
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    // Delete - Hapus transaksi
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // Delete - Hapus semua transaksi
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}
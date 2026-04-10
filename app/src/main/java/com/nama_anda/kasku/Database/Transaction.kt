package com.nama_anda.kasku.Database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "transactions")
@Parcelize
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val type: String, // "INCOME" atau "EXPENSE"

    val amount: Double,

    val description: String,

    val date: String, // Format: "dd/MM/yyyy"

    val timestamp: Long = System.currentTimeMillis()
) : Parcelable {

    companion object {
        const val TYPE_INCOME = "INCOME"
        const val TYPE_EXPENSE = "EXPENSE"
    }
}
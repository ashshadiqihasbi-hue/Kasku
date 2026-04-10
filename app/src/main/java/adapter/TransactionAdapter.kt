package com.nama_anda.kasku.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.nama_anda.kasku.R
import com.nama_anda.kasku.Database.Transaction
import java.text.NumberFormat
import java.util.*

class TransactionAdapter : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private var transactions = listOf<Transaction>()

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val viewTypeIndicator: View = itemView.findViewById(R.id.viewTypeIndicator)
        private val ivTransactionIcon: ImageView = itemView.findViewById(R.id.ivTransactionIcon)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        private val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)

        fun bind(transaction: Transaction) {
            tvDescription.text = transaction.description
            tvDate.text = transaction.date

            val context = itemView.context
            val isIncome = transaction.type == Transaction.TYPE_INCOME

            // Format currency
            val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            formatter.maximumFractionDigits = 0
            val formattedAmount = formatter.format(transaction.amount)
                .replace("Rp", "")
                .trim()

            // Set amount with sign
            val amountText = if (isIncome) {
                "+ Rp $formattedAmount"
            } else {
                "- Rp $formattedAmount"
            }
            tvAmount.text = amountText

            // Set colors based on type
            if (isIncome) {
                // Income - Green
                val greenColor = ContextCompat.getColor(context, R.color.income_green)
                viewTypeIndicator.setBackgroundColor(greenColor)
                ivTransactionIcon.setColorFilter(greenColor)
                tvAmount.setTextColor(greenColor)
                ivTransactionIcon.setImageResource(android.R.drawable.arrow_down_float)
                ivTransactionIcon.rotation = 180f // Rotate to point up
            } else {
                // Expense - Red
                val redColor = ContextCompat.getColor(context, R.color.expense_red)
                viewTypeIndicator.setBackgroundColor(redColor)
                ivTransactionIcon.setColorFilter(redColor)
                tvAmount.setTextColor(redColor)
                ivTransactionIcon.setImageResource(android.R.drawable.arrow_down_float)
                ivTransactionIcon.rotation = 0f
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size

    fun submitList(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}
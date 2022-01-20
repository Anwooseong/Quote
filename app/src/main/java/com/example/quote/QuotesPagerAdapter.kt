package com.example.quote

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class QuotesPagerAdapter(private val quotes: List<Quote>, private val isNameRevealed: Boolean) :
    RecyclerView.Adapter<QuotesPagerAdapter.QuoteViewHolder>() { //뷰페이저같은 경우 RecyclerView를 기반으로 하기 때문에 리사이클러뷰 어댑터 상속


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = QuoteViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_quote, parent, false)
    )

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val actualPosition = position % quotes.size
        holder.bind(quotes[actualPosition], isNameRevealed)
    }

    override fun getItemCount() = Int.MAX_VALUE

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val quoteTextView: TextView = itemView.findViewById(R.id.quoteTextView)
        private val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)

        @SuppressLint("SetTextI18n")
        fun bind(quote: Quote, isNameRevealed: Boolean) {
            quoteTextView.text = "\"${quote.quote}\""

            if (isNameRevealed) {
                nameTextView.text = "-${quote.name}-"
                nameTextView.visibility = View.VISIBLE
            } else {
                nameTextView.visibility = View.GONE
            }
        }
    }
}
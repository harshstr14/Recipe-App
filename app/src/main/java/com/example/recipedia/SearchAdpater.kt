package com.example.recipedia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SearchAdpater (val context: Context,var recipeList: List<Recipe>) : RecyclerView.Adapter<SearchAdpater.SearchViewHolder>() {

    lateinit var myListener: OnItemClickListener
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachitem_design,parent,false)
        return SearchViewHolder(itemView,myListener)
    }

    fun filterList(recipeList: List<Recipe>) {
        this.recipeList = recipeList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val currentItem = recipeList[position]
        Picasso.get().load(currentItem.image).into(holder.imageView)
        holder.textView1.text = currentItem.cuisine
        holder.textView2.text = currentItem.name
        holder.cardtextView1.text = currentItem.difficulty
        holder.cardtextView2.text = currentItem.cookTimeMinutes.toString()
        holder.cardtextView3.text = currentItem.rating.toString()
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class SearchViewHolder(itemView: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
        val imageView: CircleImageView
        val textView1: TextView
        val textView2: TextView
        val cardtextView1: TextView
        val cardtextView2: TextView
        val cardtextView3: TextView

        init {

            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }

            imageView = itemView.findViewById<CircleImageView>(R.id.recipe_image)
            textView1 = itemView.findViewById<TextView>(R.id.txtview1)
            textView2 = itemView.findViewById<TextView>(R.id.txtview2)
            cardtextView1 = itemView.findViewById<TextView>(R.id.cardtxt1)
            cardtextView2 = itemView.findViewById<TextView>(R.id.cardtxt2)
            cardtextView3 = itemView.findViewById<TextView>(R.id.cardtxt3)
        }
    }
}
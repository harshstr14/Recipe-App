package com.example.recipedia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class HomeAdapter1 (val context: Context,val recipeList: List<Recipe>) : RecyclerView.Adapter<HomeAdapter1.HomeViewHolder1>() {

    lateinit var myListener: OnItemClickListener
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClickListener){
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder1 {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.eachitem_design,parent,false)
        return HomeViewHolder1(itemView,myListener)
    }

    override fun onBindViewHolder(holder: HomeViewHolder1, position: Int) {
        val currentItem = recipeList[position]
        Picasso.get().load(currentItem.image).into(holder.imageView)
        holder.textView1.text = currentItem.cuisine
        holder.textView2.text = currentItem.name
        holder.cardtextView1.text = currentItem.difficulty
        val time = currentItem.cookTimeMinutes.toString()
        val rating = currentItem.rating.toString()
        "$time min".also { holder.cardtextView2.text = it }
        "$rating ★".also { holder.cardtextView3.text = it }
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class HomeViewHolder1(itemView: View,listener: OnItemClickListener) : RecyclerView.ViewHolder(itemView) {
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
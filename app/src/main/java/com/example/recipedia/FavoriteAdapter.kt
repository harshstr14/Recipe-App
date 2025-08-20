package com.example.recipedia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class FavoriteAdapter (val context: Context, userID: String,val favrecipe: List<FavRecipe>,private val onItemDeleted: (String) -> Unit)
    : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    val database = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Favorites")
    lateinit var myListener: OnItemClicklistener
    interface OnItemClicklistener{
        fun onItemClick(position: Int)
    }
    fun setOnItemClickListener(listener: OnItemClicklistener){
        myListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fav_item_design,parent,false)
        return FavoriteViewHolder(itemView,myListener)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = favrecipe[position]
        Picasso.get().load(currentItem.image).into(holder.imageView)
        holder.textView1.text = currentItem.cuisine
        holder.textView2.text = currentItem.name
        holder.cardtextView1.text = currentItem.difficult
        val time = currentItem.cookTimeMinutes.toString()
        val rating = currentItem.rating.toString()
        "$time min".also { holder.cardtextView2.text = it }
        "$rating ★".also { holder.cardtextView3.text = it }

        holder.deleteBtn.setOnClickListener {
            val name = currentItem.name
            if (name != null) {
                database.child(name).removeValue().addOnSuccessListener {
                    onItemDeleted(name)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return favrecipe.size
    }
    class FavoriteViewHolder(itemView: View,listener: OnItemClicklistener) : RecyclerView.ViewHolder(itemView) {
        val imageView: CircleImageView
        val textView1: TextView
        val textView2: TextView
        val cardtextView1: TextView
        val cardtextView2: TextView
        val cardtextView3: TextView
        val deleteBtn: ImageButton

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
            deleteBtn = itemView.findViewById<ImageButton>(R.id.deleteBtn)
        }
    }
}
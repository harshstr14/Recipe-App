package com.example.recipedia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipedia.databinding.FragmentFavoriteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Favorite : Fragment() {
    lateinit var binding: FragmentFavoriteBinding
    lateinit var recyclerView: RecyclerView
    lateinit var favoriteAdapter: FavoriteAdapter
    lateinit var favoriteList: ArrayList<FavRecipe>
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val userID = auth.currentUser?.uid

        if (userID != null) {
            database = FirebaseDatabase.getInstance().getReference("Users").child(userID).child("Favorites")
            database.get().addOnSuccessListener { snapshot ->
                favoriteList = ArrayList()
                for (recipeSnapshot in snapshot.children) {
                    val recipe = recipeSnapshot.getValue(FavRecipe::class.java)
                    recipe?.let {
                        favoriteList.add(it)
                    }
                }

                recyclerView = binding.recyclerview3
                favoriteAdapter = FavoriteAdapter(requireContext(),userID,favoriteList, onItemDeleted = {itemName ->
                    val index = favoriteList.indexOfFirst { it.name == itemName }
                    if (index != -1) {
                        favoriteList.removeAt(index)
                        favoriteAdapter.notifyItemRemoved(index)
                    }
                })
                recyclerView.adapter = favoriteAdapter
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                favoriteAdapter.setOnItemClickListener(object : FavoriteAdapter.OnItemClicklistener{

                    override fun onItemClick(position: Int) {

                        val fragment = OnclickItem()
                        val bundle = Bundle()

                        bundle.putString("cuisine", favoriteList[position].cuisine)
                        bundle.putString("name", favoriteList[position].name)
                        bundle.putString("difficulty", favoriteList[position].difficult)
                        bundle.putInt("cookTimeMinutes", favoriteList[position].cookTimeMinutes ?: 0)
                        bundle.putDouble("rating", favoriteList[position].rating ?: 0.0)
                        bundle.putString("image", favoriteList[position].image)
                        bundle.putInt("caloriesPerServing", favoriteList[position].caloriesPerServing ?: 0)
                        bundle.putString("Ingredients",favoriteList[position].ingredients)
                        bundle.putString("Instructions",favoriteList[position].instructions)
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right
                            )
                            .replace(R.id.framelayout, fragment)
                            .addToBackStack(null)
                            .commit()
                    }
                })
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to fetch Data", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

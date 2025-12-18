package com.example.recipedia

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.recipedia.databinding.FragmentOnclickItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class OnclickItem : Fragment() {
    private val CHANNEL_ID = "recipe_channel"
    private val CHANNEL_NAME = "Recipe_Notifications"
    lateinit var binding: FragmentOnclickItemBinding
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentOnclickItemBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cuisine = arguments?.getString("cuisine").toString()
        val name = arguments?.getString("name").toString()
        val difficulty = arguments?.getString("difficulty").toString()
        val cookTimeMinutes = arguments?.getInt("cookTimeMinutes", 0)
        val rating = arguments?.getDouble("rating",0.0)
        val image = arguments?.getString("image")
        val caloriesPerServing = arguments?.getInt("caloriesPerServing",0)
        val ingredients = arguments?.getString("Ingredients")
        val instructions = arguments?.getString("Instructions")

        binding.textview1.text = name
        binding.textview2.text = cuisine
        binding.difftxt.text = difficulty
        binding.caltxt.text = caloriesPerServing.toString()
        binding.ingredientstxt.text = ingredients
        binding.instructionstxt.text = instructions
        Picasso.get().load(image).into(binding.imageview)
        val time = cookTimeMinutes.toString()
        val review = rating.toString()
        "$time min".also { binding.timetxt.text = it }
        "$review ★".also { binding.reviewtxt.text = it }

        createNotificationChannel()

        binding.favbtn.setOnClickListener {
            auth = FirebaseAuth.getInstance()
            val userID = auth.currentUser?.uid

            if (userID != null) {
                database = FirebaseDatabase.getInstance().getReference("Users").child(userID)
                    .child("Favorites").child(name)
                database.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val recipe = FavRecipe(
                            image = image,
                            name = name,
                            cuisine = cuisine,
                            difficult = difficulty,
                            cookTimeMinutes = cookTimeMinutes,
                            rating = rating,
                            caloriesPerServing = caloriesPerServing,
                            ingredients = ingredients,
                            instructions = instructions
                        )
                        database.setValue(recipe).addOnSuccessListener {
                            Log.d("Favourite", "Recipe Added to Favourite: $name")
                            Toast.makeText(requireContext(), "Added to Favourites", Toast.LENGTH_SHORT).show()
                            showNotification()
                        }.addOnFailureListener {
                            Log.e("Favourite", "Failed to add recipe: ${it.message}")
                            Toast.makeText(requireContext(), "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.d("Favourite", "Recipe Already added to favorites: $name")
                        Toast.makeText(requireContext(), "Already added to favorites", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    Log.e("Favourite", "Error fetching Favourite data: ${it.message}")
                    Toast.makeText(requireContext(), "Unable to access Favourite", Toast.LENGTH_SHORT).show()
                }
            } else {
                Log.e("Auth", "UID is null after successful registration")
                Toast.makeText(requireContext(), "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun showNotification() {
        val notification = NotificationCompat.Builder(requireContext(),CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle("Recipe Saved to Favorites 🍽️")
            .setContentText("You’ve added a new recipe to your favorites. Happy cooking!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        with (NotificationManagerCompat.from(requireContext())) {
            if (ActivityCompat.checkSelfPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            notify(1001,notification.build())
        }
    }
}
package com.example.recipedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.interfaces.ItemClickListener
import com.denzcoskun.imageslider.models.SlideModel
import com.example.recipedia.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Home : Fragment() {
    lateinit var binding: FragmentHomeBinding
    lateinit var recyclerView1: RecyclerView
    lateinit var homeAdapter1: HomeAdapter1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView1 = binding.recyclerview1

        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel(R.drawable.card1, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.card2, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.card3, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.card4, ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel(R.drawable.card5, ScaleTypes.CENTER_CROP))

        val imageSlider = binding.imageSlider
        imageSlider.setImageList(imageList)
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP)

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getRecipeData()

        retrofitData.enqueue(object : Callback<Data?>{

            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                if (!isAdded) return

                val responseBody = response.body()
                val recipeList = responseBody?.recipes!!

                imageSlider.setItemClickListener(object : ItemClickListener{
                    override fun doubleClick(position: Int) {
                        TODO("Not yet implemented")
                    }

                    override fun onItemSelected(position: Int) {
                        val map = mapOf(0 to 0, 1 to 27, 2 to 24, 3 to 25, 4 to 29)
                        val index = map[position] ?: return

                        val ingredients = StringBuilder()
                        for (item in recipeList[index].ingredients) {
                            ingredients.append("• ").append(item).append("\n")
                        }

                        val instruction = StringBuilder()
                        for (item in recipeList[index].instructions) {
                            instruction.append("• ").append(item).append("\n")
                        }

                        val fragment = OnclickItem()
                        val bundle = Bundle()

                        bundle.putString("cuisine", recipeList[index].cuisine)
                        bundle.putString("name", recipeList[index].name)
                        bundle.putString("difficulty", recipeList[index].difficulty)
                        bundle.putInt("cookTimeMinutes", recipeList[index].cookTimeMinutes)
                        bundle.putDouble("rating", recipeList[index].rating)
                        bundle.putString("image", recipeList[index].image)
                        bundle.putInt("caloriesPerServing", recipeList[index].caloriesPerServing)
                        bundle.putString("Ingredients",ingredients.toString())
                        bundle.putString("Instructions",instruction.toString())
                        fragment.arguments = bundle

                        parentFragmentManager.beginTransaction()
                            .setCustomAnimations(
                                R.anim.enter_from_right,
                                R.anim.exit_to_left,
                                R.anim.enter_from_left,
                                R.anim.exit_to_right
                            )
                            .replace(R.id.framelayout,fragment)
                            .addToBackStack(null)
                            .commit()
                    }

                })

                homeAdapter1 = HomeAdapter1(requireContext(),recipeList)
                recyclerView1.adapter = homeAdapter1
                recyclerView1.layoutManager = LinearLayoutManager(requireContext())

                homeAdapter1.setOnItemClickListener(object : HomeAdapter1.OnItemClickListener{
                    override fun onItemClick(position: Int) {

                        val ingredients = StringBuilder()
                        for (item in recipeList[position].ingredients) {
                            ingredients.append("• ").append(item).append("\n")
                        }

                        val instruction = StringBuilder()
                        for (item in recipeList[position].instructions) {
                            instruction.append("• ").append(item).append("\n")
                        }

                        val fragment = OnclickItem()
                        val bundle = Bundle()

                        bundle.putString("cuisine", recipeList[position].cuisine)
                        bundle.putString("name", recipeList[position].name)
                        bundle.putString("difficulty", recipeList[position].difficulty)
                        bundle.putInt("cookTimeMinutes", recipeList[position].cookTimeMinutes)
                        bundle.putDouble("rating", recipeList[position].rating)
                        bundle.putString("image", recipeList[position].image)
                        bundle.putInt("caloriesPerServing", recipeList[position].caloriesPerServing)
                        bundle.putString("Ingredients",ingredients.toString())
                        bundle.putString("Instructions",instruction.toString())
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
            }

            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Log.d("Home","OnFailure: " + t.message)
            }
        })
    }
}
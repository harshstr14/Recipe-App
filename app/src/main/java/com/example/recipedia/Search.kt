package com.example.recipedia

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recipedia.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class Search : Fragment() {
    lateinit var binding: FragmentSearchBinding
    lateinit var recyclerView: RecyclerView
    lateinit var searchAdpater: SearchAdpater
    lateinit var filteredList: ArrayList<Recipe>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentSearchBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerview

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://dummyjson.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)

        val retrofitData = retrofitBuilder.getRecipeData()

        retrofitData.enqueue(object : Callback<Data?> {

            override fun onResponse(call: Call<Data?>, response: Response<Data?>) {
                if (!isAdded) return

                val responseBody = response.body()
                val recipeList = responseBody?.recipes!!
                filteredList = ArrayList(recipeList)

                searchAdpater = SearchAdpater(requireContext(),recipeList)
                recyclerView.adapter = searchAdpater
                recyclerView.layoutManager = LinearLayoutManager(requireContext())

                searchAdpater.setOnItemClickListener(object : SearchAdpater.OnItemClickListener{
                    override fun onItemClick(position: Int) {

                        if (filteredList.isNotEmpty()) {
                            val ingredients = StringBuilder()
                            for (item in filteredList[position].ingredients) {
                                ingredients.append("• ").append(item).append("\n")
                            }

                            val instruction = StringBuilder()
                            for (item in filteredList[position].instructions) {
                                instruction.append("• ").append(item).append("\n")
                            }

                            val fragment = OnclickItem()
                            val bundle = Bundle()

                            bundle.putString("cuisine", filteredList[position].cuisine)
                            bundle.putString("name", filteredList[position].name)
                            bundle.putString("difficulty", filteredList[position].difficulty)
                            bundle.putInt("cookTimeMinutes", filteredList[position].cookTimeMinutes)
                            bundle.putDouble("rating", filteredList[position].rating)
                            bundle.putString("image", filteredList[position].image)
                            bundle.putInt("caloriesPerServing", filteredList[position].caloriesPerServing)
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
                    }
                })

                binding.customSearchView.setOnQueryTextListener(object : CustomSearchView.OnQueryTextListener{
                    override fun onQueryTextSubmit(query: String): Boolean {
                        return false
                    }

                    override fun onQueryTextChange(newText: String): Boolean {
                        if (newText.trim().isEmpty()) {
                            recyclerView.visibility = View.GONE
                            filteredList.clear()
                            searchAdpater.filterList(filteredList)
                        } else {
                            filterer(newText, recipeList)
                        }
                        return true
                    }
                })
            }

            override fun onFailure(call: Call<Data?>, t: Throwable) {
                Log.d("Search", "OnFailure: ${t.message}")
                recyclerView.visibility = View.GONE
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun filterer(newText: String?, recipeList: List<Recipe>) {
        if (newText != null) {
            filteredList = ArrayList()
            for (i in recipeList) {
                if (i.name.lowercase().contains(newText.lowercase())) {
                    filteredList.add(i)
                }
            }
            if (filteredList.isEmpty()) {
                context?.let {
                    binding.tvNoResults.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                    Toast.makeText(requireContext(),"Recipe Not Found", Toast.LENGTH_SHORT).show()
                }
            } else {
                binding.tvNoResults.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                searchAdpater.filterList(filteredList)
            }
        }
    }
}
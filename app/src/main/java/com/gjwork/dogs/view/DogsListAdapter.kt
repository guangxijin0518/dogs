package com.gjwork.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gjwork.dogs.R
import com.gjwork.dogs.databinding.ItemDogBinding
import com.gjwork.dogs.model.DogBreed

class DogsListAdapter(val dogsList: ArrayList<DogBreed>): RecyclerView.Adapter<DogsListAdapter.DogsViewHolder>() {
    inner class DogsViewHolder(val binding: ItemDogBinding): RecyclerView.ViewHolder(binding.root)

    fun updateDogList(newDogsList: List<DogBreed>) {
        dogsList.clear()
        dogsList.addAll(newDogsList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDogBinding.inflate(inflater, parent, false)
        return DogsViewHolder(binding)
    }

    override fun getItemCount() = dogsList.size

    override fun onBindViewHolder(holder: DogsViewHolder, position: Int) {
        with (holder) {
            with (dogsList[position]) {
                binding.name.text = this.dogBread
                binding.lifespan.text = this.lifeSpan

                binding.root.setOnClickListener {
                    val action = ListFragmentDirections.actionDetailFragment()
                    Navigation.findNavController(it).navigate((action))
                }
            }
        }
    }
}
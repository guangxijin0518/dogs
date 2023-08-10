package com.gjwork.dogs.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.gjwork.dogs.R
import com.gjwork.dogs.databinding.ItemDogBinding
import com.gjwork.dogs.model.DogBreed
import com.gjwork.dogs.util.getProgressDrawable
import com.gjwork.dogs.util.loadImage

class DogsListAdapter(val dogsList: ArrayList<DogBreed>) :
    RecyclerView.Adapter<DogsListAdapter.DogsViewHolder>(), DogClickListener {
    inner class DogsViewHolder(val binding: ItemDogBinding) : RecyclerView.ViewHolder(binding.root)

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
        with(holder) {
            with(dogsList[position]) {
                binding.dog = this
                binding.listener = this@DogsListAdapter

//                binding.root.setOnClickListener {
//                    val action = ListFragmentDirections.actionDetailFragment()
//                    action.dogUuid = this.uuid
//                    Navigation.findNavController(it).navigate((action))
//                }
            }
        }
    }

    override fun onDogClocked(v: View) {
        val action = ListFragmentDirections.actionDetailFragment()
        action.dogUuid = v.findViewById<TextView>(R.id.dogId).text.toString().toInt()
        Navigation.findNavController(v).navigate((action))
    }
}
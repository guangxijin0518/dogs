package com.gjwork.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.gjwork.dogs.R
import com.gjwork.dogs.databinding.FragmentDetailBinding
import com.gjwork.dogs.util.getProgressDrawable
import com.gjwork.dogs.util.loadImage
import com.gjwork.dogs.viewmodel.DetailViewModel

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var dogUuid: Int = 0

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            dogUuid = DetailFragmentArgs.fromBundle(it).dogUuid
        }

        viewModel = ViewModelProviders.of(this)[DetailViewModel::class.java]
        viewModel.fetch(dogUuid)

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogLiveData.observe(this, Observer { dog ->
            dog?.let {
                binding.dogName.text = dog.dogBread
                binding.dogPurpose.text = dog.bredFor
                binding.dogTemperament.text = dog.temperament
                binding.dogLifespan.text = dog.lifeSpan
                binding.dogImage.loadImage(dog.imageUrl, getProgressDrawable(binding.dogImage.context))
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
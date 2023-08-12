package com.gjwork.dogs.view

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.gjwork.dogs.R
import com.gjwork.dogs.databinding.FragmentDetailBinding
import com.gjwork.dogs.databinding.SmsInfoDialogBinding
import com.gjwork.dogs.model.DogBreed
import com.gjwork.dogs.model.DogPalette
import com.gjwork.dogs.model.SmsInfo
import com.gjwork.dogs.viewmodel.DetailViewModel

class DetailFragment : Fragment() {
    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    private var dogUuid: Int = 0
    private var sendSmsStarted = false
    private var currentDog: DogBreed? = null

    private lateinit var viewModel: DetailViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
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
        viewModel.dogLiveData.observe(viewLifecycleOwner) { dog ->
            currentDog = dog
            dog?.let {
                binding.dog = it

                it.imageUrl?.let { imageUrl ->
                    setupBackgroundColor(imageUrl)
                }
            }
        }
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.lightMutedSwatch?.rgb ?: 0
                            binding.palette = DogPalette(intColor)
                        }
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSmsStarted = true

                (activity as MainActivity).checkSmsPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this dog breed")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}")
                intent.putExtra(Intent.EXTRA_STREAM, currentDog?.imageUrl)
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSmsStarted && permissionGranted) {
            context?.let {
                val dialogBinding = SmsInfoDialogBinding.inflate(LayoutInflater.from(it), null, false)
                val smsInfo = SmsInfo("", "${currentDog?.dogBreed} bred for ${currentDog?.bredFor}", currentDog?.imageUrl)

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS") {_, _ ->
                        if (dialogBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSMS(smsInfo)
                        }
                    }.setNegativeButton("Cancel") {_, _ ->}
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSMS(smsInfo: SmsInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
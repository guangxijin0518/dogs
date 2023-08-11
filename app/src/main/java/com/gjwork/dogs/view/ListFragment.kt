package com.gjwork.dogs.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gjwork.dogs.R
import com.gjwork.dogs.databinding.FragmentListBinding
import com.gjwork.dogs.viewmodel.ListViewModel

class ListFragment : Fragment() {
    private var _binding: FragmentListBinding? = null

    private val binding get() = _binding!!

    private lateinit var viewModel: ListViewModel
    private val dogsListAdapter = DogsListAdapter(arrayListOf())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        _binding = FragmentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProviders.of(this)[ListViewModel::class.java]
        viewModel.refresh()

        binding.dogsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = dogsListAdapter
        }

        binding.refreshLayout.setOnRefreshListener {
            binding.dogsList.visibility = View.GONE
            binding.listError.visibility = View.GONE
            binding.loadingView.visibility = View.VISIBLE

            viewModel.refreshBypassCache()
            binding.refreshLayout.isRefreshing = false
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.dogs.observe(viewLifecycleOwner) { dogs ->
            dogs?.let {
                binding.dogsList.visibility = View.VISIBLE
                dogsListAdapter.updateDogList(dogs)
            }
        }

        viewModel.isError.observe(viewLifecycleOwner) { isError ->
            isError?.let {
                binding.listError.visibility = if (isError) View.VISIBLE else View.GONE
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            isLoading?.let {
                binding.loadingView.visibility = if (isLoading) View.VISIBLE else View.GONE

                if (isLoading) {
                    binding.dogsList.visibility = View.GONE
                    binding.listError.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.list_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.actionSettings -> {
                view?.let {
                    Navigation.findNavController(it).navigate(ListFragmentDirections.actionSettings())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
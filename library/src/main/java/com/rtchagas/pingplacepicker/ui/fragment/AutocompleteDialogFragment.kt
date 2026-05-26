package com.rtchagas.pingplacepicker.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.R
import com.rtchagas.pingplacepicker.databinding.FragmentDialogAutocompleteBinding
import com.rtchagas.pingplacepicker.inject.PingKoinComponent
import com.rtchagas.pingplacepicker.ui.adapter.AutocompleteAdapter
import com.rtchagas.pingplacepicker.ui.collectWithLifecycle
import com.rtchagas.pingplacepicker.ui.toast
import com.rtchagas.pingplacepicker.viewmodel.AutocompleteViewModel
import com.rtchagas.pingplacepicker.viewmodel.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Full-screen autocomplete dialog that replaces the retired
 * [com.google.android.libraries.places.widget.Autocomplete] activity widget.
 */
internal class AutocompleteDialogFragment :
    AppCompatDialogFragment(),
    PingKoinComponent {

    interface OnPlacePickedListener {
        fun onAutocompletePlacePicked(place: Place)
    }

    private var _binding: FragmentDialogAutocompleteBinding? = null
    private val binding: FragmentDialogAutocompleteBinding get() = _binding!!

    private val viewModel: AutocompleteViewModel by viewModel()

    private val adapter = AutocompleteAdapter { prediction ->
        viewModel.selectPrediction(prediction)
    }

    var listener: OnPlacePickedListener? = null

    override fun getTheme(): Int = R.style.PingTheme_FullScreenDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bias = requireArguments().let {
            LatLng(it.getDouble(ARG_BIAS_LAT), it.getDouble(ARG_BIAS_LNG))
        }
        val radius = requireArguments().getDouble(ARG_RADIUS_M)
        viewModel.configure(bias, radius)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDialogAutocompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { dismiss() }
        binding.rvPredictions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPredictions.adapter = adapter
        binding.etQuery.doAfterTextChanged { viewModel.setQuery(it?.toString().orEmpty()) }
        binding.etQuery.requestFocus()

        viewModel.predictions.collectWithLifecycle(viewLifecycleOwner) { handlePredictions(it) }
        viewModel.selectedPlace.collectWithLifecycle(viewLifecycleOwner) { handleSelectedPlace(it) }
    }

    override fun onStart() {
        super.onStart()
        // Full-screen dialog with no system window background frame.
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handlePredictions(resource: Resource<List<AutocompletePrediction>>) {
        binding.pbLoading.isVisible = resource.status == Resource.Status.LOADING
        when (resource.status) {
            Resource.Status.SUCCESS -> adapter.submitList(resource.data.orEmpty())
            Resource.Status.ERROR -> requireContext().toast(R.string.picker_load_places_error)
            Resource.Status.LOADING,
            Resource.Status.NO_DATA -> adapter.submitList(emptyList())
        }
    }

    private fun handleSelectedPlace(resource: Resource<Place>) {
        binding.pbLoading.isVisible = resource.status == Resource.Status.LOADING
        when (resource.status) {
            Resource.Status.SUCCESS -> {
                resource.data?.let { listener?.onAutocompletePlacePicked(it) }
                dismissAllowingStateLoss()
            }

            Resource.Status.ERROR -> requireContext().toast(R.string.picker_load_this_place_error)
            else -> Unit
        }
    }

    companion object {
        private const val ARG_BIAS_LAT = "arg_bias_lat"
        private const val ARG_BIAS_LNG = "arg_bias_lng"
        private const val ARG_RADIUS_M = "arg_radius_m"

        fun newInstance(
            bias: LatLng,
            radiusMeters: Double,
            listener: OnPlacePickedListener,
        ): AutocompleteDialogFragment = AutocompleteDialogFragment().apply {
            arguments = Bundle().apply {
                putDouble(ARG_BIAS_LAT, bias.latitude)
                putDouble(ARG_BIAS_LNG, bias.longitude)
                putDouble(ARG_RADIUS_M, radiusMeters)
            }
            this.listener = listener
        }
    }
}

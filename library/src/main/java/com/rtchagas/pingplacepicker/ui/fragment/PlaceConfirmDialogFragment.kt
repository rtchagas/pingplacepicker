package com.rtchagas.pingplacepicker.ui.fragment

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.BundleCompat
import androidx.core.view.isVisible
import androidx.transition.TransitionManager
import coil.load
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.Config
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.R
import com.rtchagas.pingplacepicker.databinding.FragmentDialogPlaceConfirmBinding
import com.rtchagas.pingplacepicker.inject.PingKoinComponent
import com.rtchagas.pingplacepicker.ui.UiUtils
import com.rtchagas.pingplacepicker.ui.collectWithLifecycle
import com.rtchagas.pingplacepicker.viewmodel.PlaceConfirmDialogViewModel
import com.rtchagas.pingplacepicker.viewmodel.Resource
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

internal class PlaceConfirmDialogFragment : AppCompatDialogFragment(), PingKoinComponent {

    private var _binding: FragmentDialogPlaceConfirmBinding? = null

    private val binding: FragmentDialogPlaceConfirmBinding
        get() = _binding!!

    private val viewModel: PlaceConfirmDialogViewModel by viewModel()

    private lateinit var place: Place

    var confirmListener: OnPlaceConfirmedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        place = BundleCompat.getParcelable(requireArguments(), ARG_PLACE, Place::class.java)
            ?: throw IllegalArgumentException("You must pass a Place as argument to this fragment")

        viewModel.placePhotoUri
            .collectWithLifecycle(this) { handlePlacePhotoLoaded(it) }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(requireActivity())

        builder.setTitle(R.string.picker_place_confirm)
            .setView(getContentView(requireContext()))
            .setPositiveButton(android.R.string.ok) { _, _ ->
                confirmListener?.onPlaceConfirmed(place)
                dismiss()
            }
            .setNegativeButton(R.string.picker_place_confirm_cancel) { _, _ ->
                // Just dismiss here...
                dismiss()
            }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getContentView(context: Context): View {
        _binding = FragmentDialogPlaceConfirmBinding.inflate(LayoutInflater.from(context))
        initializeUi()
        return binding.root
    }

    private fun initializeUi() = with(binding) {

        if (place.displayName.isNullOrEmpty()) {
            tvPlaceName.isVisible = false
        } else {
            tvPlaceName.text = place.displayName
        }

        tvPlaceAddress.text = place.formattedAddress

        fetchPlaceMap()
        fetchPlacePhoto()
    }

    private fun fetchPlaceMap() = with(binding.ivPlaceMap) {

        isVisible =
            if (resources.getBoolean(R.bool.show_confirmation_map) &&
                PingPlacePicker.mapsApiKey.isNotEmpty()
            ) true
            else return@with

        val staticMapUrl = getFinalMapUrl() ?: return@with

        binding.ivPlaceMap.load(staticMapUrl) {
            listener(
                onError = { request, error ->
                    isVisible = false
                    Log.w(TAG, "Error loading map image: ${request.data}", error.throwable)
                }
            )
        }
    }

    private fun fetchPlacePhoto() {
        val photoMetadata = place.photoMetadatas?.firstOrNull()
        if (resources.getBoolean(R.bool.show_confirmation_photo) && photoMetadata != null) {
            viewModel.loadPlacePhoto(photoMetadata)
        } else {
            handlePlacePhotoLoaded(Resource.noData())
        }
    }

    private fun getFinalMapUrl(): String? {
        val location = place.location ?: return null
        var mapUrl = Config.STATIC_MAP_URL.format(
            location.latitude,
            location.longitude,
            PingPlacePicker.mapsApiKey,
            Locale.getDefault().language,
        )
        if (UiUtils.isNightModeEnabled(requireContext())) {
            mapUrl += Config.STATIC_MAP_URL_STYLE_DARK
        }
        return mapUrl
    }

    private fun handlePlacePhotoLoaded(result: Resource<Uri>) {
        // The view may not be inflated yet on early flow emissions.
        val binding = _binding ?: return
        val uri = result.data
        if (result.status == Resource.Status.SUCCESS && uri != null) {
            TransitionManager.beginDelayedTransition(binding.root)
            binding.ivPlacePhoto.visibility = View.VISIBLE
            binding.ivPlacePhoto.load(uri)
        } else {
            binding.ivPlacePhoto.visibility = View.GONE
        }
    }

    /**
     * Listener called when a place is updated.
     */
    interface OnPlaceConfirmedListener {
        fun onPlaceConfirmed(place: Place)
    }

    companion object {

        private const val TAG = "Ping#PlaceConfirmDialog"
        private const val ARG_PLACE = "arg_place"

        fun newInstance(
            place: Place,
            listener: OnPlaceConfirmedListener
        ): PlaceConfirmDialogFragment {

            val args = Bundle()
            args.putParcelable(ARG_PLACE, place)

            return PlaceConfirmDialogFragment().apply {
                arguments = args
                confirmListener = listener
            }
        }
    }
}

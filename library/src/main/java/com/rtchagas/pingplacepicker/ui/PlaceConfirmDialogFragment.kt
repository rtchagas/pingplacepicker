package com.rtchagas.pingplacepicker.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionManager
import com.google.android.libraries.places.api.model.Place
import com.rtchagas.pingplacepicker.Config
import com.rtchagas.pingplacepicker.PingPlacePicker
import com.rtchagas.pingplacepicker.R
import com.rtchagas.pingplacepicker.inject.DaggerInjector
import com.rtchagas.pingplacepicker.viewmodel.PlaceConfirmDialogViewModel
import com.rtchagas.pingplacepicker.viewmodel.Resource
import com.rtchagas.pingplacepicker.viewmodel.inject.PingViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_dialog_place_confirm.view.*
import javax.inject.Inject


class PlaceConfirmDialogFragment : AppCompatDialogFragment() {

    companion object {

        private const val ARG_PLACE = "arg_place"

        fun newInstance(place: Place,
                        listener: OnPlaceConfirmedListener): PlaceConfirmDialogFragment {

            val args = Bundle()
            args.putParcelable(ARG_PLACE, place)

            return PlaceConfirmDialogFragment().apply {
                arguments = args
                confirmListener = listener
            }
        }
    }

    var confirmListener: OnPlaceConfirmedListener? = null

    @Inject
    lateinit var viewModelFactory: PingViewModelFactory

    private lateinit var viewModel: PlaceConfirmDialogViewModel

    private lateinit var place: Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inject dependencies
        DaggerInjector.getInjector(activity!!.application).inject(this)

        // Initialize the ViewModel for this fragment
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get<PlaceConfirmDialogViewModel>(PlaceConfirmDialogViewModel::class.java)

        // Check mandatory parameters for this fragment
        if ((arguments == null) || (arguments?.getParcelable<Place>(ARG_PLACE) == null)) {
            throw IllegalArgumentException("You must pass a Place as argument to this fragment")
        }

        arguments?.run {
            place = getParcelable(ARG_PLACE)!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val builder = AlertDialog.Builder(activity!!)

        builder.setTitle(R.string.picker_place_confirm)
                .setView(getContentView(activity!!))
                .setPositiveButton(android.R.string.ok) { dialog, which ->
                    confirmListener?.onPlaceConfirmed(place)
                    dismiss()
                }
                .setNegativeButton(R.string.picker_place_confirm_cancel) { dialog, which ->
                    // Just dismiss here...
                    dismiss()
                }

        return builder.create()
    }

    @SuppressLint("InflateParams")
    private fun getContentView(context: Context): View {

        val content = LayoutInflater.from(context)
                .inflate(R.layout.fragment_dialog_place_confirm, null)

        content.tvPlaceName.text = place.name
        content.tvPlaceAddress.text = place.address

        fetchPlaceMap(content)
        fetchPlacePhoto(content)

        return content
    }

    private fun fetchPlaceMap(contentView: View) {

        if (resources.getBoolean(R.bool.show_confirmation_map)) {
            val staticMapUrl = Config.STATIC_MAP_URL
                    .format(place.latLng?.latitude,
                            place.latLng?.longitude,
                            PingPlacePicker.androidApiKey)
            Picasso.get().load(staticMapUrl).into(contentView.ivPlaceMap)
        }
        else {
            contentView.ivPlaceMap.visibility = View.GONE
        }
    }

    private fun fetchPlacePhoto(contentView: View) {

        if (resources.getBoolean(R.bool.show_confirmation_photo)
                && (place.photoMetadatas != null)) {

            viewModel.getPlacePhoto(place.photoMetadatas!![0]).observe(this,
                    Observer { handlePlacePhotoLoaded(contentView, it) })
        }
        else {
            contentView.ivPlacePhoto.visibility = View.GONE
        }
    }

    private fun handlePlacePhotoLoaded(contentView: View, result: Resource<Bitmap>) {

        if (result.status == Resource.Status.SUCCESS) {
            TransitionManager.beginDelayedTransition(contentView as ViewGroup)
            contentView.ivPlaceMap.visibility = View.VISIBLE
            contentView.ivPlacePhoto.setImageBitmap(result.data)
        }
        else {
            contentView.ivPlaceMap.visibility = View.GONE
        }
    }

    /**
     * Listener called when a place is updated.
     */
    interface OnPlaceConfirmedListener {
        fun onPlaceConfirmed(place: Place)
    }
}

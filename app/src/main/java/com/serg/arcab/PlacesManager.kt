package com.serg.arcab

import android.content.Context
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Tasks
import com.serg.arcab.utils.launchSilent
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class PlacesManager constructor(
        val context: Context
) {

    var job: Deferred<MutableList<AutocompletePrediction>>? = null

    private var mGeoDataClient = Places.getGeoDataClient(context)
    private val ioContext: CoroutineContext = DefaultDispatcher
    private val uiContext: CoroutineContext = UI

    fun setQuery(query: String, latLngBounds: LatLngBounds?, callback: Callback) = launchSilent(uiContext) {
        job?.cancel()
        callback.loading(true)
        val deferred = search(query, latLngBounds)
        job = deferred
        val list = deferred.await()
        callback.result(list)
    }


    private fun search(query: String, latLngBounds: LatLngBounds? = null) = async(ioContext) {
        val results = mGeoDataClient.getAutocompletePredictions(query, latLngBounds, AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build())
        if (results != null) {
            Tasks.await(results, 30, TimeUnit.SECONDS)
            DataBufferUtils.freezeAndClose(results.result)
        } else {
            mutableListOf<AutocompletePrediction>()
        }
    }

    fun fetchPlaceDetail(placeId: String?, function: (Place) -> Unit) {
        placeId?.let {
            mGeoDataClient?.getPlaceById(placeId)?.addOnCompleteListener {
                if (it.isSuccessful) {
                    val places = it.result as PlaceBufferResponse
                    val myPlace = places.get(0)
                    function(myPlace)
                    places.release()
                } else {
                    Timber.e("Not found")
                }
            }
        }
    }

    interface Callback {
        fun loading(isLoading: Boolean)
        fun result(result: MutableList<AutocompletePrediction>)
    }
}
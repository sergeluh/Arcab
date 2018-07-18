package com.serg.arcab

import android.content.Context
import com.google.android.gms.common.data.DataBufferUtils
import com.google.android.gms.location.places.*
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.tasks.Tasks
import com.serg.arcab.data.AppExecutors
import com.serg.arcab.utils.launchSilent
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

class PlacesManager constructor(
        private val context: Context,
        private val appExecutors: AppExecutors,
        private var geoDataClient: GeoDataClient
) {

    var job: Deferred<MutableList<AutocompletePrediction>>? = null

    fun setQuery(query: String, latLngBounds: LatLngBounds?, callback: Callback) = launchSilent(appExecutors.uiContext) {
        job?.cancel()
        callback.loading(true)
        val deferred = search(query, latLngBounds)
        job = deferred
        val list = deferred.await()
        callback.result(list)
    }


    private fun search(query: String, latLngBounds: LatLngBounds? = null) = async(appExecutors.ioContext) {
        val results = geoDataClient.getAutocompletePredictions(query, latLngBounds, AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build())
        if (results != null) {
            try {
                Tasks.await(results, 30, TimeUnit.SECONDS)
            }catch (e: Exception){
                Timber.d(e.message)
            }
            DataBufferUtils.freezeAndClose(results.result)
        } else {
            mutableListOf<AutocompletePrediction>()
        }
    }

    fun fetchPlaceDetail(placeId: String?, function: (Place) -> Unit) {
        placeId?.let {
            geoDataClient.getPlaceById(placeId)?.addOnCompleteListener {
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

    fun getPlaceById(id: String) = geoDataClient.getPlaceById(id)

    interface Callback {
        fun loading(isLoading: Boolean)
        fun result(result: MutableList<AutocompletePrediction>)
    }
}
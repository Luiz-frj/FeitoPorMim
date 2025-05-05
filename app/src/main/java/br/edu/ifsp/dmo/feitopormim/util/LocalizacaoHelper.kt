package br.edu.ifsp.dmo.feitopormim.util

import android.Manifest
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.io.IOException
import java.util.Locale

class LocalizacaoHelper(
    private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
) {

    interface Callback{
        fun onLocalizacaoRecebida(endereco: Address ,latitude: Double, longitude: Double)
        fun onErro(mensgem: String)
    }

    private fun obterEndereco(latitude: Double, longitude: Double, callback: Callback){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            val geocoder = Geocoder(context, Locale.getDefault())
            geocoder.getFromLocation(
                latitude,
                longitude,
                1,
                object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        if(addresses.isNotEmpty()){
                            callback.onLocalizacaoRecebida(addresses[0],latitude,longitude)
                        } else{
                            callback.onErro("Endereço não encontrado")
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        callback.onErro(errorMessage ?: "Erro deconhecido no Geocoder")
                    }

                }
            )
        } else{
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val address = geocoder.getFromLocation(latitude,longitude,1)
                if(!address.isNullOrEmpty()){
                    callback.onLocalizacaoRecebida(address[0],latitude,longitude)
                } else{
                    callback.onErro("Endereço não encontrado")
                }
            } catch (e: IOException){
                callback.onErro("Erro no Geocode: ${e.message}")
            }
        }
    }

    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION])
    fun obterLocalizacaoAtual(callback: Callback){
        val locationRequest = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fusedLocationClient.getCurrentLocation(locationRequest, null)
            .addOnSuccessListener { location: Location? ->
                if(location != null){
                    val lat = location.latitude
                    val lon = location.longitude
                    obterEndereco(lat, lon, callback)
                } else{
                    callback.onErro("Localização indisponível")
                }
            }
    }
}
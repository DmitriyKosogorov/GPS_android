package com.example.gpslocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {
    val LOCATION_PERM_CODE = 2
    var can_work=false;
    lateinit var adapter: ArrayAdapter<*>

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("can_work", can_work)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState != null)
        {
            if(savedInstanceState.containsKey("can_work"))
                can_work= savedInstanceState["can_work"] as Boolean
        }
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val texter=findViewById<TextView>(R.id.access)
        var lister=findViewById<ListView>(R.id.list)
        // запрашиваем разрешения на доступ к геопозиции
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            // переход в запрос разрешений
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERM_CODE)
        }
        if(can_work) {
            texter.setTextColor(Color.GREEN)
            texter.text="access denied"
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)

            val prv = locationManager.getBestProvider(Criteria(), true)
            Log.d("mytag", locationManager.allProviders.toString())
            if (prv != null) {
                val location = locationManager.getLastKnownLocation(prv)
                if (location != null)
                    displayCoord(location.latitude, location.longitude)
                var prv_1=locationManager.allProviders
                adapter=object : ArrayAdapter<Any?>(this, android.R.layout.simple_list_item_1, prv_1 as List<Any?>)
                {
                    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                        val view = super.getView(position, convertView, parent)
                        val place_for_text = view.findViewById<View>(android.R.id.text1) as TextView
                        val text_for_text = prv_1[position]
                        place_for_text.text=text_for_text
                        if(locationManager.isProviderEnabled(prv_1[position]))
                            place_for_text.setBackgroundColor(Color.GREEN)
                        else
                            place_for_text.setBackgroundColor(Color.RED)
                        return view
                    }
                }
                lister.adapter=adapter
                Log.d("mytag", "location set")
            }
        }
        else
        {
            texter.setTextColor(Color.RED)
            texter.text="access NOT denied"
            Log.d("accessss", "not denied")
        }
        }

    override fun onLocationChanged(loc: Location) {
        val lat = loc.latitude
        val lng = loc.longitude
        displayCoord(lat, lng)
        Log.d("my", "lat " + lat + " long " + lng)
    }

    fun displayCoord(latitude: Double, longtitude: Double) {
        findViewById<TextView>(R.id.lat).text = String.format("%.5f", latitude)
        findViewById<TextView>(R.id.lng).text = String.format("%.5f", longtitude)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_PERM_CODE
            -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.d("accessss", "denied")
                    val texter=findViewById<TextView>(R.id.access)
                    texter.setTextColor(Color.GREEN)
                    texter.text="access denied"
                    can_work=true;
                } else {
                    val texter=findViewById<TextView>(R.id.access)
                    texter.setTextColor(Color.RED)
                    texter.text="access NOT denied"
                    Log.d("accessss", "not denied")

                }
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
                return
            }
        }
    }
    override fun onProviderDisabled(provider: String) {
        val texter=findViewById<TextView>(R.id.access)
        texter.setTextColor(Color.RED)
        texter.text="Providers are disabled"
        adapter.notifyDataSetChanged();
    }

    override fun onProviderEnabled(provider: String) {
        if(can_work==false)
        {
            val texter=findViewById<TextView>(R.id.access)
            texter.setTextColor(Color.RED)
            texter.text="access NOT denied"
        }
        else
        {
            val texter=findViewById<TextView>(R.id.access)
            texter.setTextColor(Color.GREEN)
            texter.text="access denied"
        }
        adapter.notifyDataSetChanged();
    }

    // TODO: обработать возврат в активность onRequestPermissionsResult
}
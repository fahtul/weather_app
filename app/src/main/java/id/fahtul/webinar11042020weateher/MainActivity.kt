package id.fahtul.webinar11042020weateher

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.load.engine.Resource
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        edtCityName.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_SEARCH) {
                    getWeather(edtCityName.text.toString())
                    return true
                }
                return false
            }
        })

    }

    private fun getWeather(cityName: String) {
        progressBar.visibility = View.VISIBLE
        val client = ApiConfig.getApiService().getWeather(cityName, "59c61a1ab38a935a5364cc461a3cc458")
        client.enqueue(object : Callback<ResponseWeather>{
            override fun onFailure(call: Call<ResponseWeather>, t: Throwable) {
                progressBar.visibility = View.GONE
                Log.e("ERROR RESPONSE", "onResponse: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }

            @SuppressLint("SetTextI18n")
            override fun onResponse(
                call: Call<ResponseWeather>,
                response: Response<ResponseWeather>
            ) {
                progressBar.visibility = View.GONE
                if (response.isSuccessful){

                    if(response.code()==200){
                        val responseBody = response.body()

                        txtLocation.text = edtCityName.text.toString()
                        txtCountry.text = getCountryFromCode(responseBody?.sys?.country?:"")
                        txtSky.text =  "Sky Is ${responseBody?.weather?.get(0)?.main}"
                        txtHumidity.text = "Humidity: ${responseBody?.main?.humidity}%"
                        txtPressure.text = "Pressure: ${responseBody?.main?.pressure} hPa"
                        txtTemperature.text = "${kelvinToCelcius(responseBody?.main?.temp?:0.0)} \u2103"
                    }

                }else{
                    if(response.code() == 404){
                        Toast.makeText(
                            this@MainActivity,
                            "Kota tidak ditemukan",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

            }

        })
    }

    private fun kelvinToCelcius(kelvin: Double): String {
        return String.format("%.2f", kelvin - 273.15)
    }

    private fun getCountryFromCode(code: String): String {
        val loc = Locale("", code)
        return loc.displayCountry
    }
}
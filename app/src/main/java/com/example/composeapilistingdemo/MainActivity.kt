package com.example.composeapilistingdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.LazyColumnItems
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.ui.platform.setContent
import androidx.ui.tooling.preview.Preview
import com.example.composeapilistingdemo.ui.ComposeAPIListingDemoTheme
import com.google.gson.Gson
import inc.yoman.asyncdroid.api.EmployeeModel
import inc.yoman.asyncdroid.dsl.load
import inc.yoman.asyncdroid.dsl.then
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var url = "https://limitless-lake-93364.herokuapp.com/hello"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myCoroutine()
        setContent {
            ComposeAPIListingDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Greeting("LOADING PLEASE WAIT")
//                        myCoroutine()
                }
            }
        }
    }

    private fun myCoroutine() {
        load {
            apiCall()
        } then {
            // Compose Here
            setContent {
                ComposeAPIListingDemoTheme {
                    // A surface container using the 'background' color from the theme
                    Surface(color = MaterialTheme.colors.background) {
//                    Greeting("Android")
                    handlingUI(it?.body()?.string())
                    }
                }
            }
        }
    }

    private fun apiCall(): Response? {
        val request = Request.Builder()
            .url(url)
            .build()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        var client = OkHttpClient.Builder()
            .addInterceptor(logging).build()

        return client.newCall(request).execute()
    }
}
private fun mappingResult(result: String?): ArrayList<String> {
    var listEmployee: ArrayList<String> = ArrayList()
    var jsonObj = JSONObject(result)
    var jsonArray = JSONArray(jsonObj.get("employee").toString())

    for (i in 0 until jsonArray.length()) {
        val gsonObj = Gson().fromJson<EmployeeModel>(jsonArray.get(i)?.toString(), EmployeeModel::class.java)

        listEmployee.add(gsonObj.name)
    }

    return listEmployee
}

@Composable
private fun handlingUI(result: String?) {
    val resultList = mappingResult(result)

    LazyColumnForIndexed(
        items = resultList
    ) {index, item ->
        Text(text = "$item")
    }

}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeAPIListingDemoTheme {
        Greeting("Android")
    }
}
package com.example.composeapilistingdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.lazy.LazyColumnItems
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.launchInComposition
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.unit.dp
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
                Surface(color = MaterialTheme.colors.background) {
                    Column(
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalGravity = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                    }
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
                    Surface(color = MaterialTheme.colors.background) {
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
        items = resultList,
    ) { index, item ->
        ContactCard(item)
    }
}

@Composable
fun ContactCard(name: String) {
    Card(shape = RoundedCornerShape(8.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp,
                start = 4.dp, end = 4.dp)) {
        Column(Modifier.fillMaxWidth()) {
            Text(text = "$name",
                    modifier = Modifier.padding(all = 8.dp))
        }
    }
}
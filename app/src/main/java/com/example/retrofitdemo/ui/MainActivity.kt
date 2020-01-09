package com.example.retrofitdemo.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.retrofitdemo.R
import com.example.retrofitdemo.model.PartData
import com.example.retrofitdemo.network.WebAccess
import com.example.retrofitdemo.ui.part.PartAdapter
import com.example.retrofitdemo.ui.part.PartDetailActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private val tag: String = MainActivity::class.java.simpleName

    private lateinit var adapter: PartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // rv_parts is the recyclerview UI element in the XML file
        rv_parts.layoutManager = LinearLayoutManager(this)
        // Create the adapter for the recycler view, which manages the contained items
        adapter =
            PartAdapter(listOf()) { partItem: PartData ->
                partItemClicked(partItem)
            }
        rv_parts.adapter = adapter
        // Start loading recycler view items from the web
        loadPartsAndUpdateList()
    }

    private fun loadPartsAndUpdateList() {
        // Launch Kotlin Coroutine on Android's main thread
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Execute web request through coroutine call adapter & retrofit
                val webResponse = WebAccess.partsApi.getPartsAsync().await()

                if (webResponse.isSuccessful) {
                    // Get the returned & parsed JSON from the web response.
                    // Type specified explicitly here to make it clear that we already
                    // get parsed contents.
                    val partList: List<PartData>? = webResponse.body()
                    Log.d(tag, partList.toString())
                    // Assign the list to the recycler view. If partsList is null,
                    // assign an empty list to the adapter.
                    adapter.partItemList = partList ?: listOf()
                    // Inform recycler view that data has changed.
                    // Makes sure the view re-renders itself
                    adapter.notifyDataSetChanged()
                } else {
                    // Print error information to the console
                    Log.d(tag, "Error ${webResponse.code()}")
                    Toast.makeText(this@MainActivity, "Error ${webResponse.code()}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: IOException) {
                Log.e(tag, "Exception " + e.printStackTrace())
                Toast.makeText(this@MainActivity, "Exception ${e.message}", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun partItemClicked(partItem : PartData) {
        // Test code to add a new item to the list
        // Will be replaced with UI function soon
        //val newPart = PartData(Random.nextLong(0, 999999), "Infrared sensor")
        //addPart(newPart)
        //return

        Toast.makeText(this, "Clicked: ${partItem.itemName}", Toast.LENGTH_LONG).show()

        // Launch second activity, pass part ID as string parameter
        val showDetailActivityIntent = Intent(this, PartDetailActivity::class.java)
        //showDetailActivityIntent.putExtra(Intent.EXTRA_TEXT, partItem.id.toString())
        showDetailActivityIntent.putExtra("ItemId", partItem.id)
        showDetailActivityIntent.putExtra("ItemName", partItem.itemName)
        startActivity(showDetailActivityIntent)
    }

    private fun addPart(partItem: PartData) {
        GlobalScope.launch(Dispatchers.Main) {
            val webResponse = WebAccess.partsApi.addPartAsync(partItem).await()
            Log.d(tag, "Add success: ${webResponse.isSuccessful}")
        }
    }


}

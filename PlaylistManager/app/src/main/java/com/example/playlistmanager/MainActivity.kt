package com.example.playlistmanager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.net.toUri
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.lifecycleScope
import com.example.playlistmanager.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


//secure intend
//move everything to view model
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //default first butt is checked
        binding.RadioGroup.check(binding.radioButton1.id)
        loadButtNames()

        //open playlist (saved in data store) connected to checked button
        binding.playButton.setOnClickListener{
            var readUri: String? = null
            var readTitle: String? = null
            lifecycleScope.launch{
                readUri = read(getCheckedId()+"_value")
                readTitle = read(getCheckedId()+"_key")
            }
            //lifecycleScope.launch(){

            //}
            if(readUri == null){
                Toast.makeText(applicationContext, "there is nothing under this button", Toast.LENGTH_SHORT).show()
            }
            else{
                val uri = readUri!!.toUri()
                val playIntent =  Intent(Intent.ACTION_VIEW, uri)
                startActivity(playIntent)
            }
        }

        //save playlist in data store and connect it to checked button
        binding.saveButton.setOnClickListener {
            if (binding.titlePlainText.text.length < 3) {
                Toast.makeText(
                    applicationContext,
                    "title need to be longer than 3 characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (binding.uriPlainText.text.length < 3) {
                Toast.makeText(
                    applicationContext,
                    "uri need to be longer  characters",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val key = getCheckedId() + "_key"          //title is key
                val value = getCheckedId() + "_value"      //uri is value
                lifecycleScope.launch {
                    save(key, binding.titlePlainText.text.toString())
                    save(value, binding.uriPlainText.text.toString())

                }
                Toast.makeText(applicationContext, "Data saved", Toast.LENGTH_SHORT).show()

                val id = binding.RadioGroup.checkedRadioButtonId
                val butt = findViewById<RadioButton>(id)
                butt.text = binding.titlePlainText.text

                binding.titlePlainText.text =null
                binding.uriPlainText.text=null
            }
        }
    }

    //save data in dataStore under key
    private suspend fun save(key: String, value: String){
        val dataStoreKey = stringPreferencesKey(key)
        dataStore.edit {
                settings -> settings[dataStoreKey] = value
        }
    }

    //read data from data store
    private suspend fun read(key: String):String?{
        val dataStoreKey = stringPreferencesKey(key)
        val preferences = dataStore.data.first()
        return preferences[dataStoreKey]
    }

    //return checked radio button ID as string
    private fun getCheckedId ():String{
        val buttCheckedId = binding.RadioGroup.checkedRadioButtonId
        return buttCheckedId.toString()
    }

    //invoke loadButtNames2 for every radioButt
    private fun loadButtNames(){
        loadButtNames2(binding.radioButton1)
        loadButtNames2(binding.radioButton2)
        loadButtNames2(binding.radioButton3)
        loadButtNames2(binding.radioButton4)
        loadButtNames2(binding.radioButton5)
        loadButtNames2(binding.radioButton6)
        loadButtNames2(binding.radioButton7)
    }

    //search for titles saved in dataStore
    //load titles and set them on buttons
    private fun loadButtNames2(rb: RadioButton){
        val titleKey = rb.id.toString() + "_key"
        val uriKey = rb.toString() + "_value"
        var readUri: String? = null
        var readTitle: String? = null

        lifecycleScope.launch{
            readUri = read(rb.id.toString() + "_value")
            readTitle = read(rb.id.toString() + "_key")
            //Toast.makeText(applicationContext, readTitle + "  "+ readUri, Toast.LENGTH_SHORT).show()
            if(readUri != null && readTitle != null){
                rb.text = readTitle
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
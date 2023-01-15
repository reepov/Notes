package com.reepovv.notes

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class NoteActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor
    private val json = jacksonObjectMapper()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_note)
        sharedPreferences = getSharedPreferences("NOTE", Context.MODE_PRIVATE)
        val back = findViewById<Button>(R.id.back)
        val titleView = findViewById<EditText>(R.id.title)
        val dateView = findViewById<TextView>(R.id.dateAndSymbols)
        val textView = findViewById<EditText>(R.id.text)
        val date = intent.getStringExtra("Date")
        val title = intent.getStringExtra("Title")
        val text = intent.getStringExtra("Text")
        val id = intent.getStringExtra("Id")?.toInt()!!
        val arrayString = sharedPreferences.getString("Array", "")!!
        val array : ArrayList<NotesModel> = if(arrayString != "") json.readValue(arrayString) else arrayListOf()
        titleView.setText(title)
        dateView.text = date
        textView.setText(text)
        back.setOnClickListener {
            val dateTime = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val dateString = dateTime.format(formatter)
            array[id].title = titleView.text.toString()
            array[id].text = textView.text.toString()
            array[id].date = dateString
            spEditor = sharedPreferences.edit()
            spEditor.putString("Array", json.writeValueAsString(array))
            println(json.writeValueAsString(array))
            spEditor.apply()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
            overridePendingTransition(0, 0)
        }
    }
}
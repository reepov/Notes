package com.reepovv.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spEditor: SharedPreferences.Editor
    private val json = jacksonObjectMapper()
    @SuppressLint("InflateParams", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedPreferences = getSharedPreferences("NOTE", Context.MODE_PRIVATE)
        val notesLayout = findViewById<LinearLayout>(R.id.notes)
        val addNote = findViewById<Button>(R.id.addNote)
        val arrayString = sharedPreferences.getString("Array", "")!!
        println(arrayString)
        val array : ArrayList<NotesModel> = if(arrayString != "") json.readValue(arrayString) else arrayListOf()
        var child : View
        array.forEach{
            child = layoutInflater.inflate(R.layout.note_item, null)
            val title = child.findViewById<TextView>(R.id.title)
            val text = child.findViewById<TextView>(R.id.text)
            val date = child.findViewById<TextView>(R.id.date)
            val id = it.id
            title.text = it.title
            text.text = if(it.text.length > 30) it.text.substring(0, 30) + "..." else it.text
            date.text = it.date
            child.setOnClickListener {itis ->
                val intent = Intent(this, NoteActivity::class.java)
                intent.putExtra("Date", date.text)
                intent.putExtra("Title", title.text)
                intent.putExtra("Text", it.text)
                intent.putExtra("Id", id.toString())
                println(id)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
            child.setOnLongClickListener {its ->
                val builder1: AlertDialog.Builder = AlertDialog.Builder(this)
                builder1.setCancelable(true)
                builder1.setPositiveButton("Удалить") {dialog, _ ->
                    array.removeAt(array.indexOf(it))
                    spEditor = sharedPreferences.edit()
                    spEditor.putString("Array", json.writeValueAsString(array))
                    spEditor.apply()
                    notesLayout.removeView(child)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    overridePendingTransition(0, 0)
                    dialog.cancel()
                }
                builder1.setNeutralButton("Отмена") { dialog, _ ->
                    dialog.cancel()
                }
                val alert11: AlertDialog = builder1.create()
                alert11.show()
                return@setOnLongClickListener true
            }
            notesLayout.addView(child)
        }
        addNote.setOnClickListener {
            val date = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
            val text = date.format(formatter)
            array.add(NotesModel(text, array.size, "", "", ))
            spEditor = sharedPreferences.edit()
            spEditor.putString("Array", json.writeValueAsString(array))
            spEditor.apply()
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra("Date", text)
            intent.putExtra("Title", "")
            intent.putExtra("Text", "")
            intent.putExtra("Id", array.last().id.toString())
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}
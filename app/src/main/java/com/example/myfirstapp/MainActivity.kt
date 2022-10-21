package com.example.myfirstapp

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.*
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val button = findViewById<ImageButton>(R.id.button)
        button.setOnClickListener(showPopUp(button))
    }

    private var words = listOf<String>()
    private var wordsCount = 0

    override fun onStart(){
        super.onStart()

        val inputStream: InputStream = resources.openRawResource(R.raw._out)
        val reader = BufferedReader(InputStreamReader(inputStream, Charset.forName("UTF-8")))

        words = reader.readLines()
        wordsCount = words.size
        iterate((0..wordsCount).random())
    }

    private fun iterate(r: Int) {
        val wordText = findViewById<TextView>(R.id.word)
        val translationText = findViewById<TextView>(R.id.translation)
        val exampleText = findViewById<TextView>(R.id.example)

        val word = words.elementAt(r).split("\t")


        var wrd = word[0]
        wrd = wrd.replace(' ', '_').replace('-', '_')
        if (wrd == "assert" || wrd == "float") {
            wrd += "_"
        }
        var resId = this.resources.getIdentifier(wrd, "raw", this.packageName)
        if(resId != 0){
            var mediaPlayer = MediaPlayer.create(this, resId)
            mediaPlayer.setOnCompletionListener { mp -> mp.release() }

            mediaPlayer.start()
        }

        wordText.text = word[0]
        translationText.text = word[1]
        if (word.size == 3)
        {
            exampleText.setText(Html.fromHtml(word[2].replace(word[0], "<font color='#FFFF00'>" + word[0] + "</font>")))
        }
        else{
            exampleText.text = ""
        }
        Handler().postDelayed({

            iterate((0..wordsCount).random())
        }, 2000)
    }

    private fun showPopUp(button: ImageButton?): (View) -> Unit = {
        val popupMenu: PopupMenu = PopupMenu(this, button)
        popupMenu.menuInflater.inflate(R.menu.menu, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.settings -> {
                    Settings()
                    true
                }
                else -> false
            }
        })
        popupMenu.show()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus){
            super.onStart()
        }
        else{
            super.onDestroy()
            exitProcess(0)
        }
    }

}
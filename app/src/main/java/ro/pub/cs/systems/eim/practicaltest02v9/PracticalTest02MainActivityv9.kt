package ro.pub.cs.systems.eim.practicaltest02v9

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PracticalTest02MainActivityv9 : AppCompatActivity() {
    private lateinit var wordText:EditText
    private lateinit var wordTextLitere : EditText
    private lateinit var button: Button
    private lateinit var textView: TextView
    private lateinit var autocompleteReceiver: AutocompleteReceiver
    private lateinit var serverButtton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_practical_test02v9_main)

        wordText = findViewById(R.id.editText)
        button = findViewById(R.id.button)
        textView = findViewById(R.id.textView)
        wordTextLitere = findViewById(R.id.editTextLitere)
        serverButtton = findViewById(R.id.serverButton)

        // when the server button is clicked, the MapsActivity is started
        serverButtton.setOnClickListener {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        autocompleteReceiver = AutocompleteReceiver { suggestions ->
            textView.text = suggestions
        }

        val filter = IntentFilter(AutocompleteReceiver.ACTION_AUTOCOMPLETE)
        registerReceiver(autocompleteReceiver, filter, RECEIVER_EXPORTED)

        button.setOnClickListener {
            val word = wordText.text.toString()
            val litere = wordTextLitere.text.toString()
            fetchAnagrams(word, litere.toInt())
        }

    }

    private fun fetchAnagrams(word: String, minLength: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = api.getAnagrams(word)
            val anagrams = response.body()?.all?.filter { it.length >= minLength } ?: emptyList()
            Log.d("MainActivity", "Received ${anagrams.size} anagrams for $word")
            val result = anagrams.joinToString(", ")
            Log.d("MainActivity", "Anagrams for $word: $result")
            sendBroadcast(Intent(AutocompleteReceiver.ACTION_AUTOCOMPLETE).apply {
                putExtra(AutocompleteReceiver.SUGGESTIONS, result)
            })
        }
    }

    class AutocompleteReceiver(
        private val onSuggestionsReceived: (String) -> Unit
    ) : BroadcastReceiver() {

        companion object {
            const val ACTION_AUTOCOMPLETE = "ACTION_AUTOCOMPLETE"
            const val SUGGESTIONS = "suggestions"
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_AUTOCOMPLETE) {
                val suggestions = intent.getStringExtra(SUGGESTIONS) ?: ""
                Log.d("AutocompleteReceiver", "Received suggestions: $suggestions")
                onSuggestionsReceived(suggestions)
            }
        }
    }
}
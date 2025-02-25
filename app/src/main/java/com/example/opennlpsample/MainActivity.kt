package com.example.opennlpsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.opennlpsample.ui.theme.OpenNlpSampleTheme
import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.tokenize.WhitespaceTokenizer
import java.io.InputStream

class MainActivity : ComponentActivity() {
    private lateinit var tagger: POSTaggerME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            loadPosModel()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("OpenNLP", "Error loading POS model: ${e.message}")
        }


        enableEdgeToEdge()
        setContent {

            var tokensWithTags by remember { mutableStateOf(emptyList<Pair<String, String>>()) }
            var mySentence by remember { mutableStateOf("") }
            val modifyMySentence = { sentence: String -> mySentence = sentence }

            OpenNlpSampleTheme {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = mySentence,
                        onValueChange =  modifyMySentence,
                        label = { Text("sentence") },
                        placeholder = { Text("insert text") },
                    )
                    Button(
                        onClick = {  tokensWithTags = extractTokensWithTags(mySentence) }
                    ) {
                        Text("Submit")
                    }
                        tokensWithTags.forEach { tuple ->
                            Text("${tuple.first} > ${tuple.second}")
                        }
                }
            }
        }

    }

    private fun loadPosModel() {
        val inputStream: InputStream = this.assets.open("opennlp-en-ud-ewt-pos-1.2-2.5.0.bin")
        val model = POSModel(inputStream)
        tagger = POSTaggerME(model)  // Initialize tagger once
        inputStream.close()  // Close after loading
    }

    private fun extractTokensWithTags(sentence: String) : List<Pair<String, String>>{
        val listTokensWithTags: MutableList<Pair<String, String>> = mutableListOf()
        val whiteSpaceTokenizer = WhitespaceTokenizer.INSTANCE
        val tokens = whiteSpaceTokenizer.tokenize(sentence)
        val tags = tagger.tag(tokens)  // Use the preloaded tagger
        tags?.let { listTokensWithTags.addAll(tokens?.zip(it) ?: emptyList()) }

        return listTokensWithTags
    }
}

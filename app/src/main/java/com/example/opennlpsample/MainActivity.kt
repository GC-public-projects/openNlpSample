package com.example.opennlpsample

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.opennlpsample.ui.theme.OpenNlpSampleTheme
import opennlp.tools.postag.POSModel
import opennlp.tools.postag.POSTaggerME
import opennlp.tools.tokenize.WhitespaceTokenizer
import java.io.InputStream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var tokens : Array<String>
        lateinit var tags : Array<String>

        try {
            val inputStream: InputStream = this.assets.open("opennlp-en-ud-ewt-pos-1.2-2.5.0.bin")
            val model = POSModel(inputStream)
            val tagger = POSTaggerME(model)
            val mySentence = "this is the way to tag tokens from a sentence"
            val whiteSpaceTokenizer = WhitespaceTokenizer.INSTANCE
            tokens = whiteSpaceTokenizer.tokenize(mySentence)
            tags = tagger.tag(tokens)
            inputStream.close()  // Always close the stream
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("OpenNLP", "Error loading POS model: ${e.message}")
        }


        enableEdgeToEdge()
        setContent {
            OpenNlpSampleTheme {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    tokens.zip(tags).forEach { (word, tag) ->
                        Text("$word > $tag")
                    }
                }
            }
        }
    }
}

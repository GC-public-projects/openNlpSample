# openNlpSample
use of Apache OpenNLP Tools lib in order to identify all the POS of the words of a text.

### Project status : Workable, documentation completed


## target audience
This project is for Kotlin Jetpack Compose initiated users.

## Presentation
`Apache OpenNLP Tools` can identify all the Part of speech of a text, unlike `Wordnet` that can only identify some ones but with more features than OpenNLP. 

## Overview

- 1 : text field empty
- 2 : all POS extracted

<img src="/screenshots/1.png" alt="text field empty" height="500">&emsp;
<img src="/screenshots/2.png" alt="all POS extracted" height="500">&emsp;


## Warning 
The versions less recent than`org.apache.opennlp:opennlp-tools:2.0.0` don't work with the POS models available at [OpenNLP models](https://opennlp.apache.org/models.html) and the versions more recent trigger an error `javax.xml.parsers.ParserConfigurationException` when `POSTaggerME(model)`is called. So the only version compatible with my project compiled with `sdk 35` is the `2.0.0`. 

OpenNLP is only compatible with Java 17. To make the project compatible with Java 17 with former versions than Android 13 the `desugaring` library needs to be implemented and used in the build.gradle file.


# Init

## Dependencies
In build.gradle.kts (app) add the following dependencies :
``` kotlin
dependencies {
	implementation ("org.apache.opennlp:opennlp-tools:2.0.0")
	coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.4")
}
```

### Java 17 implementation
In build.gradle.kts (app) modify these lines :
``` kotlin
compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
```

### POS model usage
The main of the functions from OPenNLP work with binaries models, so to be able to extract all POS from a string we need to download the dedicated model.


Create in `app/src/main` a folder `assets`, download the POS model we want (english one) from [OpenNLP models](https://opennlp.apache.org/models.html) and copy paste it in the folder.

# Code
Everything is coded in The `MainActivity` file to gain in understanding.
``` kotlin
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
```

## components explanations
Not that much code to explain.

- `val model = POSModel(inputStream)` : loading of the POS model from the assets in a POSModel object
- `tagger = POSTaggerME(model)` : The model is loaded in a POSTaggerME object
- `val whiteSpaceTokenizer = WhitespaceTokenizer.INSTANCE` & `val tokens = whiteSpaceTokenizer.tokenize(sentence)` : tokenization of the string in an array
- `val tags = tagger.tag(tokens) ` : all tags from the array of tokens recorded in another array 
- `tags?.let { listTokensWithTags.addAll(tokens?.zip(it) ?: emptyList()) }` : all values of the 2 arrays combined in pairs and added to `listTokensWithTags` list


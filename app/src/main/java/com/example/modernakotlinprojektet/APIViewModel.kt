package se.magictechnology.modernaandroidv4

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.modernakotlinprojektet.Searchresult
import com.example.modernakotlinprojektet.SuggestedDrink
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

class APIViewModel : ViewModel() {

    //Kommentarer till Bill
    //Tjena, tänkte skriva några korta rader om hur jag jobbat och problem jag stötte på här
    //jag utgick och byggde vyer utifrån navigeringen vi gick igenom för några lektioner sen,
    //bytte från chucknorris API:et till ett jag hittade om drinkrecept istället, fick problem som tog ett tag att lösa
    //JSON-parsningen misslyckades och appen kraschade när inte resultatet var en array(så på null och sträng då alltså)
    //försökte på olika sätt samt ställde frågor till chatgpt, till slut fick jag förslaget att ändra klassen Searchresult
    //från att bestå av lista till att vara JSONElement, då fick jag en ganska straightforward lösning som funkar stabilt vad jag kan se
    //utan överflödig kod(vad jag ser :) Ska tillägga att jag inte fått AI:n i Android Studio att fungera, den klagar på
    //att de inte är tillgänglig på min plats(sitter visserligen i Småland oftast, men njae gissar att det beror på något annat :)
    // så jag jag använde enbart chatgpt.com för projektet


    private val _suggestedDrink = MutableStateFlow<SuggestedDrink?>(null)
    val suggestedDrink: StateFlow<SuggestedDrink?> = _suggestedDrink.asStateFlow()

    private val _errormessage = MutableStateFlow<String?>(null)
    val errormessage: StateFlow<String?> = _errormessage.asStateFlow()

    fun searchDrink(searchedDrink: String) {
        Thread {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://www.thecocktaildb.com/api/json/v1/1/search.php?s=" + searchedDrink)
                .build()

            client.newCall(request).execute().use { response ->

                val jsonstring = response.body!!.string()
                Log.i("modernadebug", jsonstring)

                val searchresults =
                    Json { ignoreUnknownKeys = true }.decodeFromString<Searchresult>(jsonstring)

                val drinksList = when {
                    // If drinks is a JSON array, we deserialize it to a list of SuggestedDrink
                    searchresults.drinks is JsonArray -> {
                        searchresults.drinks.jsonArray.map {
                            it.jsonObject.toSuggestedDrink()
                        }
                    }
                    // If drinks is a string, handle it accordingly
                    searchresults.drinks is JsonPrimitive && searchresults.drinks.content == "no data found" -> {
                        emptyList<SuggestedDrink>() // or handle the case as you see fit
                    }
                    // If drinks is null, handle it accordingly
                    searchresults.drinks is JsonNull -> {
                        emptyList<SuggestedDrink>()
                    }
                    else -> {
                        emptyList<SuggestedDrink>()
                    }
                }

                Log.i("drinksList", drinksList.toString())

                if (drinksList.isNullOrEmpty()) {
                    _errormessage.value = "Inga drinkar funna"
                    _suggestedDrink.value = null
                } else {
                    _suggestedDrink.value = drinksList.first()
                    _errormessage.value = null
                }
            }
        }.start()
    }
}

// Add extension function to map JsonObject to SuggestedDrink
fun JsonObject.toSuggestedDrink(): SuggestedDrink {
    val strDrink = this["strDrink"]?.jsonPrimitive?.content ?: ""
    val strInstructions = this["strInstructions"]?.jsonPrimitive?.content ?: ""
    return SuggestedDrink(strDrink, strInstructions)
}
package com.example.modernakotlinprojektet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.modernakotlinprojektet.ui.theme.ModernaKotlinProjektetTheme
import se.magictechnology.modernaandroidv4.APIViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ModernaKotlinProjektetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNav()
                }
            }
        }
    }
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mainpage") {
        composable("mainpage") {
            MainPage(navController, "Den Moderna Appen")
        }
        composable("countpage") {
            CountPage(navController, "Addition och Subtraktion")
        }
    }
}

@Composable
fun MainPage(navController: NavController, name: String, modifier: Modifier = Modifier) {

    val viewModel: APIViewModel = viewModel()
    val suggestedDrink by viewModel.suggestedDrink.collectAsState()
    val errormessage by viewModel.errormessage.collectAsState()
    var searchtext by remember { mutableStateOf("") }

    Column {
        Spacer(modifier = Modifier.height(36.dp))
        Text(
            text = "$name!",
            modifier = modifier,
            fontSize = 34.sp
        )

        Button(onClick = { navController.navigate("countpage") }) {
            Text(text = "Gå till räknesidan")
        }

        if (errormessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(errormessage!!, fontSize = 24.sp, modifier = Modifier.padding(10.dp))
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(26.dp))
        Text("Drinkrecept", fontSize = 32.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Text("Sök efter en drink:")
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = searchtext,
            onValueChange = { searchtext = it },
            label = { Text("Search") }
        )
        Button(onClick = {
            viewModel.searchDrink(searchtext)
        }) {
            Text(text = "Search")
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (suggestedDrink != null) {
            Text(suggestedDrink!!.strDrink)
            Text(suggestedDrink!!.strInstructions)
        }
    }
}

@Composable
fun CountPage(navController: NavController, name: String) {
    val countingViewModel: CountingViewModel = viewModel()

    var count by remember {
        mutableStateOf(0)
    }

    count = countingViewModel.count
    Column {
        Spacer(modifier = Modifier.height(36.dp))
        Text(text = "$name!", fontSize = 34.sp)
        Button(onClick = { navController.navigate("mainpage") }) {
            Text("Tillbaka")
        }
        Modifier.fillMaxSize()
        Spacer(modifier = Modifier.height(36.dp))
        Text(text = "Count: $count", fontSize = 36.sp)

        Button(onClick = {
            countingViewModel.incCount()
            count = countingViewModel.count
        }) {
            Text(text = "Addera", fontSize = 16.sp)
        }

        Button(onClick = {
            countingViewModel.decCount()
            count = countingViewModel.count
        }) {
            Text(text = "Subtrahera", fontSize = 16.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ModernaKotlinProjektetTheme {
        AppNav()
    }
}
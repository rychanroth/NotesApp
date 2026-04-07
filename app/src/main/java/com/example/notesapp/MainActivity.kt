package com.example.notesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.notesapp.ui.screens.ForgotPasswordScreenPreview
import com.example.notesapp.ui.screens.LoginScreen
import com.example.notesapp.ui.screens.LoginScreenPreview
import com.example.notesapp.ui.screens.RegisterScreen
import com.example.notesapp.ui.screens.RegisterScreenPreview
import com.example.notesapp.ui.theme.NotesAppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NotesAppTheme {
                ForgotPasswordScreenPreview()
            }
        }
    }
}


@Composable
fun ShutTheFuckUp() {
    
}
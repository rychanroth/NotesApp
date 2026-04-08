package com.example.notesapp

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.notesapp.ui.screens.ForgotPasswordScreen
import com.example.notesapp.ui.screens.LoginScreen
import com.example.notesapp.ui.screens.NotesListScreen
import com.example.notesapp.ui.screens.RegisterScreen

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Register: Screen("register")
    object ForgotPassword: Screen("forgot_password")
    object NotesList: Screen("notes_list")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // App starts here
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.NotesList.route) {
                        // Clear back stack so user can't go back to login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Register Screen
        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.NotesList.route) {
                        // Clear back stack so user can't go back to login
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Forgot Password Screen
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Notes List Screen
        composable(Screen.NotesList.route) {
            NotesListScreen(
                onLogOut = {
                    navController.navigate(Screen.Login.route) {
                        // Clear back stack so user can't go back to login
                        popUpTo(Screen.NotesList.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
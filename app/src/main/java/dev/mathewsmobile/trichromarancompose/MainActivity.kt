package dev.mathewsmobile.trichromarancompose

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import dev.mathewsmobile.trichromarancompose.ui.screen.CameraScreen
import dev.mathewsmobile.trichromarancompose.ui.screen.CameraScreenRoute
import dev.mathewsmobile.trichromarancompose.ui.screen.GalleryScreen
import dev.mathewsmobile.trichromarancompose.ui.screen.GalleryScreenRoute
import dev.mathewsmobile.trichromarancompose.ui.screen.ViewImageRoute
import dev.mathewsmobile.trichromarancompose.ui.screen.ViewImageScreen
import dev.mathewsmobile.trichromarancompose.ui.theme.TriChromaranComposeTheme
import dev.mathewsmobile.trichromarancompose.viewmodel.CameraViewModel
import dev.mathewsmobile.trichromarancompose.viewmodel.GalleryViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        checkAndRequestPermission()
        setContent {
            TriChromaranComposeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = CameraScreenRoute) {
                    composable<CameraScreenRoute> {
                        val viewModel: CameraViewModel by viewModels()
                        CameraScreen(
                            modifier = Modifier,
                            viewModel = viewModel,
                            onNavigateToGallery = {
                                navController.navigate(GalleryScreenRoute)
                            }
                        )
                    }

                    composable<GalleryScreenRoute> {
                        val viewModel by viewModels<GalleryViewModel>()
                        GalleryScreen(
                            modifier = Modifier,
                            viewModel = viewModel,
                            onViewImage = { image ->
                                navController.navigate(ViewImageRoute(image.path, image.takenAt))
                            }
                        )
                    }

                    composable<ViewImageRoute> { backstackEntry ->
                        val route = backstackEntry.toRoute<ViewImageRoute>()
                        ViewImageScreen(
                            modifier = Modifier,
                            imagePath = route.imagePath,
                            timestamp = route.timestamp,
                            onDismiss = {
                                navController.popBackStack()
                            },
                            onShare = { imageUri ->
                                val shareIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    // Example: content://com.google.android.apps.photos.contentprovider/...
                                    putExtra(Intent.EXTRA_STREAM, imageUri)
                                    type = "image/jpeg"
                                }
                                startActivity(Intent.createChooser(shareIntent, null))
                            }
                        )
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted, proceed with file writing.
//                takePicturesAndCombine(...) // Call your function here
            } else {
                // Permission denied.  Handle the denial gracefully.
                Toast.makeText(this, "Permission denied. Cannot save image.", Toast.LENGTH_SHORT).show()
                // You might want to:
                // - Disable the feature that requires the permission.
                // - Show an explanation to the user and allow them to retry.
                // - Direct the user to the app settings to grant the permission manually.
            }
        }

    private fun checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // You have the permission, proceed with writing to external storage
            } else {
                // Request the permission
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        else{
            //WRITE_EXTERNAL_STORAGE is not needed
//            takePicturesAndCombine(...) // Call your function here
        }
    }
}

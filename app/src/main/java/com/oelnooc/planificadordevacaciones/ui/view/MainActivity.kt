package com.oelnooc.planificadordevacaciones.ui.view

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oelnooc.planificadordevacaciones.R
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar
import com.oelnooc.planificadordevacaciones.data.local.repository.LugarRepository
import com.oelnooc.planificadordevacaciones.data.local.room.AppDatabase
import com.oelnooc.planificadordevacaciones.data.remote.repository.IndicadorClientRepo
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaAgregarLugar
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaDetalle
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaEditarLugar
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaPrincipal
import com.oelnooc.planificadordevacaciones.ui.theme.PlanificadorDeVacacionesTheme
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModel
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModelFactory
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: LugarViewModel
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = "PlanificadorDeVacaciones/1.0"

        enableEdgeToEdge()

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val cameraPermissionGranted = permissions[Manifest.permission.CAMERA] == true
            if (!cameraPermissionGranted) {
                Toast.makeText(this, "Se requiere acceso a la cámara", Toast.LENGTH_SHORT).show()
            }
        }

        takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && photoUri != null) {
                Log.d("MainActivity", "Foto tomada con éxito: ${photoUri.toString()}")
                val lugarSeleccionado = viewModel.lugarSeleccionado.value
                if (lugarSeleccionado != null) {
                    viewModel.actualizarFotoUsuario(lugarSeleccionado.id, photoUri.toString())
                    viewModel.loadLugarById(lugarSeleccionado.id)
                } else {
                    Log.e("MainActivity", "No se seleccionó un lugar para asignar la foto")
                }
            } else {
                Log.e("MainActivity", "Error al tomar la foto")
            }
        }



        val lugarRepository = LugarRepository(AppDatabase.getDatabase(application).lugarDao())
        val indicadorRepo = IndicadorClientRepo()

        val factory = LugarViewModelFactory(lugarRepository, indicadorRepo)
        viewModel = ViewModelProvider(this, factory)[LugarViewModel::class.java]

        setContent {
            PlanificadorDeVacacionesTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "pantalla_principal",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("pantalla_principal") {
                            App(
                                viewModel = viewModel,
                                onAñadirClick = {
                                    navController.navigate("pantalla_agregar")
                                },
                                navController = navController
                            )
                        }

                        composable("pantalla_agregar") {
                            PantallaAgregarLugar(
                                onGuardar = { lugar ->
                                    viewModel.agregarLugar(lugar)
                                    navController.popBackStack()
                                },
                                onCancelar = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable("pantalla_editar/{lugarId}") { backStackEntry ->
                            val lugarId = backStackEntry.arguments?.getString("lugarId")?.toIntOrNull()
                            if (lugarId != null) {
                                PantallaEditarLugar(
                                    lugarId = lugarId,
                                    onGuardar = { lugar ->
                                        viewModel.actualizarLugar(lugar)
                                        navController.popBackStack()
                                    },
                                    onCancelar = {
                                        navController.popBackStack()
                                    },
                                    viewModel = viewModel
                                )
                            }
                        }

                        composable("pantalla_detalle/{lugarId}") { backStackEntry ->
                            val lugarId = backStackEntry.arguments?.getString("lugarId")?.toIntOrNull()
                            val lugar = viewModel.obtenerLugarPorId(lugarId ?: 0)

                            if (lugar != null) {
                                PantallaDetalle(
                                    lugar = lugar,
                                    onEditarClick = {
                                        navController.navigate("pantalla_editar/${lugar.id}")
                                    },
                                    onEliminarClick = {
                                        viewModel.eliminarLugar(lugar)
                                        navController.popBackStack()
                                    },
                                    onTomarFotoClick = {
                                        viewModel.seleccionarLugar(lugar)
                                        capturePhoto()
                                    },
                                    viewModel = viewModel
                                )
                            } else {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun capturePhoto() {
        if (checkPermissions()) {
            val photoFile = createImageFile()
            if (photoFile != null) {
                val localPhotoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
                photoUri = localPhotoUri
                takePictureLauncher.launch(localPhotoUri)
            } else {
                Toast.makeText(this, "Error al crear el archivo de imagen", Toast.LENGTH_SHORT).show()
            }
        } else {
            permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }


    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        return cameraPermission == PackageManager.PERMISSION_GRANTED
    }

    private fun createImageFile(): File? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        } catch (e: IOException) {
            Log.e("MainActivity", "Error al crear el archivo de imagen", e)
            null
        }
    }
}

@Composable
fun App(
    modifier: Modifier = Modifier,
    viewModel: LugarViewModel,
    onAñadirClick: () -> Unit,
    navController: NavController
) {
    val lugares by viewModel.lugares.collectAsState(initial = emptyList())
    val context = LocalContext.current

    PantallaPrincipal(
        lugares = lugares,
        onLugarClick = { lugar ->
            navController.navigate("pantalla_detalle/${lugar.id}")
        },
        onEditarClick = { lugar ->
            navController.navigate("pantalla_editar/${lugar.id}")
        },
        onEliminarClick = { lugar ->
            viewModel.eliminarLugar(lugar)
        },
        onGeolocalizacionClick = { lugar ->
            showMapDialog(lugar, viewModel, context)
        },
        onAniadirClick = {
            navController.navigate("pantalla_agregar")
        },
        viewModel
    )
}

fun showMapDialog(lugar: Lugar, viewModel: LugarViewModel, context: Context) {
    val (lat, lon) = viewModel.abrirGeolocalizacion(lugar)

    val dialog = AlertDialog.Builder(context).apply {
        setView(R.layout.dialog_map)
        setCancelable(true)
    }.create()

    dialog.show()

    val mapView: MapView = dialog.findViewById(R.id.map_view)
    val marker = Marker(mapView)
    marker.position = GeoPoint(lat, lon)
    Log.d("posicion", viewModel.abrirGeolocalizacion(lugar).toString())
    marker.title = lugar.nombre

    mapView.setTileSource(TileSourceFactory.MAPNIK)
    mapView.controller.setZoom(15)
    mapView.controller.setCenter(GeoPoint(lat, lon))
    mapView.overlayManager.add(marker)

    val closeButton: Button = dialog.findViewById(R.id.close_button)
    closeButton.setOnClickListener {
        dialog.dismiss()
    }
}
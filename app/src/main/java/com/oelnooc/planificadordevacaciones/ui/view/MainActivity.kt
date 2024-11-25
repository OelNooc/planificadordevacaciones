package com.oelnooc.planificadordevacaciones.ui.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.oelnooc.planificadordevacaciones.data.local.repository.LugarRepository
import com.oelnooc.planificadordevacaciones.data.local.room.AppDatabase
import com.oelnooc.planificadordevacaciones.data.remote.repository.IndicadorClientRepo
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaAgregarEditar
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaAgregarLugar
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaEditarLugar
import com.oelnooc.planificadordevacaciones.ui.screens.PantallaPrincipal
import com.oelnooc.planificadordevacaciones.ui.theme.PlanificadorDeVacacionesTheme
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModel
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var viewModel: LugarViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val lugarRepository = LugarRepository(AppDatabase.getDatabase(application).lugarDao())
        val indicadorRepo = IndicadorClientRepo()

        val factory = LugarViewModelFactory(lugarRepository, indicadorRepo)
        viewModel = ViewModelProvider(this, factory)[LugarViewModel::class.java]

        setContent {
            PlanificadorDeVacacionesTheme {
                val navController = rememberNavController()  // Aquí creamos el NavController
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "pantalla_principal",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Pantalla principal
                        composable("pantalla_principal") {
                            App(
                                viewModel = viewModel,
                                onAñadirClick = {
                                    navController.navigate("pantalla_agregar")
                                },
                                navController = navController // Pasamos el navController
                            )
                        }

                        // Pantalla de agregar lugar
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

                        // Pantalla de editar lugar
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
                    }
                }
            }
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
            viewModel.seleccionarLugar(lugar)
        },
        onEditarClick = { lugar ->
            navController.navigate("pantalla_editar/${lugar.id}")
        },
        onEliminarClick = { lugar ->
            viewModel.eliminarLugar(lugar)
        },
        onGeolocalizacionClick = { lugar ->
            val geoUri = viewModel.abrirGeolocalizacion(lugar)
            val intent = Intent(Intent.ACTION_VIEW, geoUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            context.startActivity(intent)
        },
        onAñadirClick = {
            navController.navigate("pantalla_agregar")
        }
    )
}
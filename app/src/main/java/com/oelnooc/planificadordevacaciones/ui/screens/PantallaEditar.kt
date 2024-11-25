package com.oelnooc.planificadordevacaciones.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModel

@Composable
fun PantallaEditarLugar(
    lugarId: Int,
    onGuardar: (Lugar) -> Unit,
    onCancelar: () -> Unit,
    viewModel: LugarViewModel
) {
    val lugar = viewModel.obtenerLugarPorId(lugarId) // Obtener el lugar desde el ViewModel usando el ID

    // Si no encontramos el lugar, mostramos un mensaje o no hacemos nada
    if (lugar == null) {
        Text("Lugar no encontrado")
        return
    }

    var nombre by remember { mutableStateOf(lugar.nombre) }
    var foto by remember { mutableStateOf(lugar.foto) }
    var orden by remember { mutableIntStateOf(lugar.orden) }  // Orden inicial del lugar
    var costoPorNoche by remember { mutableStateOf(lugar.costoPorNoche) }
    var traslados by remember { mutableStateOf(lugar.traslados) }
    var comentarios by remember { mutableStateOf(lugar.comentarios) }
    var ubicacion by remember { mutableStateOf(lugar.ubicacion) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Editar Lugar", style = MaterialTheme.typography.bodySmall)

        // Campos del formulario
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del lugar") }
        )
        OutlinedTextField(
            value = foto,
            onValueChange = { foto = it },
            label = { Text("URL de la foto") }
        )
        // Campo de orden (tipo Int)
        OutlinedTextField(
            value = orden.toString(),
            onValueChange = { newValue ->
                orden = newValue.toIntOrNull() ?: 0  // Si no es un número válido, asigna 0
            },
            label = { Text("Orden") }
        )
        OutlinedTextField(
            value = costoPorNoche,
            onValueChange = { costoPorNoche = it },
            label = { Text("Costo por Noche") }
        )
        OutlinedTextField(
            value = traslados,
            onValueChange = { traslados = it },
            label = { Text("Costo Traslados") }
        )
        OutlinedTextField(
            value = comentarios,
            onValueChange = { comentarios = it },
            label = { Text("Comentarios") }
        )
        OutlinedTextField(
            value = ubicacion,
            onValueChange = { ubicacion = it },
            label = { Text("Ubicación") }
        )

        // Botón guardar
        Button(onClick = {
            val lugarActualizado = Lugar(
                id = lugar.id,  // Mantenemos el ID original
                nombre = nombre,
                foto = foto,
                costoPorNoche = costoPorNoche,
                traslados = traslados,
                comentarios = comentarios,
                ubicacion = ubicacion,
                orden = orden,
                null
            )
            onGuardar(lugarActualizado)
        }) {
            Text("Guardar")
        }

        // Botón cancelar
        Button(onClick = onCancelar) {
            Text("Cancelar")
        }
    }
}

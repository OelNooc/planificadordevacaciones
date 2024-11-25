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
    val lugar = viewModel.obtenerLugarPorId(lugarId)

    if (lugar == null) {
        Text("Lugar no encontrado")
        return
    }

    var nombre by remember { mutableStateOf(lugar.nombre) }
    var foto by remember { mutableStateOf(lugar.foto) }
    var orden by remember { mutableIntStateOf(lugar.orden) }
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
        OutlinedTextField(
            value = orden.toString(),
            onValueChange = { newValue ->
                orden = newValue.toIntOrNull() ?: 0
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

        Button(onClick = {
            val lugarActualizado = Lugar(
                id = lugar.id,
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

        Button(onClick = onCancelar) {
            Text("Cancelar")
        }
    }
}

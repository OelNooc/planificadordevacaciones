package com.oelnooc.planificadordevacaciones.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.rememberAsyncImagePainter
import com.oelnooc.planificadordevacaciones.R
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModel
import com.oelnooc.planificadordevacaciones.util.convertirCLPtoUSD
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PantallaDetalle(
    lugar: Lugar,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onTomarFotoClick: () -> Unit,
    viewModel: LugarViewModel
) {
    val context = LocalContext.current
    val mapaView = remember { MapView(context) }

    val lugarActualizado = viewModel.lugarSeleccionado.collectAsState().value

    val indicadores = viewModel.indicadores.collectAsState()

    if (indicadores.value == null) {
        CircularProgressIndicator()
        return
    }

    val valorDolar = viewModel.getDolarValue()

    val costoPorNocheUSD = convertirCLPtoUSD(lugar.costoPorNoche.toDouble(), valorDolar)
    val trasladoUSD = convertirCLPtoUSD(lugar.traslados.toDouble(), valorDolar)

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = lugar.nombre, style = MaterialTheme.typography.labelLarge )

        Image(
            painter = rememberAsyncImagePainter(lugar.foto),
            contentDescription = "Imagen del lugar",
            modifier = Modifier.fillMaxWidth().height(200.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Costo por noche: ${lugar.costoPorNoche} CLP - $costoPorNocheUSD USD",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Traslado: ${lugar.traslados} CLP - $trasladoUSD USD",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Comentarios", style = MaterialTheme.typography.bodyMedium)
        Text(text = lugar.comentarios, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = onTomarFotoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.camara_fotografica),
                    contentDescription = "Tomar foto",
                    tint = Color.Unspecified
                )
            }
            IconButton(onClick = onEditarClick) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onEliminarClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }

        Image(
            painter = rememberAsyncImagePainter(lugarActualizado?.fotoUsuario ?: lugar.foto),
            contentDescription = "Imagen del lugar",
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Ubicación", style = MaterialTheme.typography.labelMedium)

        Spacer(modifier = Modifier.height(16.dp))

        MapViewContainer(lugar = lugar, mapaView = mapaView)
    }
}

@Composable
fun MapViewContainer(lugar: Lugar, mapaView: MapView) {
    Box(modifier = Modifier.fillMaxSize().height(250.dp)) {
        AndroidView(
            factory = { mapaView },
            update = {
                val (lat, lon) = lugar.ubicacion.split(",").map { it.trim().toDoubleOrNull() ?: 0.0 }
                it.setTileSource(TileSourceFactory.MAPNIK)
                it.controller.setZoom(15)
                it.controller.setCenter(GeoPoint(lat, lon))
                val marker = Marker(it)
                marker.position = GeoPoint(lat, lon)
                marker.title = lugar.nombre
                it.overlayManager.add(marker)
            }
        )
    }
}

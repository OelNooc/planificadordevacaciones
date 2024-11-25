package com.oelnooc.planificadordevacaciones.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.oelnooc.planificadordevacaciones.data.local.model.Lugar
import com.oelnooc.planificadordevacaciones.ui.viewmodel.LugarViewModel
import com.oelnooc.planificadordevacaciones.util.convertirCLPtoUSD

@Composable
fun PantallaPrincipal(
    lugares: List<Lugar>,
    onLugarClick: (Lugar) -> Unit,
    onEditarClick: (Lugar) -> Unit,
    onEliminarClick: (Lugar) -> Unit,
    onGeolocalizacionClick: (Lugar) -> Unit,
    onAniadirClick: () -> Unit,
    lugarViewModel: LugarViewModel
) {
    Scaffold(
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lugares) { lugar ->
                        LugarCard(
                            lugar = lugar,
                            onLugarClick = { onLugarClick(lugar) },
                            onEditarClick = { onEditarClick(lugar) },
                            onEliminarClick = { onEliminarClick(lugar) },
                            onGeolocalizacionClick = { onGeolocalizacionClick(lugar) },
                            lugarViewModel = lugarViewModel
                        )
                    }
                }

                Text(
                    text = "Presiona para añadir un lugar",
                    color = Color.Blue,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAniadirClick() }
                        .padding(16.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    )
}

@Composable
fun LugarCard(
    lugar: Lugar,
    onLugarClick: () -> Unit,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit,
    onGeolocalizacionClick: () -> Unit,
    lugarViewModel: LugarViewModel
) {
    val indicadores = lugarViewModel.indicadores.collectAsState()

    if (indicadores.value == null) {
        CircularProgressIndicator()
        return
    }

    val valorDolar = lugarViewModel.getDolarValue()

    val costoPorNocheUSD = convertirCLPtoUSD(lugar.costoPorNoche.toDouble(), valorDolar)
    val trasladoUSD = convertirCLPtoUSD(lugar.traslados.toDouble(), valorDolar)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onLugarClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = lugar.foto),
                    contentDescription = lugar.nombre,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = lugar.nombre, style = MaterialTheme.typography.titleMedium)
                    Text(text = "Costo por noche: ${lugar.costoPorNoche} CLP - $costoPorNocheUSD USD")
                    Text(text = "Traslado: ${lugar.traslados} CLP - $trasladoUSD USD")
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onGeolocalizacionClick() }) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Geolocalización")
                }
                IconButton(onClick = { onEditarClick() }) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = { onEliminarClick() }) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
package com.dbApp.approom

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dbApp.approom.data.Compra
import com.dbApp.approom.data.CompraDatabase
import com.dbApp.approom.data.CompraRepository
import com.dbApp.approom.screens.FormularioScreen
import com.dbApp.approom.ui.theme.AppRoomTheme

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.*
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            iniciarNotificacionesPeriodicas()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Solicitar permisos de notificación en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                iniciarNotificacionesPeriodicas()
            }
        } else {
            iniciarNotificacionesPeriodicas()
        }

        enableEdgeToEdge()
        setContent {
            AppRoomTheme {
                val context = LocalContext.current
                val database = remember { CompraDatabase.getDatabase(context) }
                val repository = remember { CompraRepository(database.compraDao()) }
                val viewModel: CompraViewModel = viewModel(
                    factory = CompraViewModelFactory(repository)
                )

                AppNavigation(viewModel)
            }
        }
    }


    private fun iniciarNotificacionesPeriodicas() {
        // Configurar WorkManager para notificaciones periódicas cada 15 minutos (mínimo permitido)
        val workRequest = PeriodicWorkRequestBuilder<NotificacionWorker>(
            15, TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "notificaciones_compras",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }


    @Composable
    fun AppNavigation(viewModel: CompraViewModel) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "lista") {
            composable("lista") {
                ListaComprasScreen(
                    viewModel = viewModel,
                    onAgregarClick = { navController.navigate("formulario") }
                )
            }
            composable("formulario") {
                FormularioScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ListaComprasScreen(
        viewModel: CompraViewModel,
        onAgregarClick: () -> Unit
    ) {
        val compras by viewModel.compras.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Compras del día") }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = onAgregarClick) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar compra")
                }
            }
        ) { padding ->
            if (compras.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay compras registradas")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(compras) { compra ->
                        CompraItem(compra)
                    }
                }
            }
        }
    }

    @Composable
    fun CompraItem(compra: Compra) {
        var expanded by remember { mutableStateOf(false) }

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Compra ${compra.id}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowRight,
                        contentDescription = if (expanded) "Contraer" else "Expandir"
                    )
                }

                AnimatedVisibility(visible = expanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("Comprador: ${compra.comprador}")
                        Text("Productos: ${compra.productos}")
                        Text("Precio Total: S/. ${String.format("%.2f", compra.precioTotal)}")
                        Text("Fecha: ${compra.fecha}")
                    }
                }
            }
        }
    }
}

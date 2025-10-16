package com.example.huerto_hogar.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.huerto_hogar.data.LocalDataRepository

/**
 * Pantalla principal del administrador con navegación por pestañas
 */
@Composable
fun AdminDashboard() {
    val currentUser by LocalDataRepository.currentUser.collectAsState()
    var selectedTabIndex by remember { mutableStateOf(0) }

    // Verificar que el usuario es administrador
    if (currentUser?.role != com.example.huerto_hogar.data.UserRole.ADMIN) {
        UnauthorizedAccess()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de bienvenida
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2E7D32).copy(alpha = 0.9f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🔧 Panel de Administración",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Bienvenido, ${currentUser?.name}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        // Pestañas de navegación
        val tabs = listOf(
            AdminTab("Usuarios", Icons.Filled.People),
            AdminTab("Productos", Icons.Filled.ShoppingCart),
            AdminTab("Reportes", Icons.Filled.Analytics)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White.copy(alpha = 0.9f),
            contentColor = Color(0xFF2E7D32)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(tab.title) },
                    icon = { Icon(tab.icon, contentDescription = tab.title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Contenido de las pestañas
        when (selectedTabIndex) {
            0 -> UserManagementScreen()
            1 -> ProductManagementScreen()
            2 -> ReportsScreen()
        }
    }
}

@Composable
fun UnauthorizedAccess() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD32F2F).copy(alpha = 0.9f)
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Filled.Lock,
                contentDescription = "Acceso denegado",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "⚠️ Acceso No Autorizado",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No tienes permisos para acceder a esta sección.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

data class AdminTab(
    val title: String,
    val icon: ImageVector
)
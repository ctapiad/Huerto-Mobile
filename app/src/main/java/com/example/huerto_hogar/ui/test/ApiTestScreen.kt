package com.example.huerto_hogar.ui.test

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.huerto_hogar.network.NetworkTestHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiTestScreen(onBack: () -> Unit) {
    var testResult by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧪 Test de APIs") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Prueba de Conexión",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Verifica que los microservicios estén funcionando",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botón de prueba
            Button(
                onClick = {
                    isLoading = true
                    testResult = ""
                    NetworkTestHelper.testConnection { success, message ->
                        testResult = message
                        isSuccess = success
                        isLoading = false
                    }
                },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Probando conexión...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Probar Conexión")
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Resultado
            if (testResult.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSuccess) 
                            Color(0xFFE8F5E9) 
                        else 
                            Color(0xFFFFEBEE)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isSuccess) 
                                    Icons.Default.CheckCircle 
                                else 
                                    Icons.Default.Error,
                                contentDescription = null,
                                tint = if (isSuccess) 
                                    Color(0xFF4CAF50) 
                                else 
                                    Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isSuccess) "Éxito" else "Error",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSuccess) 
                                    Color(0xFF2E7D32) 
                                else 
                                    Color(0xFFC62828)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Text(
                            text = testResult,
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Información adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "ℹ️ Información",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "• Usuarios: http://10.0.2.2:8081",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "• Productos: http://10.0.2.2:8082",
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "Nota: 10.0.2.2 es la IP especial del emulador para localhost. En dispositivo físico, cambia a la IP de tu Mac.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Tests individuales
            TestSection(title = "Test de Login") {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }
                var loginResult by remember { mutableStateOf("") }
                var loginLoading by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loginLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !loginLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        loginLoading = true
                        NetworkTestHelper.testLogin(email, password) { success, message ->
                            loginResult = message
                            loginLoading = false
                        }
                    },
                    enabled = email.isNotEmpty() && password.isNotEmpty() && !loginLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Probar Login")
                }
                
                if (loginResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = loginResult,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (loginResult.contains("✅")) 
                            Color(0xFF2E7D32) 
                        else 
                            Color(0xFFC62828)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TestSection(title = "Test de Búsqueda de Productos") {
                var query by remember { mutableStateOf("") }
                var searchResult by remember { mutableStateOf("") }
                var searchLoading by remember { mutableStateOf(false) }
                
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Buscar producto") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !searchLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Button(
                    onClick = {
                        searchLoading = true
                        NetworkTestHelper.testProductSearch(query) { success, count, message ->
                            searchResult = message
                            searchLoading = false
                        }
                    },
                    enabled = query.isNotEmpty() && !searchLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Buscar")
                }
                
                if (searchResult.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = searchResult,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (searchResult.contains("✅")) 
                            Color(0xFF2E7D32) 
                        else 
                            Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

@Composable
fun TestSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

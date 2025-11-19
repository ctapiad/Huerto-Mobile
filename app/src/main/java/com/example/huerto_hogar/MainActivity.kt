package com.example.huerto_hogar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.huerto_hogar.data.LocalDataRepository
import com.example.huerto_hogar.data.model.User
import com.example.huerto_hogar.data.enums.UserRole
import com.example.huerto_hogar.viewmodel.CartViewModel
import com.example.huerto_hogar.ui.auth.LoginScreen
import com.example.huerto_hogar.ui.store.HomeScreen
import com.example.huerto_hogar.ui.store.ProductListScreen
import com.example.huerto_hogar.ui.store.CartScreen
import com.example.huerto_hogar.ui.admin.AdminDashboard
import com.example.huerto_hogar.ui.user.UserProfileScreen
import com.example.huerto_hogar.ui.order.PaymentSuccessScreen
import com.example.huerto_hogar.ui.theme.HuertoHogarTheme
import com.example.huerto_hogar.network.NetworkTestHelper
import kotlinx.coroutines.launch

object Routes {
    const val HOME = "home"
    const val LOGIN = "login"
    const val PRODUCTS = "products"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val CART = "cart"
    const val USER_PROFILE = "user_profile"
    const val PAYMENT_SUCCESS = "payment_success"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate started")
        
        try {
            // 🧪 TEST: Probar conexión con microservicios (comentado para evitar crash)
            // testMicroservicesConnection()
            
            Log.d("MainActivity", "Setting content")
            setContent {
                HuertoHogarTheme {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = R.drawable.huertohogarfondo),
                            contentDescription = "Fondo de la aplicación",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        AppNavigation()
                    }
                }
            }
            Log.d("MainActivity", "Content set successfully")
        } catch (e: Exception) {
            Log.e("MainActivity", "Fatal error in onCreate", e)
        }
    }
    
    private fun testMicroservicesConnection() {
        NetworkTestHelper.testConnection { success, message ->
            Log.d("NetworkTest", "=".repeat(50))
            Log.d("NetworkTest", "TEST DE CONEXIÓN CON MICROSERVICIOS")
            Log.d("NetworkTest", "=".repeat(50))
            Log.d("NetworkTest", message)
            Log.d("NetworkTest", "=".repeat(50))
            
            // Mostrar resultado en Toast
            runOnUiThread {
                Toast.makeText(
                    this,
                    if (success) "✅ APIs conectadas correctamente" else "❌ Error de conexión",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentUser by LocalDataRepository.currentUser.collectAsState()
    
    // ViewModel compartido del carrito para toda la aplicación
    val sharedCartViewModel: CartViewModel = viewModel()

    // Ahora el splash screen se maneja automáticamente por el sistema Android
    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color.Black.copy(alpha = 0.5f),
        drawerContent = {
            AppDrawerContent(
                currentUser = currentUser,
                onNavigate = { route ->
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                },
                onLogout = {
                    LocalDataRepository.logout()
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        // --- SOLUCIÓN: SCAFFOLD CENTRALIZADO ---
        // Este Scaffold gestiona la UI principal, como la TopAppBar.
        Scaffold(
            topBar = {
                // La barra superior ahora vive aquí, de forma central.
                TopAppBar(
                    title = { Text("Huerto Hogar") },
                    navigationIcon = {
                        // El botón de menú que abre el Drawer.
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    },
                    // Le damos un color semi-transparente para que se integre con el fondo.
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White
                    )
                )
            },
            // El fondo del Scaffold debe ser transparente para dejar ver la imagen de fondo.
            containerColor = Color.Transparent
        ) { innerPadding ->
            // El NavHost (que cambia las pantallas) ahora vive dentro del Scaffold.
            NavHost(
                navController = navController,
                startDestination = Routes.HOME,
                // Aplicamos el padding de la TopAppBar para que el contenido no quede debajo de ella.
                modifier = Modifier.padding(innerPadding)
            ) {
                // HomeScreen ya no necesita gestionar el menú, solo existir.
                // Esta llamada ahora es correcta porque HomeScreen() ya no tiene parámetros.
                composable(Routes.HOME) {
                    HomeScreen(
                        onNavigateToCart = {
                            navController.navigate(Routes.CART)
                        },
                        cartViewModel = sharedCartViewModel
                    )
                }

                // Las otras pantallas no se ven afectadas, pero se mostrarán
                // debajo de la TopAppBar gracias al padding.
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = { user ->
                            if (user.role == UserRole.ADMIN) {
                                navController.navigate(Routes.ADMIN_DASHBOARD) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            } else {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                // Pantalla de productos con filtros por categoría
                composable(Routes.PRODUCTS) {
                    ProductListScreen(
                        onLoginRequired = {
                            navController.navigate(Routes.LOGIN)
                        },
                        onNavigateToCart = {
                            navController.navigate(Routes.CART)
                        },
                        cartViewModel = sharedCartViewModel
                    )
                }

                // Pantalla de carrito de compras
                composable(Routes.CART) {
                    CartScreen(
                        onLoginRequired = { navController.navigate(Routes.LOGIN) },
                        onCheckout = { 
                            // Navegación manejada por onPaymentSuccess
                        },
                        onPaymentSuccess = { orderNumber, totalAmount, orderId ->
                            navController.navigate("${Routes.PAYMENT_SUCCESS}/$orderNumber/$totalAmount/$orderId")
                        },
                        cartViewModel = sharedCartViewModel
                    )
                }

                // Pantalla de perfil de usuario
                composable(Routes.USER_PROFILE) {
                    UserProfileScreen(
                        onLoginRequired = { navController.navigate(Routes.LOGIN) }
                    )
                }
                
                // Dashboard de administrador - solo accesible para administradores
                composable(Routes.ADMIN_DASHBOARD) {
                    AdminDashboard()
                }
                
                // Pantalla de confirmación de pago exitoso
                composable("${Routes.PAYMENT_SUCCESS}/{orderNumber}/{totalAmount}/{orderId}") { backStackEntry ->
                    val orderNumber = backStackEntry.arguments?.getString("orderNumber") ?: ""
                    val totalAmount = backStackEntry.arguments?.getString("totalAmount") ?: ""
                    val orderId = backStackEntry.arguments?.getString("orderId")?.toLongOrNull() ?: 0L
                    PaymentSuccessScreen(
                        onNavigateToOrders = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) { inclusive = false }
                            }
                        },
                        totalAmount = totalAmount,
                        orderNumber = orderNumber,
                        orderId = orderId
                    )
                }
                

            }
        }
    }
}

@Composable
fun AppDrawerContent(currentUser: User?, onNavigate: (String) -> Unit, onLogout: () -> Unit) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Huerto Hogar", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))
            HorizontalDivider()

            NavigationDrawerItem(label = { Text("Inicio") }, selected = false, onClick = { onNavigate(Routes.HOME) })
            NavigationDrawerItem(label = { Text("Productos") }, selected = false, onClick = { onNavigate(Routes.PRODUCTS) })
            NavigationDrawerItem(label = { Text("Mi Carrito") }, selected = false, onClick = { onNavigate(Routes.CART) })

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (currentUser == null) {
                NavigationDrawerItem(label = { Text("Iniciar Sesión") }, selected = false, onClick = { onNavigate(Routes.LOGIN) })
            } else {
                // Opciones para usuarios logueados
                NavigationDrawerItem(label = { Text("Mi Perfil") }, selected = false, onClick = { onNavigate(Routes.USER_PROFILE) })
                
                if (currentUser.role == UserRole.ADMIN) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    NavigationDrawerItem(label = { Text("Dashboard Admin") }, selected = false, onClick = { onNavigate(Routes.ADMIN_DASHBOARD) })
                }
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                NavigationDrawerItem(label = { Text("Cerrar Sesión") }, selected = false, onClick = onLogout)
            }
        }
    }
}

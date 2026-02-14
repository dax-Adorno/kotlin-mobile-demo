package com.trabajofinal.educacionfiscal
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.trabajofinal.educacionfiscal.network.ApiService
import com.trabajofinal.educacionfiscal.network.RetrofitClient
import com.trabajofinal.educacionfiscal.network.models.LoginRequest
import com.trabajofinal.educacionfiscal.network.models.RegistrarRequest
import com.trabajofinal.educacionfiscal.ui.theme.EducacionFiscalTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.trabajofinal.educacionfiscal.network.models.Categoria
import com.trabajofinal.educacionfiscal.network.models.Glosario
import com.trabajofinal.educacionfiscal.network.models.Registros
import com.trabajofinal.educacionfiscal.network.models.Tutorial
import com.trabajofinal.educacionfiscal.network.models.Vencimiento
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import com.trabajofinal.educacionfiscal.network.models.Ejemplo
import com.trabajofinal.educacionfiscal.network.models.RegistrosRequest
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.trabajofinal.educacionfiscal.network.models.Impuestos
import com.trabajofinal.educacionfiscal.network.models.RegistrosResponse
import androidx.compose.foundation.lazy.items


@OptIn(ExperimentalAnimationApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EducacionFiscalTheme {
                val navController = rememberNavController()
                MainNavigation(navController)
            }
        }
    }
}
@Composable
fun MainNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable(route = "splash") { SplashScreen(navController) }
        composable(route = "bienvenida") { PantallaBienvenida(navController) }
        composable(route = "menu") {
            PantallaPrincipal(
                onNavigate = { navController.navigate(it) },
                navController = navController
            )
        }


        // NUEVA RUTA: pantalla de elección de tipo de actividad
        composable(route = "eleccionActividad") {
            PantallaEleccionActividad { tipo ->
                navController.navigate("calcular/$tipo")
            }
        }
        composable(
            "tutorial_unico/{idTutorial}",
            arguments = listOf(navArgument("idTutorial") { type = NavType.IntType })
        ) { backStackEntry ->
            val idTutorial = backStackEntry.arguments?.getInt("idTutorial") ?: 0
            PantallaTutorialUnico(idTutorial = idTutorial)
        }


        composable(route = "calcular") {
            PantallaCalcular(navController)
        }

        composable(
            route = "resultado/{idRegistro}",
            arguments = listOf(navArgument("idRegistro") { type = NavType.IntType })
        ) { backStackEntry ->
            val idRegistro = backStackEntry.arguments?.getInt("idRegistro") ?: 0
            PantallaResultado(
                idRegistro = idRegistro,
                navController = navController  //  aquí pasamos navController
            )
        }

        composable(
            route = "resultadoEjemplo/{idEjemplo}",
            arguments = listOf(navArgument("idEjemplo") { type = NavType.IntType })
        ) { backStackEntry ->
            val idEjemplo = backStackEntry.arguments?.getInt("idEjemplo") ?: 0
            PantallaResultadoEjemplo(idEjemplo = idEjemplo, navController = navController)
        }

        composable ( route = "glosarios" ){PantallaGlosario()}
        composable(route = "historial") { PantallaHistorial(navController) }
        composable(route = "vencimientos") { PantallaVencimientos() }
        composable(route = "tutorial") { PantallaTutorial() }
        composable(route = "register") { PantallaRegister() }
        composable(route = "login") { PantallaLogin(navController) }
        composable(route = "ejemplos") { PantallaEjemplos(navController) }

    }
}



    @Composable
    fun SplashScreen(navController: NavController) {
        // Estado para controlar la animación
        var startAnimation by remember { mutableStateOf(false) }

        // Lanza la animación
        LaunchedEffect(Unit) {
            startAnimation = true
            delay(3000) // Duración total del splash
            navController.navigate("bienvenida") {
                popUpTo("splash") { inclusive = true } // No volver atrás
            }
        }

        // Animaciones: rotación y opacidad
        val rotation by animateFloatAsState(
            targetValue = if (startAnimation) 360f else 0f,
            animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
            label = ""
        )

        val alpha by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0f,
            animationSpec = tween(durationMillis = 2500),
            label = ""
        )

        // Animación de fondo degradado en movimiento
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val colorShift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )

        val gradientColors = listOf(
            Color(0xFFB3E5FC),
            Color(0xFF81D4FA),
            Color(0xFF4FC3F7),
            Color(0xFF29B6F6)
        )

        val animatedColor = lerp(gradientColors[0], gradientColors[3], colorShift)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(animatedColor, Color.White)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Imagen giratoria
            Image(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "Logo Educación Fiscal",
                modifier = Modifier
                    .size(180.dp)
                    .graphicsLayer(rotationZ = rotation, alpha = alpha)
            )

            // Título de la app con fade in/out
            AnimatedVisibility(
                visible = startAnimation,
                enter = fadeIn(animationSpec = tween(1800)),
                exit = fadeOut(animationSpec = tween(1000))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(220.dp))
                    Text(
                        text = "EDUCACIÓN FISCAL",
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = Color(0xFF01579B)
                    )
                }
            }
        }
    }

    @Composable
    fun ModernButton(text: String, onClick: () -> Unit) {
        var pressed by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.95f else 1f,
            animationSpec = tween(durationMillis = 150, easing = FastOutLinearInEasing),
            label = ""
        )

        Button(
            onClick = {
                pressed = true
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(55.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF1565C0)
            ),
            shape = RoundedCornerShape(30.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }



    // =================== PANTALLA BIENVENIDA =================

    @Composable
    fun PantallaBienvenida(navController: NavController) {
        // Animación de fondo con transición infinita de color
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val colorShift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )

        val gradientStart = lerp(Color(0xFF90CAF9), Color(0xFFBBDEFB), colorShift)
        val gradientEnd = lerp(Color(0xFF2196F3), Color(0xFF1976D2), colorShift)

        // Animación del logo
        var startAnimation by remember { mutableStateOf(false) }
        val scale by animateFloatAsState(
            targetValue = if (startAnimation) 1f else 0.8f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            label = ""
        )

        LaunchedEffect(Unit) {
            startAnimation = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(gradientStart, gradientEnd)
                    )
                )
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Logo con animación de escala
                Image(
                    painter = painterResource(id = R.drawable.ic_logo),
                    contentDescription = "Logo Educación Fiscal",
                    modifier = Modifier
                        .size(150.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Título centrado
                Text(
                    text = "EDUCACIÓN FISCAL",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Aprende, Calcula y Crece",
                    fontSize = 18.sp,
                    color = Color(0xFFE3F2FD)
                )

                Spacer(modifier = Modifier.height(80.dp)) // Esto baja los botones

                // Botones centrados
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModernButton("Iniciar Sesión") {
                        navController.navigate("login")
                    }
                    ModernButton("Registrarse") {
                        navController.navigate("register")
                    }
                    ModernButton("Entrar sin cuenta") {
                        navController.navigate("tutorial")
                    }
                }

                Spacer(modifier = Modifier.height(60.dp))

                // Texto inferior con redes sociales
                Text(
                    text = "Síguenos en redes sociales:",
                    color = Color.White,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    IconoRedSocial(R.drawable.ic_facebook, "Facebook") {}
                    IconoRedSocial(R.drawable.ic_instagram, "Instagram") {}
                    IconoRedSocial(R.drawable.ic_twitter, "Twitter") {}
                }
            }
        }
    }


    // ==================== LOGIN ==============================

    @Composable
    fun PantallaLogin(navController: NavController) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val contexto = LocalContext.current
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5FF))
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Iniciar sesión",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF001F54)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black)

            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(0.9f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black)
            )

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val api = RetrofitClient.instance.create(ApiService::class.java)
                            val response = api.login(LoginRequest(email, password))

                            if (response.isSuccessful) {
                                val body = response.body()
                                val nombreUsuario = body?.user?.nombre ?: "usuario"

                                Toast.makeText(
                                    contexto,
                                    "Bienvenido $nombreUsuario",
                                    Toast.LENGTH_LONG
                                ).show()

                                // Guardar token JWT
                                body?.token?.let { token ->
                                    val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    prefs.edit { putString("jwt_token", token) }

                                }
                                // Guardamos el id de usuario como Int
                                body?.user?.id_usuario?.let { userId ->
                                    val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                    prefs.edit { putInt("user_id", userId) }
                                }


                                // Navegar al menú principal
                                navController.navigate("menu") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Toast.makeText(
                                    contexto,
                                    "Error: ${response.code()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                contexto,
                                "Error de conexión: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC)),
                modifier = Modifier.fillMaxWidth(0.9f)
            ) {
                Text("Iniciar sesión", color = Color.White)
            }
        }
    }

    // =================== REGISTRO =============================
    @Composable
    fun PantallaRegister() {
        var nombre by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val contexto = LocalContext.current
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5FF))
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Crear cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF001F54)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black)
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black)
            )

            Button(
                onClick = {
                    scope.launch {
                        try {
                            val api = RetrofitClient.instance.create(ApiService::class.java)
                            val response =
                                api.registrarUsuario(RegistrarRequest(nombre, email, password))

                            if (response.isSuccessful) {
                                Toast.makeText(contexto, "✅ Registro exitoso", Toast.LENGTH_LONG)
                                    .show()
                            } else {
                                Toast.makeText(
                                    contexto,
                                    "❌ Error: ${response.code()}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                contexto,
                                "⚠️ Error de conexión: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC))
            ) {
                Text("Crear cuenta", color = Color.White)
            }
        }
    }

    // =================== MENU PRINCIPAL ======================
    @Composable
    fun PantallaPrincipal(onNavigate: (String) -> Unit, navController: NavController) {
        val contexto = LocalContext.current

        // Animación del fondo (gradiente dinámico)
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val shift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 5000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )

        val colorTop = lerp(Color(0xFF2196F3), Color(0xFF64B5F6), shift)
        val colorBottom = lerp(Color(0xFF1976D2), Color(0xFF42A5F5), shift)

        // Animación de entrada (fade + scale)
        var visible by remember { mutableStateOf(false) }
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 1200),
            label = ""
        )
        val scale by animateFloatAsState(
            targetValue = if (visible) 1f else 0.95f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            label = ""
        )

        LaunchedEffect(Unit) {
            visible = true
        }

        // Fondo con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(colors = listOf(colorTop, colorBottom))
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(alpha = alpha, scaleX = scale, scaleY = scale)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Texto de bienvenida
                Text(
                    text = "¡Bienvenido a tu espacio de\nEducación Fiscal!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Lista de botones
                Column(
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ModernMenuButton("🧮 Calcular impuestos") { onNavigate("calcular") }
                    ModernMenuButton("📜 Ver historial") { onNavigate("historial") }
                    ModernMenuButton("📅 Ver vencimientos") { onNavigate("vencimientos") }
                    ModernMenuButton("📘 Ver glosario") { onNavigate("glosarios") }
                    ModernMenuButton("💡 Ver ejemplos") { onNavigate("ejemplos") }
                    ModernMenuButton("🎓 Ver video tutoriales") { onNavigate("tutorial") }

                    //  Nuevo botón "Cerrar sesión"
                    Button(
                        onClick = {
                            // Eliminar token e ID de usuario
                            val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            prefs.edit().clear().apply()

                            // Redirigir a la pantalla de bienvenida
                            navController.navigate("bienvenida") {
                                popUpTo("menu") { inclusive = true } // Evita volver con el botón atrás
                            }

                            Toast.makeText(contexto, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .height(60.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD32F2F), // rojo fuerte
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(40.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "🚪 Cerrar sesión",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }

@Composable
    fun ModernMenuButton(text: String, onClick: () -> Unit) {
        var pressed by remember { mutableStateOf(false) }

        val scale by animateFloatAsState(
            targetValue = if (pressed) 0.96f else 1f,
            animationSpec = tween(durationMillis = 150),
            label = ""
        )

        Button(
            onClick = {
                pressed = true
                onClick()
            },
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(60.dp)
                .graphicsLayer(scaleX = scale, scaleY = scale),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color(0xFF0D47A1)
            ),
            shape = RoundedCornerShape(40.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
    // =================== OTROS COMPONENTES ===================
    @Composable
    fun IconoRedSocial(iconRes: Int, contentDesc: String, onClick: () -> Unit) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDesc,
            modifier = Modifier
                .size(48.dp)
                .clickable { onClick() }
        )
    }
@Composable
fun PantallaEleccionActividad(
    onSeleccionar: (String) -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val colorTop = lerp(Color(0xFFB39DDB), Color(0xFF81D4FA), shift)
    val colorBottom = lerp(Color(0xFF7E57C2), Color(0xFF0288D1), shift)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "💡 Escoja una opción",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Opción 1
            ModernOptionButton(
                text = "🛒 Me dedico a la venta de productos",
                onClick = { onSeleccionar("productos") },
                color = Color(0xFFBA68C8)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Opción 2
            ModernOptionButton(
                text = "🧰 Me dedico a los servicios",
                onClick = { onSeleccionar("servicios") },
                color = Color(0xFF64B5F6)
            )
        }
    }
}

@Composable
fun ModernOptionButton(
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    // 1. Create and remember an InteractionSource
    val interactionSource = remember { MutableInteractionSource() }

    // 2. Observe the pressed state from the interaction source
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3. Animate the scale based on the pressed state
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = tween(100),
        label = "scaleAnimation"
    )

    Button(
        onClick = onClick, // Keep the original onClick logic
        colors = ButtonDefaults.buttonColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer(
                scaleX = scale,
                scaleY = scale
            )
            .clip(RoundedCornerShape(16.dp)),
        elevation = ButtonDefaults.buttonElevation(10.dp),
        // 4. Pass the interactionSource to the Button
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center
        )
    }
}
// =========================================================
// ============ Pantalla Calcular (con conexión API) =======
// =========================================================
@Composable
fun PantallaCalcular(
    navController: NavController
) {
    // Fondo animado (gradiente azul-violeta)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFFBBDEFB), Color(0xFFE1BEE7), shift)
    val colorBottom = lerp(Color(0xFF5C6BC0), Color(0xFF8E24AA), shift)

    // Estados del formulario
    var ingresos by remember { mutableStateOf("") }
    var superficie by remember { mutableStateOf("") }
    var energia by remember { mutableStateOf("") }
    var alquiler by remember { mutableStateOf("") }
    var tieneLocal by remember { mutableStateOf(false) }
    var alquila by remember { mutableStateOf(false) }
    var vendeProductos by remember { mutableStateOf(false) }

    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    //  Efecto de “latido” del botón Calcular
    val infiniteScale = rememberInfiniteTransition(label = "")
    val pulsate by infiniteScale.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 800, easing = LinearEasing),
            RepeatMode.Reverse
        ),
        label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom)))
            .padding(24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Título principal
            Text(
                text = "💰 Calculá tu aporte mensual",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // ===================== CAMPOS =====================

            AnimatedInputField(
                value = ingresos,
                onValueChange = { ingresos = it },
                label = "Ingresos Brutos anuales"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = tieneLocal, onCheckedChange = {
                    tieneLocal = it
                    if (!it) {
                        superficie = ""
                        energia = ""
                        alquila = false
                        alquiler = ""
                    }
                })
                Text("¿Tiene local?", color = Color.White)
            }

            // Solo se habilita si tieneLocal == true
            AnimatedInputField(
                value = superficie,
                onValueChange = { superficie = it },
                label = "Superficie del local (m²)",
                enabled = tieneLocal
            )

            AnimatedInputField(
                value = energia,
                onValueChange = { energia = it },
                label = "Energía eléctrica consumida (KW)",
                enabled = tieneLocal
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = alquila, onCheckedChange = {
                    alquila = it
                    if (!it) alquiler = ""
                })
                Text("¿Alquila?", color = Color.White)
            }

            // Solo se habilita si tieneLocal && alquila == true
            AnimatedInputField(
                value = alquiler,
                onValueChange = { alquiler = it },
                label = "Monto total del alquiler anual",
                enabled = tieneLocal && alquila
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text("¿En qué se basa su negocio?", fontWeight = FontWeight.Medium, color = Color.White)

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !vendeProductos,
                    onClick = { vendeProductos = false }
                )
                Text("Servicios", color = Color.White)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = vendeProductos,
                    onClick = { vendeProductos = true }
                )
                Text("Venta de productos", color = Color.White)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ===================== BOTÓN =====================
            Button(
                onClick = {
                    scope.launch {
                        try {
                            val api = RetrofitClient.instance.create(ApiService::class.java)
                            val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                            val token = prefs.getString("jwt_token", null)
                            val idUsuario = prefs.getInt("user_id", -1)

                            if (token == null || idUsuario == -1) {
                                Toast.makeText(contexto, "Error: usuario no autenticado", Toast.LENGTH_LONG).show()
                                return@launch
                            }

                            val registro = RegistrosRequest(
                                id_usuario = idUsuario,
                                ingresos_brutos = ingresos.toDoubleOrNull() ?: 0.0,
                                superficie_afectada = superficie.toDoubleOrNull() ?: 0.0,
                                energia_electrica = energia.toDoubleOrNull() ?: 0.0,
                                alquileres = alquiler.toDoubleOrNull() ?: 0.0,
                                muebles = vendeProductos
                            )

                            val response = api.guardarRegistro("Bearer $token", registro)

                            if (response.isSuccessful) {
                                val idRegistro = response.body()?.id_registro ?: 0
                                Toast.makeText(contexto, "Registro creado (ID: $idRegistro)", Toast.LENGTH_LONG).show()
                                navController.navigate("resultado/$idRegistro")
                            } else {
                                Toast.makeText(contexto, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                            }

                        } catch (e: Exception) {
                            Toast.makeText(contexto, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC)),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(scaleX = pulsate, scaleY = pulsate),
                elevation = ButtonDefaults.buttonElevation(10.dp)
            ) {
                Text("Calcular", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AnimatedInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    enabled: Boolean = true // controlado desde afuera
) {
    var focused by remember { mutableStateOf(false) }

    val borderColor by animateColorAsState(
        targetValue = if (focused) Color(0xFF8E24AA) else Color.White.copy(alpha = 0.6f),
        animationSpec = tween(400),
        label = ""
    )
    val backgroundColor by animateColorAsState(
        targetValue = if (focused) Color.White.copy(alpha = 0.9f) else Color.White.copy(alpha = 0.7f),
        animationSpec = tween(400),
        label = ""
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        enabled = enabled, //  se habilita o deshabilita según la condición del padre
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .onFocusChanged { focused = it.isFocused },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            disabledBorderColor = Color.Gray.copy(alpha = 0.4f),
            focusedContainerColor = backgroundColor,
            unfocusedContainerColor = backgroundColor,
            disabledContainerColor = Color.LightGray.copy(alpha = 0.3f),
            focusedLabelColor = Color(0xFF6A1B9A),
            cursorColor = Color(0xFF8E24AA),
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black

            )
    )
}


@Composable
fun ExpandableResultButton(
    title: String,
    value: String,
    explanation: String // Nuevo parámetro para el texto desplegable
) {
    // Estado para controlar el despliegue del texto
    var expanded by remember { mutableStateOf(false) }

    // Efecto de "Glow" (elevación y escala) en el botón
    val glow by animateFloatAsState(
        targetValue = if (expanded) 15f else 0f, // Más glow cuando está expandido
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (expanded) 0.98f else 1f, // Ligeramente presionado al expandir
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = ""
    )

    // Animación de color del texto del título
    val titleColor by animateColorAsState(
        targetValue = if (expanded) Color(0xFF00ACC1) else Color(0xFF0277BD),
        animationSpec = tween(300),
        label = ""
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .graphicsLayer(scaleX = scale, scaleY = scale) // Aplicar animación de escala
    ) {
        // 1. El Botón Principal (Header)
        Button(
            onClick = { expanded = !expanded }, // Alternar el estado al hacer clic
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .graphicsLayer {
                    shadowElevation = glow // Aplicar animación de glow
                },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black // Color por defecto
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                    modifier = Modifier.weight(1f) // Ocupa el espacio restante
                )

                // Texto del valor (a la derecha)
                Text(
                    text = value,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF006064)
                )

                // Icono de flecha animado
                Icon(
                    // Cambiamos painterResource por el objeto Icons.Filled
                    imageVector = if (expanded)
                        Icons.Filled.KeyboardArrowUp // Usamos una flecha hacia arriba
                    else
                        Icons.Filled.KeyboardArrowDown, // Usamos una flecha hacia abajo

                    contentDescription = if (expanded) "Colapsar" else "Expandir",
                    tint = titleColor,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        // 2. El Contenido Desplegable (Explicación)
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(animationSpec = tween(500)), // Animación de despliegue suave
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-5).dp) // Superponer ligeramente sobre el botón
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F7FA).copy(alpha = 0.95f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Text(
                    text = explanation,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF006064)
                )
            }
        }
    }
}


    // =================== pantalla para ver Resultado ==================
    @Composable
    fun PantallaResultado(
        idRegistro: Int,
        navController: NavController
    ) {
        val contexto = LocalContext.current
        val scope = rememberCoroutineScope()

        // Fondo animado
        val infiniteTransition = rememberInfiniteTransition(label = "")
        val shift by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(durationMillis = 6000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )
        val colorTop = lerp(Color(0xFF00BCD4), Color(0xFF80DEEA), shift)
        val colorBottom = lerp(Color(0xFF0097A7), Color(0xFF006064), shift)

        // Estados
        var registro by remember { mutableStateOf<Registros?>(null) }
        var categoria by remember { mutableStateOf<Categoria?>(null) }
        var impuestos by remember { mutableStateOf<List<Impuestos>>(emptyList()) }
        var cargando by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        // Cargar registro, categorías e impuestos
        LaunchedEffect(idRegistro) {
            scope.launch {
                try {
                    val api = RetrofitClient.instance.create(ApiService::class.java)
                    val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val token = prefs.getString("jwt_token", null)

                    if (token.isNullOrEmpty()) {
                        error = "No se encontró token de autenticación."
                        cargando = false
                        return@launch
                    }

                    // Obtener registro
                    val responseReg = api.obtenerRegistro("Bearer $token", idRegistro)
                    if (!responseReg.isSuccessful || responseReg.body() == null) {
                        error = "Error al obtener el registro."
                        cargando = false
                        return@launch
                    }
                    registro = responseReg.body()!!

                    // Obtener categorías
                    val responseCat = api.obtenerCategorias("Bearer $token")
                    if (!responseCat.isSuccessful || responseCat.body() == null) {
                        error = "Error al obtener las categorías."
                        cargando = false
                        return@launch
                    }
                    val categorias = responseCat.body()!!.sortedBy { it.id_categoria }

                    // Determinar categoría
                    categoria = categorias.find {
                        registro!!.ingresos_brutos <= it.ingresos_brutos &&
                                registro!!.superficie_afectada <= it.superficie_afectada &&
                                registro!!.energia_electrica <= it.energia_electrica &&
                                registro!!.alquileres <= it.alquileres
                    }

                    // Obtener impuestos
                    val responseImpuestos = api.obtenerImpuestos("Bearer $token")
                    if (responseImpuestos.isSuccessful && responseImpuestos.body() != null) {
                        impuestos = responseImpuestos.body()!!
                    }

                } catch (e: Exception) {
                    error = "Error de conexión: ${e.message}"
                } finally {
                    cargando = false
                }
            }
        }

        // Animaciones de entrada
        var visible by remember { mutableStateOf(false) }
        val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(1000), label = "")
        val offsetY by animateDpAsState(targetValue = if (visible) 0.dp else 50.dp, animationSpec = tween(1000), label = "")
        LaunchedEffect(Unit) { visible = true }

        // UI
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(colorTop, colorBottom)))
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            when {
                cargando -> CircularProgressIndicator(color = Color.White)
                error != null -> Text(text = error ?: "Error desconocido", color = Color.White, fontSize = 18.sp)
                registro != null -> {
                    val reg = registro!!
                    LazyColumn(
                        modifier = Modifier
                            .graphicsLayer(alpha = alpha)
                            .offset(y = offsetY)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        contentPadding = PaddingValues(vertical = 24.dp)
                    ) {
                        item {
                            Text(
                                text = "📊 Resultados del cálculo",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                        }

                        // Categoría
                        item {
                            if (categoria != null) {
                                val cat = categoria!!
                                ExpandableResultButton(
                                    title = "Categoría",
                                    value = cat.nombre,
                                    explanation = "Categoría determinada según tus ingresos, superficie, energía y alquileres."
                                )
                                val montoMensual = if (reg.muebles) cat.total_muebles else cat.total
                                ExpandableResultButton(
                                    title = "Monto mensual",
                                    value = "$${"%.2f".format(montoMensual)}",
                                    explanation = if (reg.muebles)
                                        "Incluye componente impositivo para venta de productos."
                                    else
                                        "Incluye componente impositivo para servicios."
                                )
                            } else {
                                Text(
                                    "No califica para las categorías del monotributo",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // Impuestos
                        if (impuestos.isNotEmpty()) {
                            items(impuestos) { impuesto ->
                                val porcentaje = impuesto.porcentaje ?: 0.0
                                val fijo = impuesto.monto_fijo ?: 0.0
                                val valor = reg.ingresos_brutos * (porcentaje / 100) + fijo

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    ExpandableResultButton(
                                        title = impuesto.nombre,
                                        value = "$${"%.2f".format(valor)}",
                                        explanation = impuesto.descripcion
                                    )
                                    Button(
                                        onClick = { navController.navigate("tutorial_unico/${impuesto.id_tutorial}") },
                                        modifier = Modifier
                                            .padding(vertical = 4.dp)
                                            .fillMaxWidth(0.6f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC))
                                    ) {
                                        Text("Ver tutorial", color = Color.White)
                                    }
                                }
                            }
                        } else {
                            item {
                                Text(
                                    "No hay impuestos adicionales",
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }

                        // Botón volver
                        item {
                            Spacer(modifier = Modifier.height(32.dp))
                            Button(
                                onClick = { navController.navigate("menu") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC)),
                                modifier = Modifier
                                    .fillMaxWidth(0.8f)
                                    .padding(top = 16.dp)
                            ) {
                                Text("Volver al menú", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }






@Composable
fun PantallaHistorial(navController: NavController) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fondo animado
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )

    val colorTop = lerp(Color(0xFFE1BEE7), Color(0xFFF3E5F5), colorShift)
    val colorBottom = lerp(Color(0xFF8E24AA), Color(0xFF4A148C), colorShift)

    // Estados
    var registros by remember { mutableStateOf<List<Registros>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Llamada a la API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)
                val user = prefs.getInt("user_id", -1) // Guardado en Login

                if (token == null || user == -1) {
                    error = "No se encontró token o usuario."
                    cargando = false
                    return@launch
                }

                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerRegistros("Bearer $token") // GET /registros
                if (!response.isSuccessful || response.body() == null) {
                    error = "Error al obtener los registros."
                    cargando = false
                    return@launch
                }

                // Filtrar registros del usuario logueado
                registros = response.body()!!.filter { it.id_usuario == user }
                cargando = false

            } catch (e: Exception) {
                error = "Error de conexión: ${e.message}"
                cargando = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.Center
    ) {
        when {
            cargando -> CircularProgressIndicator(color = Color.White)
            error != null -> Text(text = error ?: "Error desconocido", color = Color.White, fontSize = 18.sp)
            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "📜 Historial de Cálculos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 20.dp)
                    )
                }

                itemsIndexed(registros) { index, reg ->
                    AnimatedHistoryCard(
                        registro = reg,
                        delayMillis = index * 200
                    ) { idRegistro ->
                        navController.navigate("resultado/$idRegistro") // envía el ID
                    }
                }

            }
        }
    }
}

@Composable
fun AnimatedHistoryCard(
    registro: Registros,
    delayMillis: Int = 0,
    onClick: (Int) -> Unit   // Recibe el id_registro al hacer clic
) {
    var visible by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
        label = ""
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 50.dp,
        animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
        label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = ""
    )

    LaunchedEffect(Unit) { visible = true }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(vertical = 8.dp)
            .graphicsLayer(
                alpha = alpha,
                translationY = offsetY.value,
                scaleX = scale,
                scaleY = scale
            )
            .clickable {
                pressed = true
                onClick(registro.id_registro) // Envía el id al hacer clic
            },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Ingresos: $${registro.ingresos_brutos}",
                color = Color(0xFF4A148C),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "Superficie afectada: ${registro.superficie_afectada} m²",
                color = Color(0xFF4A148C),
                fontSize = 14.sp
            )
            Text(
                text = "Energía eléctrica: ${registro.energia_electrica} KW",
                color = Color(0xFF4A148C),
                fontSize = 14.sp
            )
            Text(
                text = "Alquiler anual: $${registro.alquileres}",
                color = Color(0xFF4A148C),
                fontSize = 14.sp
            )
            Text(
                text = "Modelo de negocio: ${if (registro.muebles) "Venta de productos" else "Servicios"}",
                color = Color(0xFF4A148C),
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Ver detalle ▸",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}



// =========================================================
// =============== PANTALLA VENCIMIENTOS ====================
// =========================================================
@Composable
fun PantallaVencimientos() {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fondo degradado animado
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF7986CB), Color(0xFFB39DDB), colorShift)
    val colorBottom = lerp(Color(0xFF303F9F), Color(0xFF512DA8), colorShift)

    // Estados
    var vencimientos by remember { mutableStateOf<List<Vencimiento>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Lógica: obtener token y llamar API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)

                if (token.isNullOrEmpty()) {
                    errorMessage = "Token no encontrado. Inicie sesión nuevamente."
                    isLoading = false
                    return@launch
                }

                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerVencimientos("Bearer $token")

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()

                    // Ordenar por fecha más cercana
                    val formatoPosibles = listOf("yyyy-MM-dd", "dd/MM/yyyy")
                    val formato = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    vencimientos = lista.sortedBy { v ->
                        formatoPosibles.firstNotNullOfOrNull { f ->
                            try {
                                SimpleDateFormat(f, Locale.getDefault()).parse(v.fecha_vencimiento)
                            } catch (e: Exception) {
                                null
                            }
                        } ?: Date(Long.MAX_VALUE)
                    }
                } else {
                    errorMessage = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Diseño
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = Color.White)
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📅 Próximos Vencimientos",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (vencimientos.isEmpty()) {
                        Text(
                            text = "No hay vencimientos disponibles.",
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        vencimientos.forEachIndexed { index, v ->
                            AnimatedVencimientoCard(
                                vencimiento = v,
                                delayMillis = index * 200
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun AnimatedVencimientoCard(vencimiento: Vencimiento, delayMillis: Int = 0) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + expandVertically(tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("📘 ${vencimiento.nombre}", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                Text("📝 ${vencimiento.descripcion}")
                Text("📅 ${vencimiento.fecha_vencimiento}", fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
    fun AnimatedDeadlineCard(titulo: String, fecha: String, delayMillis: Int = 0) {
        var visible by remember { mutableStateOf(false) }
        var clicked by remember { mutableStateOf(false) }

        // Animaciones de aparición
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
            label = ""
        )
        val offsetX by animateDpAsState(
            targetValue = if (visible) 0.dp else (-40).dp,
            animationSpec = tween(durationMillis = 600, delayMillis = delayMillis),
            label = ""
        )

        // Animación de clic (iluminación + leve rebote)
        val scale by animateFloatAsState(
            targetValue = if (clicked) 1.05f else 1f,
            animationSpec = tween(durationMillis = 150),
            label = ""
        )
        val glowColor = if (clicked) Color(0xFFE1BEE7) else Color.White

        LaunchedEffect(Unit) { visible = true }

        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 8.dp)
                .graphicsLayer(
                    alpha = alpha,
                    translationX = offsetX.value,
                    scaleX = scale,
                    scaleY = scale
                )
                .clickable { clicked = !clicked },
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = titulo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF311B92)
                    )
                    Text(
                        text = "Vence el $fecha",
                        fontSize = 15.sp,
                        color = Color.DarkGray
                    )
                }

                Icon(
                    painter = painterResource(id = R.drawable.ic_calendar),
                    contentDescription = "Vencimiento",
                    tint = glowColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
// =========================================================
// =============== PANTALLA GLOSARIO ========================
// =========================================================
@Composable
fun PantallaGlosario() {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fondo degradado animado (azul a violeta)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF64B5F6), Color(0xFFCE93D8), colorShift)
    val colorBottom = lerp(Color(0xFF303F9F), Color(0xFF6A1B9A), colorShift)

    // Estados
    var glosarios by remember { mutableStateOf<List<Glosario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Lógica: obtener token y llamar API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)

                if (token.isNullOrEmpty()) {
                    errorMessage = "Token no encontrado. Inicie sesión nuevamente."
                    isLoading = false
                    return@launch
                }

                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerGlosarios("Bearer $token")

                if (response.isSuccessful) {
                    glosarios = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Diseño
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = Color.White)
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📘 Glosario Fiscal",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (glosarios.isEmpty()) {
                        Text(
                            text = "No hay términos registrados en el glosario.",
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        glosarios.forEachIndexed { index, g ->
                            AnimatedGlosarioCard(
                                glosario = g,
                                delayMillis = index * 200
                            )
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AnimatedGlosarioCard(glosario: Glosario, delayMillis: Int = 0) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + expandVertically(tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "📖 ${glosario.palabra}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                    fontSize = 18.sp
                )
                Text(
                    text = glosario.significado,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}


    // =================== pantalla de Tutoriales==================

@Composable
fun PantallaTutorial() {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var tutoriales by remember { mutableStateOf<List<Tutorial>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    // Fondo degradado animado
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF26C6DA), Color(0xFF80DEEA), shift)
    val colorBottom = lerp(Color(0xFF00796B), Color(0xFF004D40), shift)

    // Llamada a la API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerTutoriales() // sin token

                if (response.isSuccessful) {
                    tutoriales = response.body() ?: emptyList()
                } else {
                    Toast.makeText(contexto, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(contexto, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                cargando = false
            }
        }
    }


    // Fondo animado + lista
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            cargando -> {
                CircularProgressIndicator(color = Color.White)
            }

            tutoriales.isEmpty() -> {
                Text(
                    "No hay tutoriales disponibles 📭",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = "🎓 Tutoriales Interactivos",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                        )
                    }

                    item {
                        Text(
                            text = "Aprendé sobre impuestos, obligaciones y beneficios fiscales 📚",
                            fontSize = 16.sp,
                            color = Color(0xFFE0F7FA),
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }

                    itemsIndexed(tutoriales) { index, tutorial ->
                        AnimatedVideoCard(
                            videoUrl = tutorial.link,
                            titulo = tutorial.titulo,
                            delayMillis = index * 250
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AnimatedVideoCard(videoUrl: String, titulo: String, delayMillis: Int = 0) {
    val contexto = LocalContext.current
    var visible by remember { mutableStateOf(false) }
    var hovered by remember { mutableStateOf(false) }

    // Obtener ID del video (para mostrar miniatura)
    val videoId = remember(videoUrl) {
        Regex("(?<=v=|shorts/|youtu\\.be/)[a-zA-Z0-9_-]{11}")
            .find(videoUrl)?.value ?: ""
    }

    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

    // Animaciones
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 800, delayMillis = delayMillis),
        label = ""
    )
    val offsetY by animateDpAsState(
        targetValue = if (visible) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 800, delayMillis = delayMillis),
        label = ""
    )
    val scale by animateFloatAsState(
        targetValue = if (hovered) 1.03f else 1f,
        animationSpec = tween(durationMillis = 250),
        label = ""
    )
    val borderColor by animateColorAsState(
        targetValue = if (hovered) Color(0xFF18FFFF) else Color.Transparent,
        animationSpec = tween(durationMillis = 400),
        label = ""
    )

    LaunchedEffect(Unit) { visible = true }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(vertical = 10.dp)
            .graphicsLayer(
                alpha = alpha,
                translationY = offsetY.value,
                scaleX = scale,
                scaleY = scale
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { hovered = true; tryAwaitRelease(); hovered = false },
                    onTap = {
                        // 🟥 Abrir el video directamente en YouTube o navegador
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
                        contexto.startActivity(intent)
                    }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = titulo,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF004D40),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Mostrar miniatura + botón de play
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = "Miniatura del video",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Botón de play centrado
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Abrir en YouTube",
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(72.dp)
                        .shadow(8.dp, CircleShape)
                )
            }
        }
    }
}

// =========================================================
// =============== PANTALLA EJEMPLOS ======================
// =========================================================
@Composable
fun PantallaEjemplos(navController: NavController) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fondo degradado animado (azul a violeta)
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF64B5F6), Color(0xFFCE93D8), colorShift)
    val colorBottom = lerp(Color(0xFF303F9F), Color(0xFF6A1B9A), colorShift)

    // Estados
    var ejemplos by remember { mutableStateOf<List<Ejemplo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Lógica: obtener token y llamar API
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)

                if (token.isNullOrEmpty()) {
                    errorMessage = "Token no encontrado. Inicie sesión nuevamente."
                    isLoading = false
                    return@launch
                }

                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerEjemplos("Bearer $token")

                if (response.isSuccessful) {
                    ejemplos = response.body() ?: emptyList()
                } else {
                    errorMessage = "Error ${response.code()}: ${response.message()}"
                }
            } catch (e: Exception) {
                errorMessage = "Error de conexión: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Diseño
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = Color.White)
            }

            errorMessage != null -> {
                Text(
                    text = errorMessage ?: "Error desconocido",
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📚 Ejemplos de Cálculo",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    if (ejemplos.isEmpty()) {
                        Text(
                            text = "No hay ejemplos registrados.",
                            color = Color.White,
                            fontStyle = FontStyle.Italic
                        )
                    } else {
                        ejemplos.forEachIndexed { index, ejemplo ->
                            AnimatedEjemploCard(
                                ejemplo = ejemplo,
                                navController = navController,
                                delayMillis = index * 200
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================
// =============== TARJETA ANIMADA EJEMPLO =================
// =========================================================
@Composable
fun AnimatedEjemploCard(ejemplo: Ejemplo, navController: NavController, delayMillis: Int = 0) {
    var visible by remember { mutableStateOf(false) }
    var pressed by remember { mutableStateOf(false) }
    var idEjemplo = ejemplo.id_ejemplo
    LaunchedEffect(Unit) {
        delay(delayMillis.toLong())
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(600)) + expandVertically(tween(600))
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { pressed = !pressed },
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
            elevation = CardDefaults.cardElevation(6.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "📌 ${ejemplo.nombre}",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A237E),
                    fontSize = 18.sp
                )
                Text(
                    text = ejemplo.descripcion,
                    fontSize = 15.sp,
                    color = Color.DarkGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Ingresos Brutos: $${"%,.2f".format(ejemplo.ingresos_brutos)}", fontSize = 14.sp)
                Text("Superficie afectada: ${ejemplo.superficie_afectada} m²", fontSize = 14.sp)
                Text("Energía eléctrica: ${ejemplo.energia_electrica} KW", fontSize = 14.sp)
                Text("Alquileres: $${"%,.2f".format(ejemplo.alquileres)}", fontSize = 14.sp)
                Text("Modelo de negocio: ${if (ejemplo.muebles) "Venta de productos" else "Servicios"}", fontSize = 14.sp)

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        navController.navigate("resultadoEjemplo/$idEjemplo")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Usar este ejemplo", color = Color.White)
                }
            }
        }
    }
}
// =========================================================
// =============== PANTALLA RESULTADO EJEMPLO =============
// =========================================================
@Composable
fun PantallaResultadoEjemplo(
    idEjemplo: Int,
    navController: NavController
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    // Fondo animado
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF00BCD4), Color(0xFF80DEEA), shift)
    val colorBottom = lerp(Color(0xFF0097A7), Color(0xFF006064), shift)

    // Estados
    var ejemplo by remember { mutableStateOf<Ejemplo?>(null) }
    var categoria by remember { mutableStateOf<Categoria?>(null) }
    var impuestos by remember { mutableStateOf<List<Impuestos>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Cargar ejemplo, categorías e impuestos
    LaunchedEffect(idEjemplo) {
        scope.launch {
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                val prefs = contexto.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("jwt_token", null)

                if (token.isNullOrEmpty()) {
                    error = "No se encontró token de autenticación."
                    cargando = false
                    return@launch
                }

                // Obtener ejemplo
                val responseEjemplo = api.obtenerEjemplo("Bearer $token", idEjemplo)
                if (!responseEjemplo.isSuccessful || responseEjemplo.body() == null) {
                    error = "Error al obtener el ejemplo."
                    cargando = false
                    return@launch
                }
                ejemplo = responseEjemplo.body()!!

                // Obtener categorías
                val responseCat = api.obtenerCategorias("Bearer $token")
                if (!responseCat.isSuccessful || responseCat.body() == null) {
                    error = "Error al obtener las categorías."
                    cargando = false
                    return@launch
                }
                val categorias = responseCat.body()!!.sortedBy { it.id_categoria }

                // Determinar categoría
                categoria = categorias.find {
                    ejemplo!!.ingresos_brutos <= it.ingresos_brutos &&
                            ejemplo!!.superficie_afectada <= it.superficie_afectada &&
                            ejemplo!!.energia_electrica <= it.energia_electrica &&
                            ejemplo!!.alquileres <= it.alquileres
                }

                // Obtener impuestos
                val responseImpuestos = api.obtenerImpuestos("Bearer $token")
                if (responseImpuestos.isSuccessful && responseImpuestos.body() != null) {
                    impuestos = responseImpuestos.body()!!
                }

            } catch (e: Exception) {
                error = "Error de conexión: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    // Animaciones de entrada
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, animationSpec = tween(1000), label = "")
    val offsetY by animateDpAsState(targetValue = if (visible) 0.dp else 50.dp, animationSpec = tween(1000), label = "")
    LaunchedEffect(Unit) { visible = true }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom)))
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            cargando -> CircularProgressIndicator(color = Color.White)
            error != null -> Text(text = error ?: "Error desconocido", color = Color.White, fontSize = 18.sp)
            ejemplo != null -> {
                val ej = ejemplo!!
                LazyColumn(
                    modifier = Modifier
                        .graphicsLayer(alpha = alpha)
                        .offset(y = offsetY)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 24.dp)
                ) {
                    item {
                        Text(
                            text = "📊 Resultado del ejemplo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }

                    // Categoría
                    item {
                        if (categoria != null) {
                            val cat = categoria!!
                            ExpandableResultButton(
                                title = "Categoría",
                                value = cat.nombre,
                                explanation = "Categoría determinada según los valores del ejemplo."
                            )
                            val montoMensual = if (ej.muebles) cat.total_muebles else cat.total
                            ExpandableResultButton(
                                title = "Monto mensual",
                                value = "$${"%.2f".format(montoMensual)}",
                                explanation = if (ej.muebles)
                                    "Incluye componente impositivo para venta de productos."
                                else
                                    "Incluye componente impositivo para servicios."
                            )
                        } else {
                            Text(
                                "No califica para las categorías del monotributo",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Impuestos
                    if (impuestos.isNotEmpty()) {
                        items(impuestos) { impuesto ->
                            val porcentaje = impuesto.porcentaje ?: 0.0
                            val fijo = impuesto.monto_fijo ?: 0.0
                            val valor = ej.ingresos_brutos * (porcentaje / 100) + fijo

                            Column(modifier = Modifier.fillMaxWidth()) {
                                ExpandableResultButton(
                                    title = impuesto.nombre,
                                    value = "$${"%.2f".format(valor)}",
                                    explanation = impuesto.descripcion
                                )
                                Button(
                                    onClick = { navController.navigate("tutorial_unico/${impuesto.id_tutorial}") },
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .fillMaxWidth(0.6f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC))
                                ) {
                                    Text("Ver tutorial", color = Color.White)
                                }
                            }
                        }
                    } else {
                        item {
                            Text(
                                "No hay impuestos adicionales",
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }

                    // Botón volver
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = { navController.navigate("menu") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9C8CFC)),
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(top = 16.dp)
                        ) {
                            Text("Volver al menú", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}


// pantalla para tutorial de un impuesto en especifico
@Composable
fun PantallaTutorialUnico(
    idTutorial: Int
) {
    val contexto = LocalContext.current
    val scope = rememberCoroutineScope()

    var tutorial by remember { mutableStateOf<Tutorial?>(null) }
    var cargando by remember { mutableStateOf(true) }

    // Fondo degradado animado
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 7000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = ""
    )
    val colorTop = lerp(Color(0xFF26C6DA), Color(0xFF80DEEA), shift)
    val colorBottom = lerp(Color(0xFF00796B), Color(0xFF004D40), shift)

    // Llamada a la API
    LaunchedEffect(idTutorial) {
        scope.launch {
            try {
                val api = RetrofitClient.instance.create(ApiService::class.java)
                val response = api.obtenerTutoriales() // sin token

                if (response.isSuccessful) {
                    tutorial = response.body()?.firstOrNull { it.id_tutorial == idTutorial }
                } else {
                    Toast.makeText(contexto, "Error ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(contexto, "Error de conexión: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                cargando = false
            }
        }
    }

    // UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(colorTop, colorBottom))),
        contentAlignment = Alignment.TopCenter
    ) {
        when {
            cargando -> CircularProgressIndicator(color = Color.White)
            tutorial == null -> Text(
                "Tutorial no disponible",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {
                        Text(
                            text = tutorial!!.titulo,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                        )
                    }

                    item {
                        AnimatedVideoCard(
                            videoUrl = tutorial!!.link,
                            titulo = tutorial!!.titulo,
                            delayMillis = 0
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}






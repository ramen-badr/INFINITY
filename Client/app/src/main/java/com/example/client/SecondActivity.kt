package com.example.client

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client.ui.theme.AppTypes.type_1
import com.example.client.ui.theme.AppTypes.type_2
import com.example.client.ui.theme.AppTypes.type_Button
import kotlinx.coroutines.delay
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import okhttp3.OkHttpClient
import okhttp3.Request

data class ServiceResponse(
    val id: Long,
    val price: String,
    val imageUrl: String,
    val name: String
)

class SecondActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val gson = Gson()

    @Composable
    fun ServicesScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var currentImageIndex by remember { mutableIntStateOf(0) }
        var showDialog by remember { mutableStateOf(false) }
        val scrollState = rememberScrollState()
        var showDialog2 by remember { mutableStateOf(false) }
        var list by remember { mutableStateOf<List<ServiceResponse>>(emptyList()) }
        var isLoading by remember { mutableStateOf<Boolean>(true) }
        var error by remember { mutableStateOf<String?>(null) }

        if (showDialog2) {
            AlertDialog(
                onDismissRequest = { showDialog2 = false },
                title = {
                    Text(
                        text = "Ваша заявка принята!",
                        style = type_2.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                text = {
                    Text("В ближайшее время с вами свяжется специалист")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog2 = false
                            context.startActivity(Intent(context, MainActivity::class.java))
                            (context as Activity).finish()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text("На главную", style = type_Button)
                    }
                }
            )
        }

        LaunchedEffect(Unit) {
            try {
                val serviceIds = withContext(Dispatchers.IO) {
                    URL("http://10.0.2.2:9090/services").readText()
                }.let { json ->
                    gson.fromJson(json, Array<Long>::class.java).toList()
                }

                val services = mutableListOf<ServiceResponse>()
                for (id in serviceIds) {
                    val serviceJson = withContext(Dispatchers.IO) {
                        URL("http://10.0.2.2:9090/services/$id").readText()
                    }
                    val service = gson.fromJson(serviceJson, ServiceResponse::class.java)
                    services.add(
                        ServiceResponse(
                            price = service.price,
                            imageUrl = "http://10.0.2.2:9090/${service.imageUrl}",
                            name = service.name,
                            id = id
                        )
                    )
                }
                list = services
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .background(color = Color.Black.copy(alpha = 0.8f))
                    .padding(24.dp, top = 32.dp, bottom = 24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(48.dp)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "INFINITY",
                        style = type_2,
                        color = Color.White
                    )
                    Text(
                        text = "Рекламно-производственная компания",
                        style = type_1,
                        color = Color.White
                    )
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (error != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "Непредвиденная ошибка",
                            style = type_Button,
                            color = Color.Red)
                    }
                } else if (list.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = {
                            currentImageIndex =
                                if (currentImageIndex - 1 < 0) list.lastIndex
                                else currentImageIndex - 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.arrowleft),
                                contentDescription = "Back",
                                modifier = Modifier.requiredSize(32.dp)
                            )
                        }

                        Text(
                            text = list[currentImageIndex].name,
                            style = type_Button,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f, false)
                        )

                        IconButton(onClick = {
                            currentImageIndex =
                                if (currentImageIndex + 1 > list.lastIndex) 0
                                else currentImageIndex + 1
                        }) {
                            Image(
                                painter = painterResource(id = R.drawable.arrowright),
                                contentDescription = "Forward",
                                modifier = Modifier.requiredSize(32.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .horizontalScroll(scrollState)
                            ) {
                                CustomImageLoader(
                                    imageUrl = list[currentImageIndex].imageUrl
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Листай",
                                    style = type_Button,
                                    color = Color.White
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.arrowright),
                                    contentDescription = "Scroll",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                showDialog = true
                                CartItems.add(
                                    list[currentImageIndex]
                                )},
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.Black
                            ),
                            border = BorderStroke(2.dp, Color.Black),
                            modifier = Modifier.weight(1f).requiredHeight(64.dp)
                        ) {
                            Text("В корзину", style = type_Button)
                        }

                        Button(
                            onClick = {
                                List = mutableStateListOf<Long>(list[currentImageIndex].id.toLong())
                                context.startActivity(Intent(context, FifthActivity::class.java))
                                (context as Activity).finish()},
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f).requiredHeight(64.dp)
                        ) {
                            Text("Заказать", style = type_Button)
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(vertical = 16.dp)
            ) {
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Vector",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, SecondActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 40.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.services),
                            contentDescription = "Vector",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, ThirdActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cart),
                            contentDescription = "Vector",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, FourthActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.history),
                            contentDescription = "Vector",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
            }
        }

        if (showDialog) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable { showDialog = false },
                contentAlignment = Alignment.BottomCenter
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Услуга успешно добавлена в корзину!",
                            style = type_1.copy(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            LaunchedEffect(showDialog) {
                delay(1000)
                showDialog = false
            }
        }
    }

    @Composable
    private fun CustomImageLoader(
        imageUrl: String,
        modifier: Modifier = Modifier
    ) {
        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(imageUrl) {
            isLoading = true
            error = null
            try {
                imageBitmap = loadImageFromNetwork(imageUrl)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }

        Box(modifier = modifier) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                error != null -> Text(
                    text = "Ошибка загрузки",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                imageBitmap != null -> Image(
                    bitmap = imageBitmap!!,
                    contentDescription = "Image",
                    contentScale = ContentScale.FillHeight,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private suspend fun loadImageFromNetwork(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext null

                response.body?.byteStream()?.use { inputStream ->
                    BitmapFactory.decodeStream(inputStream)?.asImageBitmap()
                }
            }
        } catch (_: Exception) {
            null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ServicesScreen()
        }
    }
}
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
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
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
import com.example.client.ui.theme.AppTypes.type_Body
import com.example.client.ui.theme.AppTypes.type_Button
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request

val CartItems = mutableListOf<ServiceResponse>()

class ThirdActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val selectedIds = mutableStateListOf<Long>()

    @Composable
    fun CartItem(data: ServiceResponse,
                 isChecked: Boolean,
                 onCheckedChange: (Boolean) -> Unit) {
        val context = LocalContext.current

        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
            ) {
                CustomImageLoader(
                    imageUrl = data.imageUrl
                )

                IconButton(
                    onClick = { onCheckedChange(!isChecked) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    if (isChecked) {
                        Image(
                            painter = painterResource(R.drawable.checkmark),
                            contentDescription = "Checked",
                            modifier = Modifier.size(32.dp)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                        )
                    }
                }
            }

            Text(
                text = data.name,
                style = type_2,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = {
                    List = mutableStateListOf<Long>(data.id.toLong())
                    context.startActivity(Intent(context, FifthActivity::class.java))
                    (context as Activity).finish()},
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black
                ),
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Заказать",
                    style = type_Button)
            }
        }
    }

    @Composable
    fun CartScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var showDialog2 by remember { mutableStateOf(false) }

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

        Column(
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Header
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

            // Main Content
            Column(
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                if (CartItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            Text(
                                text = "Пока у вас нет заказов",
                                style = type_Body,
                                color = Color.Gray,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(24.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(CartItems) { item ->
                            val isChecked = remember { derivedStateOf { item.id.toLong() in selectedIds } }
                            CartItem(
                                data = item,
                                isChecked = isChecked.value,
                                onCheckedChange = { checked ->
                                    if (checked) {
                                        selectedIds.add(item.id.toLong())
                                    } else {
                                        selectedIds.remove(item.id.toLong())
                                    }
                                }
                            )
                        }
                    }
                }

                // Checkout Button
                Button(
                    onClick = {
                        List = selectedIds
                        context.startActivity(Intent(context, FifthActivity::class.java))
                        (context as Activity).finish()},
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("К оформлению",
                        style = type_Button)
                }
            }

            // Bottom Navigation
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
                    contentDescription = "Item Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    private suspend fun loadImageFromNetwork(url: String): ImageBitmap? =
        withContext(Dispatchers.IO) {
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
            CartScreen()
        }
    }
}
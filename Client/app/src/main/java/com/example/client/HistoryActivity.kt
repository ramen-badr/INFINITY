package com.example.client

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.Locale

data class HistoryItemData(
    val imageUrl: String,
    val price: String,
    val title: String,
    val date: String
)

data class OrderDetails(
    val purchaseDate: String,
    val itemIds: List<Long>
)

data class ServiceDetails(
    val price: String,
    val imageUrl: String,
    val name: String
)

class HistoryActivity : ComponentActivity() {
    private val client = OkHttpClient()
    private val gson = Gson()

    @Composable
    fun HistoryItem(
        data: HistoryItemData,
        onAddToCart: () -> Unit
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomImageLoader(
                imageUrl = data.imageUrl
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "${data.price} ₽",
                    style = type_Body,
                    color = Color.Black
                )
                Text(
                    text = data.date,
                    style = type_2,
                    color = Color.Gray
                )
            }

            Text(
                text = data.title,
                style = type_2,
                color = Color.Black
            )

            Button(
                onClick = onAddToCart,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("В корзину", style = type_Button)
            }
        }
    }

    @Composable
    fun HistoryScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var showDialog by remember { mutableStateOf(false) }
        var historyItems by remember { mutableStateOf<List<HistoryItemData>>(emptyList()) }
        var error by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf<Boolean>(true) }

        LaunchedEffect(Unit) {
            try {
                val orders = getOrders()
                val items = mutableListOf<HistoryItemData>()

                for (orderId in orders) {
                    val orderDetails = getOrderDetails(orderId)
                    val formattedDate = formatDate(orderDetails.purchaseDate)

                    for (itemId in orderDetails.itemIds) {
                        val service = getServiceDetails(itemId)
                        items.add(
                            HistoryItemData(
                                imageUrl = "http://10.0.2.2:9090/${service.imageUrl}",
                                price = service.price,
                                title = service.name,
                                date = formattedDate
                            )
                        )
                    }
                }

                historyItems = items
                isLoading = false
            } catch (e: Exception) {
                error = e.message
                isLoading = false
            }
        }

        Column(
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
                    painter = painterResource(id = R.mipmap.logo),
                    contentDescription = "Логотип компании",
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

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (error != null && phoneNumber != "") {
                Box(modifier = Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Непредвиденная ошибка",
                        style = type_Button,
                        color = Color.Red)
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp)
                ) {
                    if (historyItems.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
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
                                    textAlign = TextAlign.Center
                                )
                                Button(
                                    onClick = {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                ServicesActivity::class.java
                                            )
                                        )
                                        (context as Activity).finish()
                                    },
                                    shape = RoundedCornerShape(16.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Black,
                                        contentColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Услуги", style = type_Button)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(historyItems) { item ->
                                HistoryItem(item) { showDialog = true }
                            }
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
                            contentDescription = "Перейти на главную страницу",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, ServicesActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.services),
                            contentDescription = "Перейти на страницу услуг",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, CartActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.cart),
                            contentDescription = "Перейти в корзину",
                            modifier = Modifier
                                .fillMaxSize())
                    }

                }
                IconButton(
                    onClick = {
                        context.startActivity(Intent(context, HistoryActivity::class.java))
                        (context as Activity).finish()},
                ) {
                    Box(
                        modifier = Modifier
                            .requiredSize(size = 36.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.history),
                            contentDescription = "Перейти к истории покупок",
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

    private suspend fun getOrders(): List<Long> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://10.0.2.2:9090/history/$phoneNumber")
            .build()

        val response = client.newCall(request).execute()
        gson.fromJson(
            response.body?.string() ?: throw Exception("Empty response"),
            object : TypeToken<List<Long>>() {}.type
        )
    }

    private suspend fun getOrderDetails(orderId: Long): OrderDetails = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://10.0.2.2:9090/orders/$orderId")
            .build()

        val response = client.newCall(request).execute()
        gson.fromJson(
            response.body?.string() ?: throw Exception("Empty response"),
            OrderDetails::class.java
        )
    }

    private suspend fun getServiceDetails(serviceId: Long): ServiceDetails = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("http://10.0.2.2:9090/services/$serviceId")
            .build()

        val response = client.newCall(request).execute()
        gson.fromJson(
            response.body?.string() ?: throw Exception("Empty response"),
            ServiceDetails::class.java
        )
    }


    private fun formatDate(isoDate: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            val date = inputFormat.parse(isoDate) ?: return ""
            outputFormat.format(date)
        } catch (_: Exception) {
            ""
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
                    contentDescription = "Изображение заказанного товара",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 200.dp, max = 300.dp)
                        .clip(RoundedCornerShape(16.dp))
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
            HistoryScreen()
        }
    }
}
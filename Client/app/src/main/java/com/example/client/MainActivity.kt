package com.example.client

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client.ui.theme.AppTypes.type_1
import com.example.client.ui.theme.AppTypes.type_2
import com.example.client.ui.theme.AppTypes.type_Body
import com.example.client.ui.theme.AppTypes.type_Button

class MainActivity : ComponentActivity() {
    @Composable
    fun Home(modifier: Modifier = Modifier) {
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

        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Image(
                painter = painterResource(id = R.drawable.image),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.9f)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
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
                            color = Color.Black
                        )
                        Text(
                            text = "Рекламно-производственная компания",
                            style = type_1,
                            color = Color.Black
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.Start)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Наша реклама -",
                            color = Color.Red,
                            style = type_Body
                        )
                    }

                    Box(
                        contentAlignment = Alignment.CenterEnd,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.End)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "ВАШ УСПЕХ",
                            color = Color.Red,
                            style = type_Body
                        )
                    }

                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        List = mutableStateListOf<Long>()
                        context.startActivity(Intent(context, FifthActivity::class.java))
                        (context as Activity).finish()},
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .shadow(8.dp, shape = RoundedCornerShape(24.dp))
                ) {
                    Text(
                        text = "Заказать",
                        style = type_Button
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Home()
        }
    }
}
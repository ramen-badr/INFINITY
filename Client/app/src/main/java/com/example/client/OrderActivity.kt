package com.example.client

import android.annotation.SuppressLint
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.client.ui.theme.AppTypes.type_1
import com.example.client.ui.theme.AppTypes.type_2
import com.example.client.ui.theme.AppTypes.type_Button
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

data class HistoryRequest(
    val phoneNumber: String,
    val orderId: Long
)

var List = mutableStateListOf<Long>()
var phoneNumber = ""

data class OrderRequest(val itemIds: List<Long>)

class OrderActivity : ComponentActivity() {
    private val gson = Gson()
    private val client = OkHttpClient()

    @SuppressLint("CoroutineCreationDuringComposition")
    @Composable
    fun OrderScreen(modifier: Modifier = Modifier) {
        val context = LocalContext.current
        var isChecked by remember { mutableStateOf(false) }
        var name by remember { mutableStateOf(TextFieldValue()) }
        var email by remember { mutableStateOf(TextFieldValue()) }
        var phone by remember { mutableStateOf(TextFieldValue()) }
        var responseOrder by remember { mutableStateOf(false) }
        var showDialog by remember { mutableStateOf(false) }
        var nameError by remember { mutableStateOf<String?>(null) }
        var emailError by remember { mutableStateOf<String?>(null) }
        var phoneError by remember { mutableStateOf<String?>(null) }
        var consentError by remember { mutableStateOf<String?>(null) }

        LaunchedEffect (Unit) {
            if (phoneNumber != "") {
                responseOrder = true
            }
        }

        if (responseOrder) {
            responseOrder = false
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val orderId = createOrder(List)

                    orderId?.let { addToHistory(phoneNumber, it) }

                    withContext(Dispatchers.Main) {
                        showDialog = true
                    }
                } catch (_: Exception) {
                    withContext(Dispatchers.Main) {
                        phoneError = "Непредвиденная ошибка, повторите позже"
                    }
                }
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
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
                            showDialog = false
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

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(24.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Text(
                        text = "Проверьте данные для возможности связи",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        InputField(
                            label = "Имя",
                            placeholder = "Ангелина",
                            value = name,
                            errorMessage = nameError,
                            onValueChange = { name = it },
                            onErrorReset = { nameError = null }
                        )
                        InputField(
                            label = "Почта",
                            placeholder = "exe@mail.ru",
                            value = email,
                            errorMessage = emailError,
                            onValueChange = { email = it },
                            onErrorReset = { emailError = null }
                        )
                        PhoneInputField(
                            value = phone,
                            errorMessage = phoneError,
                            onValueChange = { phone = it },
                            onErrorReset = { phoneError = null }
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = {
                                        isChecked = !isChecked
                                        consentError = null},
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (isChecked) R.drawable.checkbox2
                                            else R.drawable.checkbox
                                        ),
                                        contentDescription = "Разрешение на обработку своих персональных данных",
                                        tint = Color.Black,
                                        modifier = Modifier.requiredSize(16.dp)
                                    )
                                }
                                Text(
                                    text = "Оформляя заказ, я даю разрешение на обработку своих персональных данных",
                                    style = type_1,
                                    color = if (consentError != null) Color.Red else Color.Black,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        val isValid = validateFields(
                            name = name.text,
                            email = email.text,
                            phone = phone.text,
                            onNameError = { nameError = it },
                            onEmailError = { emailError = it },
                            onPhoneError = { phoneError = it }
                        )

                        if (!isChecked) {
                            consentError = "Вам нужно подписать согласие"
                        }

                        if (isValid && isChecked) {
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    sendUserData(
                                        name = name.text,
                                        email = email.text,
                                        phone = processPhoneForRequest(phone.text)
                                    )

                                    withContext(Dispatchers.Main) {
                                        phoneNumber = processPhoneForRequest(phone.text)
                                        responseOrder = true
                                    }
                                } catch (_: Exception) {
                                    withContext(Dispatchers.Main) {
                                        phoneError = "Непредвиденная ошибка, попробуйте позже"
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text("Заказать", style = type_Button)
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
    }

    fun validateFields(
        name: String,
        email: String,
        phone: String,
        onNameError: (String?) -> Unit,
        onEmailError: (String?) -> Unit,
        onPhoneError: (String?) -> Unit
    ): Boolean {
        val nameError = when {
            name.isBlank() -> "Введите имя"
            !name.matches(Regex("^[а-яА-ЯёЁ-]+$")) -> "Допускаются только кириллические буквы и тире"
            else -> null
        }
        onNameError(nameError)

        val emailError = when {
            email.isBlank() -> "Введите email"
            !email.matches(Regex("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) -> "Некорректный формат email"
            else -> null
        }
        onEmailError(emailError)

        val phoneDigits = phone.filter { it.isDigit() }
        val phoneError = when {
            phoneDigits.isEmpty() -> "Введите телефон"
            phoneDigits.length != 11 -> "Некорректный формат телефона"
            else -> null
        }
        onPhoneError(phoneError)

        return nameError == null && emailError == null && phoneError == null
    }

    fun createOrder(itemIds: List<Long>): Long? {
        if (itemIds.isEmpty()) return null

        val requestBody = gson.toJson(OrderRequest(itemIds))
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:9090/orders")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        return response.body?.string().let {
           it?.trim()?.toLongOrNull() ?: run {
               null
           }
        }
    }

    private fun addToHistory(phone: String, orderId: Long) {
        val requestBody = gson.toJson(
            HistoryRequest(phone, orderId)
        ).toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:9090/history")
            .post(requestBody)
            .build()

        client.newCall(request).execute()
    }

    private fun processPhoneForRequest(phone: String): String {
        val digits = phone.filter { it.isDigit() }
        return "+${digits.take(11)}"
    }

    private fun sendUserData(name: String, email: String, phone: String): Response {
        val requestBody = JSONObject()
            .put("phoneNumber", phone)
            .put("email", email)
            .put("name", name)
            .toString()
            .toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("http://10.0.2.2:9090/users")
            .post(requestBody)
            .build()

        return client.newCall(request).execute()
    }

    @Composable
    private fun PhoneInputField(
        value: TextFieldValue,
        errorMessage: String?,
        onValueChange: (TextFieldValue) -> Unit,
        onErrorReset: () -> Unit
    ) {
        Column {
            Text(
                text = "Телефон",
                style = type_2,
                color = Color.Black
            )

            BasicTextField(
                value = value,
                onValueChange = { newValue ->
                    val processed = processPhoneInput(newValue)
                    onValueChange(processed)
                    onErrorReset()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                textStyle = type_2.copy(
                    color = if (errorMessage != null) Color.Red else Color.Black,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    Column {
                        Box {
                            if (value.text.isEmpty()) {
                                Text(
                                    text = "+7 999 999-99-99",
                                    style = type_2.copy(
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            thickness = 1.dp,
                            color = if (errorMessage != null) Color.Red else Color.Gray
                        )
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                style = type_2.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            )
        }
    }

    private fun processPhoneInput(newValue: TextFieldValue): TextFieldValue {
        val digits = newValue.text.filter { it.isDigit() }
        val cleanInput = when {
            digits.startsWith("7") -> digits.drop(1)
            digits.startsWith("8") -> "7" + digits.drop(1)
            else -> digits
        }.take(10)

        val formatted = buildString {
            append("+7")
            if (cleanInput.isNotEmpty()) {
                append(" ")
                append(cleanInput.take(3))

                if (cleanInput.length > 3) {
                    append(" ")
                    append(cleanInput.substring(3, 6.coerceAtMost(cleanInput.length)))
                }
                if (cleanInput.length > 6) {
                    append("-")
                    append(cleanInput.substring(6, 8.coerceAtMost(cleanInput.length)))
                }
                if (cleanInput.length > 8) {
                    append("-")
                    append(cleanInput.substring(8, 10.coerceAtMost(cleanInput.length)))
                }
            }
        }

        val newCursorPos = when {
            newValue.selection.start == newValue.text.length -> formatted.length
            else -> {
                val sameCharIndex = findCursorPosition(
                    oldText = newValue.text,
                    newText = formatted,
                    oldSelection = newValue.selection.start
                )
                (sameCharIndex + 1).coerceIn(0, formatted.length)
            }
        }

        return TextFieldValue(
            text = formatted,
            selection = TextRange(newCursorPos)
        )
    }

    private fun findCursorPosition(oldText: String, newText: String, oldSelection: Int): Int {
        var oldIndex = 0
        var newIndex = 0

        while (oldIndex < oldSelection && newIndex < newText.length) {
            if (oldIndex < oldText.length && oldText[oldIndex] == newText[newIndex]) {
                oldIndex++
                newIndex++
            } else if (oldText.getOrNull(oldIndex) == ' ' || oldText.getOrNull(oldIndex) == '-') {
                oldIndex++
            } else {
                newIndex++
            }
        }
        return newIndex
    }


    @Composable
    private fun InputField(
        label: String,
        placeholder: String,
        value: TextFieldValue,
        errorMessage: String?,
        onValueChange: (TextFieldValue) -> Unit,
        onErrorReset: () -> Unit
    ) {
        Column {
            Text(
                text = label,
                style = type_2,
                color = Color.Black
            )

            BasicTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    onErrorReset()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                textStyle = type_2.copy(
                    color = if (errorMessage != null) Color.Red else Color.Black,
                    textAlign = TextAlign.Start
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    Column {
                        Box {
                            if (value.text.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    style = type_2.copy(
                                        color = Color.Gray.copy(alpha = 0.5f)
                                    )
                                )
                            }
                            innerTextField()
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            thickness = 1.dp,
                            color = if (errorMessage != null) Color.Red else Color.Gray
                        )
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage,
                                color = Color.Red,
                                style = type_2.copy(fontSize = 12.sp),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OrderScreen()
        }
    }
}
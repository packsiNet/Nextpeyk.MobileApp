package ir.nextpeyk.android.ui.screens.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private val LoginBg            = Color(0xFFF0F4F8)
private val LoginSurface2      = Color(0xFFE8EFF5)
private val LoginBorder        = Color(0xFFDDE4ED)
private val LoginBlue          = Color(0xFF246FA3)
private val LoginBlueDark      = Color(0xFF1A5C8A)
private val LoginTextPrimary   = Color(0xFF1A2636)
private val LoginTextSecondary = Color(0xFF5A7088)
private val LoginTextMuted     = Color(0xFF9AB0C5)
private val LoginDanger        = Color(0xFFE05C5C)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LoginBg)
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LogoSection()

            Spacer(modifier = Modifier.height(36.dp))

            // Username
            LoginField(
                label = "شماره موبایل یا نام کاربری",
                value = uiState.username,
                onValueChange = viewModel::onUsernameChange,
                placeholder = "مثال: ۰۹۱۲۱۲۳۴۵۶۷",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        tint = LoginTextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) },
                ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            LoginField(
                label = "رمز عبور",
                value = uiState.password,
                onValueChange = viewModel::onPasswordChange,
                placeholder = "رمز عبور خود را وارد کنید",
                leadingIcon = {
                    Icon(
                        Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = LoginTextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            if (uiState.passwordVisible) Icons.Outlined.VisibilityOff
                            else Icons.Outlined.Visibility,
                            contentDescription = null,
                            tint = LoginTextMuted,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                },
                visualTransformation = if (uiState.passwordVisible)
                    VisualTransformation.None
                else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        viewModel.login(onLoginSuccess)
                    },
                ),
            )

            // Error
            if (uiState.error != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = uiState.error!!,
                    color = LoginDanger,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Links
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                TextButton(onClick = {}) {
                    Text("ورود با کد یک‌بار مصرف", color = LoginBlueDark, fontSize = 13.sp)
                }
                TextButton(onClick = {}) {
                    Text("فراموشی رمز عبور", color = LoginBlueDark, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Submit
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.login(onLoginSuccess)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LoginBlue,
                    contentColor = Color.White,
                    disabledContainerColor = LoginBlue.copy(alpha = 0.6f),
                ),
                enabled = !uiState.isLoading,
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        "ورود به حساب",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(72.dp),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(modifier = Modifier.size(44.dp)) {
                val s = size.width / 140f
                drawRoundRect(
                    color = Color(0xFF1C2D40),
                    cornerRadius = CornerRadius(28 * s),
                )
                drawLine(
                    color = Color.White,
                    start = Offset(42 * s, 100 * s),
                    end = Offset(42 * s, 40 * s),
                    strokeWidth = 16 * s,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Color.White,
                    start = Offset(98 * s, 100 * s),
                    end = Offset(98 * s, 40 * s),
                    strokeWidth = 16 * s,
                    cap = StrokeCap.Round,
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.7f),
                    start = Offset(42 * s, 40 * s),
                    end = Offset(98 * s, 100 * s),
                    strokeWidth = 16 * s,
                    cap = StrokeCap.Round,
                )
                drawCircle(color = Color(0xFF246FA3), center = Offset(42 * s, 100 * s), radius = 13 * s)
                drawCircle(color = Color(0xFF246FA3), center = Offset(98 * s, 40 * s), radius = 11 * s)
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                "Nextpeyk",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = LoginTextPrimary,
                letterSpacing = 0.5.sp,
            )
            Text(
                "به حساب خود وارد شوید",
                fontSize = 13.sp,
                color = LoginTextSecondary,
            )
        }
    }
}

@Composable
private fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = LoginTextSecondary,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, fontSize = 14.sp, color = LoginTextMuted) },
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = LoginBlue,
                unfocusedBorderColor = LoginBorder,
                focusedContainerColor = LoginSurface2,
                unfocusedContainerColor = LoginSurface2,
                cursorColor = LoginBlue,
                focusedTextColor = LoginTextPrimary,
                unfocusedTextColor = LoginTextPrimary,
                focusedLeadingIconColor = LoginBlueDark,
                unfocusedLeadingIconColor = LoginTextMuted,
                focusedTrailingIconColor = LoginTextSecondary,
                unfocusedTrailingIconColor = LoginTextMuted,
            ),
            singleLine = true,
        )
    }
}

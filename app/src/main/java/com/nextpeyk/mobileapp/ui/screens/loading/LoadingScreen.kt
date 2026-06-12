package com.nextpeyk.mobileapp.ui.screens.loading

import android.webkit.WebView
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextpeyk.mobileapp.ui.screens.login.AuthState
import kotlinx.coroutines.delay

// Screen colors (inside the frame — #F5F3EE cream)
private val ScreenBg   = Color(0xFFF5F3EE)
private val TextDark   = Color(0xFF111111)
private val Orange     = Color(0xFFF97316)
private val Blue       = Color(0xFF3B82F6)
private val TrackColor = Color(0xFFDDD8D0)
private val MutedColor = Color(0xFFAAAAAA)

@Composable
fun LoadingScreen(
    authState: AuthState,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    var timerDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2800)
        timerDone = true
    }

    LaunchedEffect(timerDone, authState) {
        if (timerDone && authState != AuthState.Loading) {
            when (authState) {
                AuthState.Authenticated   -> onNavigateToHome()
                AuthState.Unauthenticated -> onNavigateToLogin()
                else -> {}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 22.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        LogoSection()
        Spacer(Modifier.height(24.dp))
        MapSection()
        Spacer(Modifier.height(20.dp))
        ProgressSection()
    }
}

// ─── Logo ──────────────────────────────────────────────────────────────────────

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Badge with N logo
        Box(
            modifier = Modifier
                .size(76.dp)
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = Color(0xFF143C64).copy(alpha = 0.38f),
                    spotColor   = Color(0xFF143C64).copy(alpha = 0.38f),
                )
                .clip(RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val s = size.width / 140f
                // Dark gradient background
                drawRoundRect(color = Color(0xFF1C2D40), cornerRadius = CornerRadius(20.dp.toPx()))
                // Glow blobs
                drawCircle(Color(0xFF246FA3).copy(alpha = 0.18f), center = Offset(42 * s, 100 * s), radius = 24 * s)
                drawCircle(Color(0xFF246FA3).copy(alpha = 0.18f), center = Offset(98 * s, 40 * s), radius = 20 * s)
                // N strokes
                drawLine(Color.White.copy(alpha = 0.95f), Offset(42 * s, 100 * s), Offset(42 * s, 40 * s), strokeWidth = 16 * s, cap = StrokeCap.Round)
                drawLine(Color.White.copy(alpha = 0.95f), Offset(98 * s, 100 * s), Offset(98 * s, 40 * s), strokeWidth = 16 * s, cap = StrokeCap.Round)
                drawLine(Color.White.copy(alpha = 0.72f), Offset(42 * s, 40 * s),  Offset(98 * s, 100 * s), strokeWidth = 16 * s, cap = StrokeCap.Round)
                // Accent dots
                drawCircle(Color(0xFF4EA8DE), center = Offset(42 * s, 100 * s), radius = 13 * s)
                drawCircle(Color.White.copy(alpha = 0.7f), center = Offset(42 * s, 100 * s), radius = 13 * s, style = Stroke(2.5f * s))
                drawCircle(Color(0xFF4EA8DE), center = Offset(98 * s, 40 * s), radius = 11 * s)
                drawCircle(Color.White.copy(alpha = 0.7f), center = Offset(98 * s, 40 * s), radius = 11 * s, style = Stroke(2.5f * s))
            }
        }

        // Brand name + subtitle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = TextDark)) { append("Next") }
                    withStyle(SpanStyle(color = Orange))   { append("peyk") }
                },
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.4).sp,
            )
            Text(
                "تحویل سریع · امن · مطمئن",
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = MutedColor,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

// ─── Map SVG animation (WebView) ───────────────────────────────────────────────

private val MAP_HTML = """
<!DOCTYPE html><html><head><meta charset="UTF-8">
<style>
*{margin:0;padding:0;box-sizing:border-box}
html,body{width:100%;height:100%;overflow:hidden;background:#E4DDD3}
svg{display:block;width:100%;height:100%}
</style></head><body>
<svg viewBox="0 0 276 196" xmlns="http://www.w3.org/2000/svg" fill="none" preserveAspectRatio="xMidYMid slice">
<defs>
  <filter id="bs" x="-15%" y="-15%" width="145%" height="145%"><feDropShadow dx="1" dy="2" stdDeviation="2.5" flood-color="rgba(0,0,0,.16)"/></filter>
  <filter id="ps" x="-80%" y="-80%" width="260%" height="260%"><feDropShadow dx="0" dy="3" stdDeviation="5" flood-color="rgba(0,0,0,.15)"/></filter>
  <linearGradient id="rg" gradientUnits="userSpaceOnUse" x1="5" y1="156" x2="270" y2="44">
    <stop offset="0%" stop-color="#F97316"/><stop offset="100%" stop-color="#3B82F6"/>
  </linearGradient>
  <pattern id="gr" patternUnits="userSpaceOnUse" width="5" height="5">
    <rect width="5" height="5" fill="#C4DDBE"/>
    <circle cx="1.2" cy="1.2" r=".6" fill="#B0CFAA" opacity=".7"/>
    <circle cx="3.7" cy="3.5" r=".5" fill="#B0CFAA" opacity=".55"/>
  </pattern>
</defs>
<rect width="276" height="196" fill="#E4DDD3"/>
<rect x="0" y="42" width="276" height="13" fill="#EEE8DF"/>
<rect x="0" y="99" width="276" height="12" fill="#EEE8DF"/>
<rect x="0" y="150" width="276" height="12" fill="#EEE8DF"/>
<rect x="42" y="0" width="12" height="196" fill="#EEE8DF"/>
<rect x="100" y="0" width="12" height="196" fill="#EEE8DF"/>
<rect x="156" y="0" width="12" height="196" fill="#EEE8DF"/>
<rect x="216" y="0" width="12" height="196" fill="#EEE8DF"/>
<rect x="0" y="70" width="276" height="6" fill="#E8E2D8"/>
<rect x="0" y="128" width="276" height="6" fill="#E8E2D8"/>
<rect x="70" y="0" width="6" height="196" fill="#E8E2D8"/>
<rect x="132" y="0" width="6" height="196" fill="#E8E2D8"/>
<rect x="192" y="0" width="6" height="196" fill="#E8E2D8"/>
<rect x="246" y="0" width="6" height="196" fill="#E8E2D8"/>
<rect x="0" y="41.5" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="0" y="54.2" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="0" y="98.5" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="0" y="110.2" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="0" y="149.5" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="0" y="161.2" width="276" height="1.2" fill="rgba(180,170,158,.65)"/>
<rect x="41.5" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="53.2" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="99.5" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="111.2" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="155.5" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="167.2" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="215.5" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<rect x="226.2" y="0" width="1.2" height="196" fill="rgba(180,170,158,.65)"/>
<line x1="0" y1="48" x2="276" y2="48" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="0" y1="105" x2="276" y2="105" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="0" y1="156" x2="276" y2="156" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="48" y1="0" x2="48" y2="196" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="106" y1="0" x2="106" y2="196" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="162" y1="0" x2="162" y2="196" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<line x1="222" y1="0" x2="222" y2="196" stroke="rgba(255,255,255,.55)" stroke-width=".8" stroke-dasharray="10 8"/>
<g filter="url(#bs)"><rect x="3" y="3" width="16" height="37" rx="1.5" fill="#C8C2B6"/><rect x="21" y="3" width="19" height="17" rx="1.5" fill="#CCC6BA"/><rect x="21" y="22" width="19" height="18" rx="1.5" fill="#C5BFB3"/></g>
<g filter="url(#bs)"><rect x="55" y="3" width="13" height="37" rx="1.5" fill="#CAC4B8"/><rect x="70" y="3" width="28" height="15" rx="1.5" fill="#C8C2B6"/><rect x="70" y="20" width="28" height="20" rx="1.5" fill="#CFC9BD"/></g>
<g filter="url(#bs)"><rect x="113" y="3" width="17" height="37" rx="1.5" fill="#C6C0B4"/><rect x="132" y="3" width="21" height="22" rx="1.5" fill="#CAC4B8"/><rect x="132" y="27" width="21" height="13" rx="1.5" fill="#C3BDB1"/></g>
<rect x="168" y="3" width="46" height="37" rx="2" fill="url(#gr)"/>
<ellipse cx="181" cy="18" rx="7" ry="8" fill="#85BC7E" opacity=".9"/>
<ellipse cx="198" cy="26" rx="6" ry="6.5" fill="#8EC487" opacity=".85"/>
<ellipse cx="188" cy="30" rx="4.5" ry="5" fill="#7BAF74" opacity=".8"/>
<path d="M170,12 Q183,18 196,28 Q202,34 212,36" stroke="#9CC894" stroke-width="1.5" fill="none" stroke-linecap="round"/>
<g filter="url(#bs)"><rect x="228" y="3" width="21" height="16" rx="1.5" fill="#CAC4B8"/><rect x="228" y="21" width="21" height="19" rx="1.5" fill="#C6C0B4"/><rect x="251" y="3" width="21" height="37" rx="1.5" fill="#CCC6BA"/></g>
<g filter="url(#bs)"><rect x="3" y="57" width="37" height="11" rx="1.5" fill="#C8C2B6"/><rect x="3" y="76" width="20" height="21" rx="1.5" fill="#CBC5B9"/><rect x="25" y="76" width="15" height="21" rx="1.5" fill="#C5BFB3"/></g>
<g filter="url(#bs)"><rect x="55" y="57" width="13" height="11" rx="1.5" fill="#CAC4B8"/><rect x="70" y="57" width="28" height="11" rx="1.5" fill="#C8C2B6"/><rect x="55" y="76" width="13" height="21" rx="1.5" fill="#C6C0B4"/><rect x="70" y="76" width="28" height="21" rx="1.5" fill="#CCC6BA"/></g>
<ellipse cx="121" cy="82" rx="9" ry="5.5" fill="#A8C8E0" opacity=".88"/>
<ellipse cx="121" cy="82" rx="9" ry="5.5" fill="none" stroke="#85AECA" stroke-width=".7"/>
<g filter="url(#bs)"><rect x="113" y="57" width="17" height="38" rx="1.5" fill="#CAC4B8"/><rect x="132" y="57" width="21" height="16" rx="1.5" fill="#C8C2B6"/><rect x="132" y="75" width="21" height="20" rx="1.5" fill="#C4BEB2"/></g>
<g filter="url(#bs)"><rect x="168" y="57" width="22" height="38" rx="1.5" fill="#CCC6BA"/><rect x="192" y="57" width="22" height="17" rx="1.5" fill="#C8C2B6"/><rect x="192" y="76" width="22" height="19" rx="1.5" fill="#C6C0B4"/></g>
<g filter="url(#bs)"><rect x="228" y="57" width="20" height="38" rx="1.5" fill="#C8C2B6"/><rect x="250" y="57" width="22" height="18" rx="1.5" fill="#CAC4B8"/><rect x="250" y="77" width="22" height="18" rx="1.5" fill="#C4BEB2"/></g>
<g filter="url(#bs)"><rect x="3" y="113" width="37" height="14" rx="1.5" fill="#C8C2B6"/><rect x="3" y="163" width="18" height="29" rx="1.5" fill="#CAC4B8"/><rect x="23" y="163" width="17" height="29" rx="1.5" fill="#C6C0B4"/></g>
<g filter="url(#bs)"><rect x="55" y="113" width="43" height="14" rx="1.5" fill="#CCC6BA"/><rect x="55" y="163" width="18" height="29" rx="1.5" fill="#C8C2B6"/><rect x="75" y="163" width="23" height="29" rx="1.5" fill="#C3BDB1"/></g>
<rect x="113" y="163" width="41" height="29" rx="2" fill="url(#gr)"/>
<ellipse cx="126" cy="178" rx="6.5" ry="7" fill="#85BC7E" opacity=".9"/>
<ellipse cx="143" cy="182" rx="5" ry="5.5" fill="#8EC487" opacity=".8"/>
<g filter="url(#bs)"><rect x="168" y="113" width="22" height="14" rx="1.5" fill="#CAC4B8"/><rect x="168" y="163" width="22" height="29" rx="1.5" fill="#C8C2B6"/></g>
<g filter="url(#bs)"><rect x="192" y="113" width="21" height="14" rx="1.5" fill="#C6C0B4"/><rect x="215" y="113" width="13" height="14" rx="1.5" fill="#CAC4B8"/><rect x="228" y="113" width="44" height="14" rx="1.5" fill="#CCC6BA"/><rect x="192" y="163" width="80" height="29" rx="1.5" fill="#C8C2B6"/><rect x="194" y="165" width="22" height="14" rx="1" fill="#C0BAB0"/><rect x="218" y="165" width="22" height="14" rx="1" fill="#C6C0B4"/><rect x="242" y="165" width="26" height="14" rx="1" fill="#C2BCB0"/><rect x="194" y="181" width="76" height="9" rx="1" fill="#BDB7AD"/></g>
<circle cx="44" cy="44" r="4.5" fill="#62A85B" opacity=".9"/><circle cx="52" cy="40" r="3" fill="#6DB466" opacity=".85"/><circle cx="40" cy="53" r="3.5" fill="#62A85B" opacity=".8"/><circle cx="52" cy="54" r="4" fill="#6DB466" opacity=".88"/>
<circle cx="102" cy="44" r="4" fill="#62A85B" opacity=".85"/><circle cx="110" cy="40" r="3" fill="#6DB466" opacity=".8"/><circle cx="98" cy="54" r="3.5" fill="#62A85B" opacity=".85"/>
<circle cx="102" cy="101" r="4" fill="#62A85B" opacity=".85"/><circle cx="110" cy="97" r="3" fill="#6DB466" opacity=".8"/><circle cx="98" cy="110" r="3.5" fill="#62A85B" opacity=".85"/><circle cx="110" cy="110" r="3" fill="#6DB466" opacity=".8"/>
<circle cx="158" cy="44" r="4.5" fill="#62A85B" opacity=".9"/><circle cx="166" cy="40" r="3" fill="#6DB466" opacity=".85"/><circle cx="154" cy="54" r="3.5" fill="#62A85B" opacity=".8"/><circle cx="166" cy="54" r="4" fill="#6DB466" opacity=".88"/>
<circle cx="158" cy="101" r="4" fill="#62A85B" opacity=".85"/><circle cx="166" cy="97" r="3" fill="#6DB466" opacity=".8"/><circle cx="154" cy="110" r="3.5" fill="#62A85B" opacity=".85"/>
<circle cx="218" cy="101" r="4" fill="#62A85B" opacity=".85"/><circle cx="226" cy="97" r="3" fill="#6DB466" opacity=".8"/><circle cx="214" cy="110" r="3.5" fill="#62A85B" opacity=".85"/><circle cx="226" cy="110" r="3" fill="#6DB466" opacity=".8"/>
<circle cx="80" cy="44" r="3.5" fill="#6DB466" opacity=".78"/><circle cx="92" cy="44" r="3" fill="#62A85B" opacity=".72"/><circle cx="80" cy="54" r="3" fill="#6DB466" opacity=".78"/><circle cx="138" cy="44" r="3.5" fill="#62A85B" opacity=".75"/><circle cx="230" cy="44" r="3.5" fill="#6DB466" opacity=".78"/><circle cx="240" cy="44" r="3" fill="#62A85B" opacity=".72"/>
<rect x="58" y="43" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="73" y="43" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="116" y="43" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="200" y="43" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="230" y="43" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/>
<rect x="58" y="55" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="134" y="55" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="172" y="55" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/>
<rect x="58" y="100" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="90" y="100" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="230" y="100" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="253" y="100" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/>
<rect x="10" y="150" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="58" y="150" width="9" height="4" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="194" y="150" width="9" height="4" rx="1.1" fill="#9CA4B0" opacity=".8"/>
<rect x="43" y="60" width="4" height="9" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="43" y="76" width="4" height="9" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="43" y="116" width="4" height="9" rx="1.1" fill="#9CA4B0" opacity=".8"/>
<rect x="157" y="60" width="4" height="9" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="157" y="78" width="4" height="9" rx="1.1" fill="#9CA4B0" opacity=".8"/><rect x="157" y="116" width="4" height="9" rx="1.1" fill="#96A0AC" opacity=".75"/><rect x="157" y="134" width="4" height="9" rx="1.1" fill="#9CA4B0" opacity=".8"/>
<path id="rp" d="M5,156 L36,156 Q48,156 48,144 L48,62 Q48,48 60,48 L150,48 Q162,48 162,62 L162,94 Q162,105 174,105 L270,105" fill="none"/>
<path d="M5,156 L36,156 Q48,156 48,144 L48,62 Q48,48 60,48 L150,48 Q162,48 162,62 L162,94 Q162,105 174,105 L270,105" stroke="rgba(59,130,246,.2)" stroke-width="2.8" stroke-dasharray="7 6" stroke-linecap="round"/>
<path d="M5,156 L36,156 Q48,156 48,144 L48,62 Q48,48 60,48 L150,48 Q162,48 162,62 L162,94 Q162,105 174,105 L270,105" stroke="url(#rg)" stroke-width="10" stroke-linecap="round" pathLength="1000" stroke-dasharray="1000" stroke-dashoffset="1000" opacity=".22">
  <animate attributeName="stroke-dashoffset" values="1000;0;0;1000;1000" keyTimes="0;0.63;0.84;0.855;1" dur="5.5s" begin="0.6s" repeatCount="indefinite" calcMode="spline" keySplines="0.4 0 0.2 1;0 0 1 1;0 0 0 1;0 0 1 1"/>
</path>
<path d="M5,156 L36,156 Q48,156 48,144 L48,62 Q48,48 60,48 L150,48 Q162,48 162,62 L162,94 Q162,105 174,105 L270,105" stroke="url(#rg)" stroke-width="2.8" stroke-linecap="round" pathLength="1000" stroke-dasharray="1000" stroke-dashoffset="1000">
  <animate attributeName="stroke-dashoffset" values="1000;0;0;1000;1000" keyTimes="0;0.63;0.84;0.855;1" dur="5.5s" begin="0.6s" repeatCount="indefinite" calcMode="spline" keySplines="0.4 0 0.2 1;0 0 1 1;0 0 0 1;0 0 1 1"/>
</path>
<circle cx="5" cy="156" r="8" fill="rgba(249,115,22,.13)"/>
<circle cx="5" cy="156" r="4.5" fill="#F97316"/>
<circle cx="5" cy="156" r="4" fill="none" stroke="#F97316" stroke-width="1.4" opacity="0">
  <animate attributeName="r" values="4;18" dur="2s" begin="0.6s" repeatCount="indefinite"/>
  <animate attributeName="opacity" values="0.6;0" dur="2s" begin="0.6s" repeatCount="indefinite"/>
</circle>
<g opacity="0">
  <circle r="13" fill="white" filter="url(#ps)"/>
  <rect x="-6.5" y="-6" width="13" height="10.5" rx="1.8" fill="#F97316"/>
  <line x1="-6.5" y1="-2" x2="6.5" y2="-2" stroke="rgba(255,255,255,.55)" stroke-width="1.2"/>
  <line x1="0" y1="-6" x2="0" y2="4.5" stroke="rgba(255,255,255,.42)" stroke-width="1.2"/>
  <path d="M-6.5 -8.5L0 -11L6.5 -8.5V-6H-6.5Z" fill="#EA580C"/>
  <animateMotion dur="5.5s" begin="0.6s" repeatCount="indefinite" keyPoints="0;1;1;0;0" keyTimes="0;0.63;0.84;0.855;1" calcMode="spline" keySplines="0.4 0 0.2 1;0 0 1 1;0 0 0 1;0 0 1 1" rotate="0">
    <mpath href="#rp"/>
  </animateMotion>
  <animate attributeName="opacity" values="0;1;1;0;0" keyTimes="0;0.05;0.80;0.855;1" dur="5.5s" begin="0.6s" repeatCount="indefinite"/>
</g>
<g transform="translate(270,105)" opacity="0">
  <ellipse cx="0" cy="12" rx="5.5" ry="2.5" fill="rgba(59,130,246,.18)"/>
  <path d="M0,-17 C-7,-17 -11,-10.5 -11,-5 C-11,4 0,15 0,15 C0,15 11,4 11,-5 C11,-10.5 7,-17 0,-17Z" fill="#3B82F6"/>
  <circle cy="-5" r="4.5" fill="white"/>
  <circle cy="-5" r="2" fill="#3B82F6"/>
  <circle cy="-5" r="4" fill="none" stroke="#3B82F6" stroke-width="1.8">
    <animate attributeName="r" values="4;20" dur="1.7s" begin="0.6s" repeatCount="indefinite"/>
    <animate attributeName="opacity" values="0.55;0" dur="1.7s" begin="0.6s" repeatCount="indefinite"/>
  </circle>
  <animate attributeName="opacity" values="0;0;1;1;0;0" keyTimes="0;0.60;0.64;0.84;0.855;1" dur="5.5s" begin="0.6s" repeatCount="indefinite"/>
</g>
</svg>
</body></html>
""".trimIndent()

@Composable
private fun MapSection() {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                // Solid background — transparent causes blank WebView on many devices
                setBackgroundColor(0xFFE4DDD3.toInt())
                isScrollContainer = false
                overScrollMode = android.view.View.OVER_SCROLL_NEVER
                // file:// base URL needed for SVG filter url() refs to resolve
                loadDataWithBaseURL("file:///android_asset/", MAP_HTML, "text/html", "UTF-8", null)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp)),
    )
}

// ─── Progress ──────────────────────────────────────────────────────────────────

@Composable
private fun ProgressSection() {
    val dots = rememberInfiniteTransition(label = "dots")
    val d1 by dots.animateFloat(0.25f, 1f, infiniteRepeatable(tween(650), RepeatMode.Reverse, StartOffset(0)),   label = "d1")
    val d2 by dots.animateFloat(0.25f, 1f, infiniteRepeatable(tween(650), RepeatMode.Reverse, StartOffset(180)), label = "d2")
    val d3 by dots.animateFloat(0.25f, 1f, infiniteRepeatable(tween(650), RepeatMode.Reverse, StartOffset(360)), label = "d3")

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(7.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf(d1, d2, d3).forEach { a ->
                Canvas(Modifier.size(4.dp)) { drawCircle(MutedColor.copy(alpha = a)) }
            }
        }
        Text("در حال بارگذاری", fontSize = 10.5.sp, color = MutedColor)
    }
}

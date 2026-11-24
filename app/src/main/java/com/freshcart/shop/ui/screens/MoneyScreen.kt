package com.freshcart.shop.ui.screens


import com.freshcart.shop.ui.Plex
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.freshcart.shop.data.AppState
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.StoreTabs
import com.freshcart.shop.ui.T
import com.freshcart.shop.ui.fmt
import com.freshcart.shop.ui.pressable
import kotlinx.coroutines.delay

private val Indigo = Color(0xFF4E46CD)
private val IndigoPill = Color(0xFF5A52D3)

data class Corridor(val flag: String, val country: String, val code: String, val rate: Double)

private val CORRIDORS = listOf(
    Corridor("🇮🇳", "India", "INR", 25.25), Corridor("🇵🇰", "Pakistan", "PKR", 75.27), Corridor("🇵🇭", "Philippines", "PHP", 17.29),
    Corridor("🇧🇩", "Bangladesh", "BDT", 31.30), Corridor("🇱🇰", "Sri Lanka", "LKR", 90.45), Corridor("🇪🇬", "Egypt", "EGP", 14.70),
    Corridor("🇳🇵", "Nepal", "NPR", 40.41),
)

@Composable
fun MoneyScreen(state: AppState) {
    var picked by remember { mutableStateOf<Corridor?>(null) }
    Column(Modifier.fillMaxSize().background(Color(0xFFF6F6F6))) {
        // Header: same store tabs in the same place as the other stores, then the headline, pills and the flag marquee.
        Column(Modifier.fillMaxWidth().background(Indigo, RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)).statusBarsPadding()) {
            Spacer(Modifier.height(10.9.dp))
            StoreTabs(state, Indigo)
            Spacer(Modifier.height(15.7.dp))
            Text(
                buildAnnotatedString {
                    append("Send money ")
                    withStyle(SpanStyle(color = Color(0xFF8FC1FF))) { append("fast") }
                    append(", worldwide")
                },
                style = TextStyle(fontFamily = Plex, fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 26.2.sp), color = Color.White,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Spacer(Modifier.height(11.6.dp))
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pill(Icons.Rounded.Bolt, "Within 30 mins*", Modifier.weight(1f))
                Pill(Icons.Outlined.CheckCircle, "No fees", Modifier.weight(1f))
                Pill(Icons.Outlined.TrendingUp, "Great rates", Modifier.weight(1f))
            }
            FlagMarquee { picked = it }
            Spacer(Modifier.height(12.2.dp))
        }
        AnimatedContent(picked, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "money") { c ->
            if (c == null) RatesCard { picked = it } else Converter(c) { picked = null }
        }
    }
}

@Composable
private fun Pill(icon: ImageVector, text: String, modifier: Modifier) {
    Row(
        modifier.height(30.dp).background(IndigoPill, RoundedCornerShape(15.dp)).border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(15.dp)),
        horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, null, tint = Color(0xFFFFD54F), modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text(text, style = TextStyle(fontFamily = Plex, fontSize = 11.5.sp, fontWeight = FontWeight.Medium), color = Color.White, maxLines = 1)
    }
}

/** Country flags drifting sideways (76 x 75.6 dp buttons on an 80 dp pitch); pauses while touched. */
@Composable
private fun FlagMarquee(onPick: (Corridor) -> Unit) {
    val list = rememberLazyListState()
    val loop = remember { List(12) { CORRIDORS }.flatten() }
    LaunchedEffect(Unit) {
        while (true) {
            delay(16)
            if (!list.isScrollInProgress) list.scrollBy(0.6f)
        }
    }
    LazyRow(state = list, modifier = Modifier.fillMaxWidth().padding(top = 12.dp).height(75.6.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        items(loop.size) { k ->
            val c = loop[k]
            Column(Modifier.width(76.dp).height(75.6.dp).clip(RoundedCornerShape(10.dp)).clickable { onPick(c) }, horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.height(8.dp))
                Box(Modifier.size(36.dp).background(Color.White, CircleShape), contentAlignment = Alignment.Center) {
                    Text(c.flag, style = TextStyle(fontFamily = Plex, fontSize = 23.sp))
                }
                Spacer(Modifier.height(6.dp))
                Text(c.country, style = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Medium), color = Color.White, maxLines = 1)
            }
        }
    }
}

@Composable
private fun RatesCard(onPick: (Corridor) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 24.dp)) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, C.Line, RoundedCornerShape(12.dp))) {
            Row(Modifier.fillMaxWidth().background(Color(0xFFECECED)).padding(horizontal = 16.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Send money abroad", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                    Text("Today's rate for 1 AED", style = T.small.copy(fontSize = 11.sp), color = C.Ink2)
                }
                Row(Modifier.height(29.8.dp).background(Color(0xFFE1E0F1), RoundedCornerShape(15.dp)).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.History, null, tint = Indigo, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("My Transactions", style = T.smallM, color = Indigo)
                }
            }
            Column(Modifier.fillMaxWidth().height(266.5.dp).verticalScroll(rememberScrollState())) { CORRIDORS.forEach { c ->
                Row(Modifier.fillMaxWidth().height(56.dp).clickable { onPick(c) }.padding(horizontal = 19.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(c.flag, style = TextStyle(fontFamily = Plex, fontSize = 20.sp))
                    Spacer(Modifier.width(16.dp))
                    Text(c.country, style = T.body, color = C.Ink, modifier = Modifier.weight(1f))
                    Text("%.2f".format(c.rate), style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
                    Text(" ${c.code}", style = T.small.copy(fontSize = 11.sp), color = C.Ink2)
                }
            }
            Box(
                Modifier.padding(horizontal = 16.dp, vertical = 10.dp).fillMaxWidth().height(48.dp).background(Indigo, RoundedCornerShape(8.dp))
                    .pressable { onPick(CORRIDORS.first()) }.testTag("send_money"),
                contentAlignment = Alignment.Center,
            ) { Text("Send Money", style = T.button, color = Color.White) }
            Text("Rates are indicative and confirmed before you pay. *Delivery times may vary.", style = T.small.copy(fontSize = 10.5.sp),
                color = C.Ink3, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp))
            Row(Modifier.fillMaxWidth().padding(top = 12.dp).background(Color(0xFFFBF2DE)).padding(vertical = 14.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text("Powered by ", style = T.small, color = C.Ink2)
                Text("Demo Cart Exchange", style = T.smallM.copy(fontWeight = FontWeight.Bold), color = C.Money)
            }
        }
    }
}

@Composable
private fun Converter(c: Corridor, onBack: () -> Unit) {
    var amount by remember { mutableStateOf("100.00") }
    var notice by remember { mutableStateOf(false) }
    val aed = amount.replace(",", "").toDoubleOrNull() ?: 0.0
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).border(1.dp, C.Line, RoundedCornerShape(12.dp)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Rounded.ArrowBack, "Back to rates", tint = C.Ink, modifier = Modifier.size(22.dp).clickable(onClick = onBack))
                Spacer(Modifier.width(10.dp))
                Text("Send money to ${c.country}", style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink, modifier = Modifier.weight(1f))
                Text("AED 1 = ${c.code} %.2f".format(c.rate), style = T.small, color = C.Ink2)
            }
            Spacer(Modifier.height(14.dp))
            AmountBox("From", "🇦🇪", "AED") {
                BasicTextField(amount, { amount = it.filter { ch -> ch.isDigit() || ch == '.' }.take(9) }, singleLine = true,
                    textStyle = TextStyle(fontFamily = Plex, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = C.Ink, textAlign = TextAlign.End),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth().testTag("money_amount"))
            }
            Box(Modifier.fillMaxWidth().height(30.dp), contentAlignment = Alignment.Center) {
                Box(Modifier.size(30.dp).background(Color(0xFFE1E0F1), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.SwapVert, null, tint = Indigo, modifier = Modifier.size(18.dp))
                }
            }
            AmountBox("To", c.flag, c.code) {
                Text(fmt(aed * c.rate), style = TextStyle(fontFamily = Plex, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End), color = C.Ink, modifier = Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(14.dp))
            Box(
                Modifier.fillMaxWidth().height(52.dp).background(Indigo, RoundedCornerShape(10.dp)).pressable { notice = true },
                contentAlignment = Alignment.Center,
            ) { Text("Send Money", style = T.button, color = Color.White) }
            if (notice) {
                Text("Demo app: transfers are not sent. This is where a real app would confirm the recipient and payment.",
                    style = T.small, color = C.Danger, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
            }
            Spacer(Modifier.height(14.dp))
            Row {
                Column(Modifier.weight(1f)) { Text("Fee", style = T.smallM, color = C.Ink); Text("No fees", style = T.small, color = C.Brand) }
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) { Text("ETA", style = T.smallM, color = C.Ink); Text("Within 30 minutes*", style = T.small, color = C.Ink2) }
            }
        }
    }
}

@Composable
private fun AmountBox(label: String, flag: String, code: String, value: @Composable () -> Unit) {
    Column(Modifier.fillMaxWidth().border(1.dp, C.Line, RoundedCornerShape(10.dp)).padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(label, style = T.small, color = C.Ink2)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(flag, style = TextStyle(fontFamily = Plex, fontSize = 18.sp))
            Spacer(Modifier.width(6.dp))
            Text(code, style = T.bodyM.copy(fontWeight = FontWeight.Bold), color = C.Ink)
            Spacer(Modifier.width(16.dp))
            Box(Modifier.weight(1f)) { value() }
        }
    }
}

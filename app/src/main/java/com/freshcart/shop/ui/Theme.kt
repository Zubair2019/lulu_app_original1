package com.freshcart.shop.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import com.freshcart.shop.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** IBM Plex Sans (OFL, IBM's static weights), the closest open font to the source's UI type. */
val Plex = FontFamily(
    Font(R.font.plex_regular, FontWeight.Normal),
    Font(R.font.plex_medium, FontWeight.Medium),
    Font(R.font.plex_semibold, FontWeight.SemiBold),
    Font(R.font.plex_bold, FontWeight.Bold),
    Font(R.font.plex_bold, FontWeight.ExtraBold),
)

/** Design tokens. Colours sampled from the source screens; sizes measured in dp on a 393 dp wide phone. */
object C {
    val Brand = Color(0xFF0F7F4A)          // brand green: selected store tab, buttons, prices on PDP
    val BrandDark = Color(0xFF0B6B3D)
    val BrandSoft = Color(0xFFE7F3EC)
    val Campaign = Color(0xFFDB272B)       // seasonal header colour (store header, hero)
    val CampaignDeep = Color(0xFFED0007)   // product-rail sections
    val CampaignSoft = Color(0xFFE58184)   // selected chip tab on the campaign header
    val TabRow = Color(0x66FFFFFF)         // translucent store-tab track on the header
    val Accent = Color(0xFFFFC72C)         // coupon code / flash sale yellow
    val Page = Color(0xFFFFFFFF)
    val Surface = Color(0xFFF6F6F6)        // search field, section backgrounds
    val Card = Color(0xFFF9F9FA)           // product image well
    val Line = Color(0xFFE6E6E8)
    val Glass = Color(0xE6EDEDED)          // floating bottom bar / view cart pill
    val Ink = Color(0xFF070708)            // measured: prices, chip labels, titles
    val Slate = Color(0xFF232D3B)          // measured: store-tab labels, View cart title
    val Ink2 = Color(0xFF5C6271)           // measured: inactive nav labels, secondary text
    val NavSel = Color(0xFF3A4454)         // measured: selected nav label
    val Hint = Color(0xFF6C7280)           // measured: search hint
    val Ink3 = Color(0xFF8A8D93)
    val Strike = Color(0xFF55585E)
    val OffBlue = Color(0xFF162A6F)        // measured: "34% OFF" badge text
    val OffBadge = Color(0xFFEFF2FA)
    val Danger = Color(0xFFD92D20)
    val Money = Color(0xFF0E5A9C)          // Demo Cart Money accent
    val Minutes = Color(0xFF6B21A8)        // Minutes accent (quick commerce)
    val MinutesSoft = Color(0xFFF3E8FF)
    val TabLift = Color(0x24FFFFFF)        // lifted ground of the selected store tab and the block it grows from
    val TabEdge = Color(0x59FFFFFF)        // its hairline edge
    val Slab = Color(0xFF1B1C1E)           // dark slab behind campaign words, close button
    val Scrim = Color(0x8C000000)          // dimmed list behind the product sheet
    val NavPill = Color(0xFFDADADA)        // selected item of the bottom bar
    val Star = Color(0xFFF5A623)
}

object D {
    val Gutter = 10.dp
    val StoreTabH = 40.dp
    val SearchH = 36.dp
    val ChipRowH = 68.dp
    val NavH = 64.dp
    val CardW = 112.dp
    val Radius = 12.dp
    val CardUnitGap = 2.dp      // product card: name to the pack-size line
    val CardPriceGap = 4.dp     // product card: pack-size line to the price
    val CardH = 200.dp          // a 117 dp wide product card: 117 picture + 7 + 32 name + 2 + 14 pack size + 4 + 19.6 price + 4.4
    val Touch = 48.dp           // smallest touch area for anything tappable

    // Layout of the redesigned screens, in dp on a 393 dp wide phone (measured on the reference layouts).
    val Side = 16.dp            // side margin of the home and product pages
    val AddrH = 56.dp           // address row at the top of a store
    val BigTabH = 70.dp         // store tabs: pictogram over label
    val SearchTall = 56.dp      // search field
    val TextTabH = 48.dp        // second tab row (40 dp on the reference; raised to the touch floor)
    val R12 = 12.dp
    val R16 = 16.dp
    val R20 = 20.dp
    val R24 = 24.dp
    val TileW = 96.dp           // offer tiles under the campaign strip
    val TileH = 114.dp
    val DishW = 108.dp          // product card inside a deal shelf
    val RailW = 86.dp           // aisle page: side rail of sections
    val RailItemH = 91.dp
    val GridGap = 12.dp         // aisle page: gap between product cards
}

/**
 * Type scale. Sizes solved from the source's measured ink widths with tools/calibrate_type.py
 * (same strings rendered in IBM Plex Sans at the measured weight); line heights from the measured text boxes.
 */
object T {
    val tab = TextStyle(fontFamily = Plex, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
    val delivery = TextStyle(fontFamily = Plex, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, lineHeight = 17.sp)
    val search = TextStyle(fontFamily = Plex, fontSize = 14.7.sp, fontWeight = FontWeight.Normal, lineHeight = 19.6.sp)
    val chip = TextStyle(fontFamily = Plex, fontSize = 12.9.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
    val section = TextStyle(fontFamily = Plex, fontSize = 17.6.sp, fontWeight = FontWeight.Bold, lineHeight = 19.6.sp)
    val viewCart = TextStyle(fontFamily = Plex, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)
    val viewCartSub = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 15.sp)
    val viewAll = TextStyle(fontFamily = Plex, fontSize = 11.4.sp, fontWeight = FontWeight.SemiBold, lineHeight = 12.sp)
    val h1 = TextStyle(fontFamily = Plex, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
    val h2 = TextStyle(fontFamily = Plex, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
    val h3 = TextStyle(fontFamily = Plex, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
    val body = TextStyle(fontFamily = Plex, fontSize = 14.sp, fontWeight = FontWeight.Normal, lineHeight = 19.sp)
    val bodyM = TextStyle(fontFamily = Plex, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 19.sp)
    val small = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 16.sp)
    val smallM = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
    val tiny = TextStyle(fontFamily = Plex, fontSize = 10.sp, fontWeight = FontWeight.Medium, lineHeight = 13.sp)
    val cardName = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Normal, lineHeight = 16.sp)
    val cardUnit = TextStyle(fontFamily = Plex, fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
    val addrLabel = TextStyle(fontFamily = Plex, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 22.sp)
    val storeTab = TextStyle(fontFamily = Plex, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
    val textTab = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, lineHeight = 16.sp, letterSpacing = 0.4.sp)
    val tile = TextStyle(fontFamily = Plex, fontSize = 12.sp, fontWeight = FontWeight.Bold, lineHeight = 14.sp)
    val railLabel = TextStyle(fontFamily = Plex, fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 14.sp)
    val priceBig = TextStyle(fontFamily = Plex, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
    val price = TextStyle(fontFamily = Plex, fontSize = 14.8.sp, fontWeight = FontWeight.SemiBold, lineHeight = 19.6.sp)
    val strike = TextStyle(fontFamily = Plex, fontSize = 8.7.sp, fontWeight = FontWeight.Normal, lineHeight = 12.sp)
    val badge = TextStyle(fontFamily = Plex, fontSize = 10.85.sp, fontWeight = FontWeight.Bold, lineHeight = 19.3.sp)
    val nav = TextStyle(fontFamily = Plex, fontSize = 11.7.sp, fontWeight = FontWeight.Normal, lineHeight = 14.2.sp)
    val button = TextStyle(fontFamily = Plex, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
}

@Composable
fun FreshCartTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = C.Brand, onPrimary = Color.White, background = C.Page, surface = C.Page,
            onSurface = C.Ink, onBackground = C.Ink, secondary = C.Campaign,
        ),
        content = content,
    )
}

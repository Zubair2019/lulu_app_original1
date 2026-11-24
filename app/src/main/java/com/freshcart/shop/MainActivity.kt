package com.freshcart.shop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import com.freshcart.shop.ui.App
import com.freshcart.shop.ui.C
import com.freshcart.shop.ui.FreshCartTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(C.Page.toArgb(), C.Page.toArgb()),
        )
        setContent { FreshCartTheme { App() } }
    }
}

package com.example.syncup.ui.navbar

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState


data class BottomBarItemData(
    val title: String,
    val route: String,
    @DrawableRes val image: Int
)


@Composable
fun SingleBottomBarItem(
    item: BottomBarItemData,
    modifier: Modifier = Modifier,
    onItemClick: () -> Unit
) {

    Box(contentAlignment = Alignment.Center, modifier = modifier.padding(5.dp)) {
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = modifier
                .size(50.dp)
                .padding(8.dp)
                .clickable {
                    onItemClick()
                }
        )
    }
}


@Composable
fun BottomNavigationBar(
    navController: NavController,
    items: List<BottomBarItemData>,
    onItemClick: (BottomBarItemData) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .height(60.dp),
        containerColor = MaterialTheme.colorScheme.background,
//        tonalElevation = 5.dp,

        ) {

        val backStackEntry = navController.currentBackStackEntryAsState()

        items.forEach { item ->

            val selectedRoute = item.route == backStackEntry.value?.destination?.route

            NavigationBarItem(
                modifier = Modifier.padding(5.dp),
                selected = selectedRoute,
                onClick = { onItemClick(item) },
                icon = {
                    Image(
                        painter = painterResource(id = item.image),
                        contentDescription = null,
                        modifier = Modifier
                            .size(35.dp)
                            .padding(5.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = MaterialTheme.colorScheme.background,
//                    indicatorColor = Color.White,
                )

            )
        }
    }
}


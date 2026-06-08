package com.moviles.jobmatch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.jobmatch.navigation.AppDestinations
import com.moviles.jobmatch.ui.theme.JobMatchColors

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Inicio : BottomNavItem(AppDestinations.SEARCH_COMPANY, "Inicio", Icons.Outlined.Home)
    object Explorar : BottomNavItem(AppDestinations.JOBS_EXPLORE, "Explorar", Icons.Outlined.Search)
    object Trabajos : BottomNavItem(AppDestinations.MY_JOBS, "Trabajos", Icons.Outlined.Work)
    object Alertas : BottomNavItem(AppDestinations.ALERTS, "Alertas", Icons.Outlined.Notifications)
    object Perfil : BottomNavItem(AppDestinations.PROFILE, "Perfil", Icons.Outlined.Person)
}

@Composable
fun JobMatchBottomBar(
    currentRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem.Inicio,
        BottomNavItem.Explorar,
        BottomNavItem.Trabajos,
        BottomNavItem.Alertas,
        BottomNavItem.Perfil
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .shadow(elevation = 15.dp, clip = false)
            .background(Color.LightGray.copy(alpha = 0.5f))
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp,
        modifier = modifier
    ) {
        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onItemSelected(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.height(24.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 11.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = JobMatchColors.bottomNavSelected,
                    selectedTextColor = JobMatchColors.bottomNavSelected,
                    unselectedIconColor = JobMatchColors.bottomNavUnselected,
                    unselectedTextColor = JobMatchColors.bottomNavUnselected,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}
package com.moviles.jobmatch.navigation

/**
 * Mapeo lógico de las pestañas siguiendo el estándar de la profe.
 */
enum class MainTab {
    Inicio,
    Explorar,
    MisTrabajos,
    Perfil
}

fun routeToMainTab(route: String?): MainTab {
    val r = route.orEmpty()
    return when {
        r == AppDestinations.SEARCH_COMPANY ||
                r.startsWith("${AppDestinations.COMPANY_PROFILE}/") -> MainTab.Inicio
        r == AppDestinations.JOBS_EXPLORE -> MainTab.Explorar
        else -> MainTab.Inicio
    }
}
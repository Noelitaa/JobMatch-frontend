package com.moviles.jobmatch.data

object CompanyMap {
    // Mapa de nombre de empresa ID
    val companies = mapOf(
        "Tech Solutions" to "D3888166-1643-435E-BC10-347D8DEB285C",
        "Nueva Tech Solutions" to "FE4CA8C7-649F-4677-1E7B-08DEA6254CF1"
    )

    // Obtener todas las empresas para mostrar en lista
    fun getAllCompanies(): List<Pair<String, String>> {
        return companies.map { it.key to it.value }
    }

    // Buscar ID por nombre
    fun getCompanyIdByName(name: String): String? {
        return companies[name]
    }
}
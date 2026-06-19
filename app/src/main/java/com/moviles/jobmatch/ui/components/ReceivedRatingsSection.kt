package com.moviles.jobmatch.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.moviles.jobmatch.data.remote.model.ReceivedRatingResponse
import com.moviles.jobmatch.ui.theme.DarkBlue

@Composable
fun ReceivedRatingsSection(
    isLoading: Boolean,
    error: String?,
    ratings: List<ReceivedRatingResponse>
) {
    SectionHeader(title = "Calificaciones recibidas")
    Spacer(modifier = Modifier.height(8.dp))
    when {
        isLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = DarkBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        error != null -> {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        ratings.isEmpty() -> {
            Text(
                text = "Aún no tienes calificaciones recibidas",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF9AA5B4)
            )
        }
        else -> {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ratings.forEach { rating ->
                    ReceivedRatingCard(rating = rating)
                }
            }
        }
    }
}

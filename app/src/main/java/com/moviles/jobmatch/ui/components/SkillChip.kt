package com.moviles.jobmatch.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moviles.jobmatch.ui.theme.DarkBlue
import com.moviles.jobmatch.ui.theme.LightBlue

@Composable
fun SkillChip(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = LightBlue,
    textColor: Color = DarkBlue,
    shape: Shape = CircleShape,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Surface(
        shape = shape,
        color = backgroundColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontSize = fontSize,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

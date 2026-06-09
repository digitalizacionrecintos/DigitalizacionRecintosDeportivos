package org.example.project.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.example.project.presentation.theme.MuniColors
import org.example.project.presentation.theme.MuniElevation
import org.example.project.presentation.theme.MuniGradients
import org.example.project.presentation.theme.MuniShapes
import org.example.project.presentation.theme.MuniSpacing

enum class CardStyle {
    Default, Elevated, Flat, AccentLeft
}

@Composable
fun MuniCard(
    style: CardStyle = CardStyle.Default,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = MuniShapes.card
    val elevation = when (style) {
        CardStyle.Default -> MuniElevation.subtle
        CardStyle.Elevated -> MuniElevation.raised
        CardStyle.Flat -> MuniElevation.flat
        CardStyle.AccentLeft -> MuniElevation.subtle
    }
    val colors = CardDefaults.cardColors(
        containerColor = MuniColors.surfaceCard
    )

    if (onClick != null) {
        Card(
            onClick = onClick,
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = colors,
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(MuniSpacing.lg)) {
                content()
            }
        }
    } else {
        Card(
            shape = shape,
            elevation = CardDefaults.cardElevation(defaultElevation = elevation),
            colors = colors,
            modifier = modifier
        ) {
            Column(modifier = Modifier.padding(MuniSpacing.lg)) {
                content()
            }
        }
    }
}

@Composable
fun MuniInfoCard(
    label: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    MuniCard(style = CardStyle.Flat, modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            icon()
            Spacer(Modifier.width(MuniSpacing.sm))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MuniColors.mediumGray,
                    fontSize = 10.sp
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MuniColors.darkGray
                )
            }
        }
    }
}

@Composable
fun MuniEventCard(
    imageUrl: String?,
    title: String,
    tags: @Composable RowScope.() -> Unit = {},
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = MuniShapes.card,
        elevation = CardDefaults.cardElevation(defaultElevation = MuniElevation.subtle),
        colors = CardDefaults.cardColors(containerColor = MuniColors.surfaceCard),
        modifier = modifier
    ) {
        Column {
            Box {
                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .background(MuniColors.lightGray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = MuniColors.lightGray,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MuniGradients.imageOverlay, MuniShapes.card)
                )
            }
            Column(modifier = Modifier.padding(MuniSpacing.lg)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MuniColors.darkGray,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(MuniSpacing.sm))
                Row(horizontalArrangement = Arrangement.spacedBy(MuniSpacing.sm)) {
                    tags()
                }
            }
        }
    }
}

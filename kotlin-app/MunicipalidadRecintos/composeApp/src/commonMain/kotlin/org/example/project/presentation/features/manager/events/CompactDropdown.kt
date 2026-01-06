package org.example.project.presentation.features.manager.events

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompactDropdown(
        label: String,
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit,
        modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {

        Surface(
                onClick = { expanded = !expanded },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFF3F4F6),
                modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = label, fontSize = 10.sp, color = Color(0xFF6B7280))
                    Text(
                            text = selectedOption,
                            fontSize = 13.sp,
                            color = Color(0xFF1A1A1A),
                            fontWeight = FontWeight.Medium
                    )
                }
                Icon(
                        imageVector =
                                if (expanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(18.dp)
                )
            }
        }

        DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.fillMaxWidth(0.3f)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                        text = {
                            Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    color =
                                            if (option == selectedOption) Color(0xFF040581)
                                            else Color(0xFF1A1A1A)
                            )
                        },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        },
                        modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

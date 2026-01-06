package org.example.project.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun YearPickerDialog(onDismiss: () -> Unit, onYearSelected: (Int) -> Unit) {
    var selectedYear by remember { mutableStateOf(2025) }
    val years = (2024..2030).toList()

    AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Seleccionar Año") },
            text = {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                    LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        items(years.size) { index ->
                            val year = years[index]
                            val isSelected = selectedYear == year
                            TextButton(
                                    onClick = { selectedYear = year },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors =
                                            ButtonDefaults.textButtonColors(
                                                    contentColor =
                                                            if (isSelected) Color(0xFF043CC7)
                                                            else Color.Gray
                                            )
                            ) {
                                Text(
                                        text = year.toString(),
                                        fontWeight =
                                                if (isSelected) FontWeight.Bold
                                                else FontWeight.Normal,
                                        fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { onYearSelected(selectedYear) }) { Text("Confirmar") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

package tu.paquete.presentation.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import municipalidadrecintos.composeapp.generated.resources.DM_Sans
import municipalidadrecintos.composeapp.generated.resources.DM_Sans_Italic
import municipalidadrecintos.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun getPoppinsFontFamily(): FontFamily {
        return FontFamily(
                Font(resource = Res.font.DM_Sans_Italic, weight = FontWeight.Normal),
                Font(resource = Res.font.DM_Sans, weight = FontWeight.Bold)
        )
}

@Composable
fun getAppTypography(): Typography {
        val poppins = getPoppinsFontFamily()

        return Typography(

                headlineMedium =
                        TextStyle(
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 28.sp
                        ),

                titleMedium =
                        TextStyle(
                                fontFamily = poppins,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                        ),

                bodyMedium =
                        TextStyle(
                                fontFamily = poppins,
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp
                        ),

                labelLarge =
                        TextStyle(
                                fontFamily = poppins,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                        )
        )
}

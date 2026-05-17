package org.example.project.presentation.features.events.detail

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import municipalidadrecintos.composeapp.generated.resources.Res
import municipalidadrecintos.composeapp.generated.resources.logo_muni_arica
import org.example.project.getPlatform
import org.example.project.presentation.components.HoldToConfirmButton
import org.example.project.presentation.components.SpotlightCoachMark
import org.jetbrains.compose.resources.painterResource

enum class InscriptionState {
	INITIAL,
	LOADING,
	SUCCESS,
	ALREADY_REGISTERED
}

data class EventDetailScreen(val eventId: String) : Screen {

	@Composable
	override fun Content() {

		val navigator = LocalNavigator.currentOrThrow
		val viewModel = remember { EventDetailViewModel() }
		val state by viewModel.state.collectAsState()

		val snackbarHostState = remember { SnackbarHostState() }

		LaunchedEffect(eventId) {
			viewModel.loadEvent(eventId)
			viewModel.checkTutorialStatus()
		}

		LaunchedEffect(state.feedbackMessage) {
			state.feedbackMessage?.let {
				snackbarHostState.showSnackbar(it)
				viewModel.clearFeedback()
			}
		}

		Box(modifier = Modifier.fillMaxSize()) {
			EventDetailScreenContent(
				state = state,
				onBackClick = { navigator.pop() },
				onInscribeClick = { viewModel.inscribeUser() },
				onDismissTooltip = { viewModel.dismissTooltip() }
			)

			SnackbarHost(
				hostState = snackbarHostState,
				modifier = Modifier.align(Alignment.BottomCenter)
			)
		}
	}
}

@Composable
fun EventDetailScreenContent(
	state: EventDetailState,
	onBackClick: () -> Unit,
	onInscribeClick: () -> Unit,
	onDismissTooltip: () -> Unit
) {
	var buttonRect by remember { mutableStateOf<Rect?>(null) }

	Box(modifier = Modifier.fillMaxSize()) {
		Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.background(
						Brush.horizontalGradient(
							colors = listOf(
								Color(0xFF001F5C),
								Color(0xFF023075),
								Color(0xFF0D47A1)
							)
						)
					).statusBarsPadding()
					.padding(horizontal = 20.dp, vertical = 12.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Volver",
					tint = Color.White,
					modifier = Modifier.size(24.dp).clickable { onBackClick() }
				)
				Spacer(modifier = Modifier.width(16.dp))
				Image(
					painter = painterResource(Res.drawable.logo_muni_arica),
					contentDescription = "Logo",
					modifier =
						Modifier.size(48.dp)
							.clip(RoundedCornerShape(8.dp))
							.background(Color.White.copy(alpha = 0.1f))
							.padding(4.dp),
					contentScale = ContentScale.Fit
				)
				Spacer(modifier = Modifier.width(16.dp))
				Column {
					Text(
						text = "Detalle evento",
						color = Color.White,
						fontWeight = FontWeight.Bold,
						style = MaterialTheme.typography.titleLarge,
						fontSize = 22.sp
					)
					Text(
						text = "Información completa",
						color = Color.White.copy(alpha = 0.9f),
						style = MaterialTheme.typography.bodySmall,
						fontSize = 12.sp
					)
				}
			}
			when {
				state.isLoading -> {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally
						) {
							CircularProgressIndicator(color = Color(0xFF043CC7))
							Spacer(modifier = Modifier.height(16.dp))
							Text(
								"Cargando evento...",
								color = Color.Gray,
								style =
									MaterialTheme.typography
										.bodyMedium
							)
						}
					}
				}

				state.event == null -> {
					Box(
						modifier = Modifier.fillMaxSize(),
						contentAlignment = Alignment.Center
					) {
						Column(
							horizontalAlignment = Alignment.CenterHorizontally,
							modifier = Modifier.padding(16.dp)
						) {
							Text(
								"No se pudo cargar el evento",
								color = Color(0xFF043CC7),
								style =
									MaterialTheme.typography
										.titleMedium,
								fontWeight = FontWeight.Bold
							)
							Spacer(modifier = Modifier.height(8.dp))
							Text(
								"Por favor, intenta nuevamente.",
								color = Color.Gray,
								style =
									MaterialTheme.typography
										.bodyMedium
							)
						}
					}
				}

				else -> {

					val event = state.event

					val initialState =
						if (!event.canEnroll) {
							InscriptionState.ALREADY_REGISTERED
						} else {
							InscriptionState.INITIAL
						}

					var inscriptionState by
					remember(event.canEnroll) {
						mutableStateOf(initialState)
					}

					LaunchedEffect(inscriptionState) {
						if (inscriptionState == InscriptionState.LOADING) {
							delay(2000)
							inscriptionState = InscriptionState.SUCCESS
							onInscribeClick()
						}
					}

					Column(modifier = Modifier.weight(1f)) {

						Column(
							modifier =
								Modifier.weight(1f)
									.verticalScroll(
										rememberScrollState()
									)
						) {

							if (!event.imagenUrl.isNullOrBlank()) {
								coil3.compose.AsyncImage(
									model = event.imagenUrl,
									contentDescription =
										"Imagen Evento",
									modifier =
										Modifier.height(
											160.dp
										)
											.fillMaxWidth(),
									contentScale =
										ContentScale.Crop
								)
							} else {
								Box(
									modifier =
										Modifier.height(
											160.dp
										)
											.fillMaxWidth()
											.background(
												Color.Gray
											),
									contentAlignment =
										Alignment.Center
								) {
									Icon(
										imageVector =
											Icons.Default
												.Image,
										contentDescription =
											"Sin imagen",
										tint = Color.White,
										modifier =
											Modifier.size(
												48.dp
											)
									)
								}
							}

							Column(
								modifier =
									Modifier.padding(
										horizontal = 16.dp,
										vertical = 12.dp
									)
							) {

								Text(
									text = event.title,
									fontSize = 24.sp,
									color = Color(0xFF0D47A1),
									fontWeight =
										FontWeight.Black,
									lineHeight = 28.sp
								)

								Spacer(
									modifier =
										Modifier.height(
											8.dp
										)
								)

								Text(
									text = event.description,
									fontSize = 14.sp,
									color = Color.Gray,
									lineHeight = 20.sp,
									modifier =
										Modifier.padding(
											bottom =
												8.dp
										)
								)

								Spacer(
									modifier =
										Modifier.height(
											8.dp
										)
								)

								Column(
									verticalArrangement =
										Arrangement
											.spacedBy(
												12.dp
											),
									modifier =
										Modifier.fillMaxWidth()
								) {

									event.categoria?.let { cat
										->
										CategoryCard(
											categoryName =
												cat.nombre
										)
									}

									val encargadoName =
										if (event.encargado !=
											null
										) {
											"${event.encargado.nombre} ${event.encargado.apellido}"
										} else {
											event.organizerName
												.replace(
													"Encargado ",
													""
												)
										}
									EncargadoCard(
										organizerName =
											encargadoName
									)

									event.cupoMaximo?.let { max
										->
										CapacityCard(
											maxCapacity =
												max
										)
									}
								}

								Spacer(
									modifier =
										Modifier.height(
											12.dp
										)
								)

								Row(
									horizontalArrangement =
										Arrangement
											.spacedBy(
												8.dp
											),
									modifier =
										Modifier.fillMaxWidth()
								) {

									Row(
										verticalAlignment =
											Alignment
												.CenterVertically,
										modifier =
											Modifier.clip(
												RoundedCornerShape(
													20.dp
												)
											)
												.background(
													Color(
														0xFFE3F2FD
													)
												)
												.padding(
													horizontal =
														12.dp,
													vertical =
														8.dp
												)
									) {
										Icon(
											Icons.Default
												.DateRange,
											contentDescription =
												null,
											tint =
												Color(
													0xFF1976D2
												),
											modifier =
												Modifier.size(
													20.dp
												)
										)
										Spacer(
											modifier =
												Modifier.width(
													6.dp
												)
										)
										Text(
											text =
												event.date,
											fontSize =
												14.sp,
											fontWeight =
												FontWeight
													.Medium,
											color =
												Color(
													0xFF1976D2
												)
										)
									}

									Row(
										verticalAlignment =
											Alignment
												.CenterVertically,
										modifier =
											Modifier.clip(
												RoundedCornerShape(
													20.dp
												)
											)
												.background(
													Color(
														0xFFE0F7FA
													)
												)
												.padding(
													horizontal =
														12.dp,
													vertical =
														8.dp
												)
												.weight(
													1f,
													fill =
														false
												)
									) {
										Icon(
											Icons.Default
												.LocationOn,
											contentDescription =
												null,
											tint =
												Color(
													0xFF00BCD4
												),
											modifier =
												Modifier.size(
													20.dp
												)
										)
										Spacer(
											modifier =
												Modifier.width(
													6.dp
												)
										)
										Text(
											text =
												event.location,
											fontSize =
												14.sp,
											fontWeight =
												FontWeight
													.Medium,
											color =
												Color(
													0xFF00BCD4
												),
											maxLines = 1
										)
									}
								}

								Spacer(
									modifier =
										Modifier.height(
											12.dp
										)
								)

								Spacer(
									modifier =
										Modifier.height(
											12.dp
										)
								)

								Card(
									modifier =
										Modifier.fillMaxWidth(),
									shape =
										RoundedCornerShape(
											12.dp
										),
									colors =
										CardDefaults
											.cardColors(
												containerColor =
													Color(
														0xFFF0F4FF
													)
											),
									elevation =
										CardDefaults
											.cardElevation(
												0.dp
											)
								) {
									Row(
										modifier =
											Modifier.fillMaxWidth()
												.padding(
													12.dp
												),
										verticalAlignment =
											Alignment
												.CenterVertically
									) {

										Box(
											modifier =
												Modifier.size(
													44.dp
												)
													.clip(
														RoundedCornerShape(
															10.dp
														)
													)
													.background(
														Color(
															0xFF043CC7
														)
													),
											contentAlignment =
												Alignment
													.Center
										) {
											Icon(
												Icons.Default
													.LocationOn,
												contentDescription =
													null,
												tint =
													Color.White,
												modifier =
													Modifier.size(
														24.dp
													)
											)
										}

										Spacer(
											modifier =
												Modifier.width(
													12.dp
												)
										)

										Column(
											modifier =
												Modifier.weight(
													1f
												)
										) {
											Text(
												text =
													"Ubicación del evento",
												style =
													MaterialTheme
														.typography
														.labelSmall,
												color =
													Color.Gray
											)
											Text(
												text =
													event.location,
												style =
													MaterialTheme
														.typography
														.bodyMedium,
												fontWeight =
													FontWeight
														.Medium,
												color =
													Color(
														0xFF333333
													)
											)
										}

										Button(
											onClick = {
												val coords =
													event.recinto
														?.coordenadasGPS
														?: ""
												if (coords.isNotBlank()
												) {
													try {
														val parts =
															coords.split(
																","
															)
														if (parts.size >=
															2
														) {
															val lat =
																parts[
																	0]
																	.trim()
																	.toDoubleOrNull()
																	?: 0.0
															val lng =
																parts[
																	1]
																	.trim()
																	.toDoubleOrNull()
																	?: 0.0
															if (lat !=
																0.0 &&
																lng !=
																0.0
															) {
																org.example
																	.project
																	.getPlatform()
																	.openMaps(
																		lat,
																		lng,
																		event.location
																	)
															}
														}
													} catch (
														e:
														Exception
													) {
													}
												}
											},
											colors =
												ButtonDefaults
													.buttonColors(
														containerColor =
															Color(
																0xFF043CC7
															)
													),
											shape =
												RoundedCornerShape(
													8.dp
												),
											contentPadding =
												PaddingValues(
													horizontal =
														12.dp,
													vertical =
														8.dp
												)
										) {
											Text(
												"Ver mapa",
												fontSize =
													12.sp
											)
										}
									}
								}
							}
						}

						Box(
							modifier =
								Modifier.fillMaxWidth()
									.padding(16.dp)
						) {
							AnimatedContent(
								targetState = inscriptionState,
								transitionSpec = {
									fadeIn(
										animationSpec =
											tween(300)
									) togetherWith
											fadeOut(
												animationSpec =
													tween(
														300
													)
											)
								}
							) { targetInscriptionState ->
								when (targetInscriptionState) {
									InscriptionState
										.INITIAL -> {

										HoldToConfirmButton(
											modifier =
												Modifier.fillMaxWidth()
													.onGloballyPositioned { coordinates
														->
														buttonRect =
															coordinates
																.boundsInRoot()
													},
											onConfirm = {
												inscriptionState =
													InscriptionState
														.LOADING
											}
										)
									}

									InscriptionState
										.LOADING -> {
										Button(
											onClick = {
											},
											modifier =
												Modifier.fillMaxWidth()
													.height(
														56.dp
													),
											shape =
												RoundedCornerShape(
													12.dp
												),
											colors =
												ButtonDefaults
													.buttonColors(
														containerColor =
															Color(
																0xFF000080
															)
													),
											enabled =
												false
										) {
											CircularProgressIndicator(
												color =
													Color.White
											)
										}
									}

									InscriptionState
										.SUCCESS -> {
										Card(
											modifier =
												Modifier.fillMaxWidth()
													.height(
														56.dp
													),

											shape =
												RoundedCornerShape(
													12.dp
												),
											colors =
												CardDefaults
													.cardColors(
														containerColor =
															Color(
																0xFF02BB94
															)
													),
											elevation =
												CardDefaults
													.cardElevation(
														4.dp
													)
										) {
											Row(
												modifier =
													Modifier.fillMaxSize()
														.padding(
															horizontal =
																16.dp
														),
												verticalAlignment =
													Alignment
														.CenterVertically,
												horizontalArrangement =
													Arrangement
														.Center
											) {
												Icon(
													imageVector =
														Icons.Default
															.CheckCircle,
													contentDescription =
														"Éxito",
													tint =
														Color.White,
													modifier =
														Modifier.size(
															24.dp
														)
												)

												Spacer(
													modifier =
														Modifier.width(
															12.dp
														)
												)

												Text(
													text =
														"¡Inscripción Exitosa!",
													color =
														Color.White,
													fontSize =
														18.sp,
													fontWeight =
														FontWeight
															.Bold
												)
											}
										}
									}

									InscriptionState
										.ALREADY_REGISTERED -> {

										val isCuposMessage =
											event.enrolledStatus
												.contains(
													"cupo",
													ignoreCase =
														true
												) ||
													event.enrolledStatus
														.contains(
															"lleno",
															ignoreCase =
																true
														) ||
													event.enrolledStatus
														.contains(
															"disponible",
															ignoreCase =
																true
														)

										val containerColor =
											if (isCuposMessage
											)
												Color(
													0xFFE0E0E0
												)
											else
												Color(
													0xFFE0E0E0
												)
										val contentColor =
											if (isCuposMessage
											)
												Color(
													0xFF333333
												)
											else
												Color.Gray
										val iconToShow =
											if (isCuposMessage
											)
												Icons.Default
													.Warning
											else
												Icons.Default
													.CheckCircle

										Button(
											onClick = {
											},

											modifier =
												Modifier.fillMaxWidth()
													.height(
														56.dp
													),
											shape =
												RoundedCornerShape(
													12.dp
												),
											colors =
												ButtonDefaults
													.buttonColors(
														disabledContainerColor =
															containerColor,
														disabledContentColor =
															contentColor
													),
											enabled =
												false
										) {
											Icon(
												iconToShow,
												null,
												modifier =
													Modifier.size(
														20.dp
													)
											)
											Spacer(
												modifier =
													Modifier.width(
														8.dp
													)
											)
											Text(
												text =
													event.enrolledStatus
														.ifBlank {
															"Ya estás inscrito"
														},
												fontSize =
													16.sp,
												fontWeight =
													FontWeight
														.Bold
											)
										}
									}
								}
							}
						}
					}
				}
			}
		}

		if (state.event != null) {
			SpotlightCoachMark(
				isVisible = state.showInscriptionTooltip,
				targetRect = buttonRect,
				text = "",
				onDismiss = onDismissTooltip
			)
		}
	}
}

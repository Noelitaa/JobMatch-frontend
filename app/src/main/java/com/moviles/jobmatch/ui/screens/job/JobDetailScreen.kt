package com.moviles.jobmatch.ui.screens.job

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.jobmatch.data.AuthSession
import com.moviles.jobmatch.data.remote.model.JobDetailResponse
import com.moviles.jobmatch.ui.theme.DarkBlue
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: Int,
    onBackPressed: () -> Unit = {},
    onViewApplicants: (Int, String) -> Unit = { _, _ -> },
    onEditJob: (Int) -> Unit = {},
    onCompanyClick: (String) -> Unit = {},
    viewModel: JobDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(jobId) {
        viewModel.loadJobDetail(jobId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Detalles de la Oferta",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Información",
                            tint = DarkBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            if (uiState is JobDetailUiState.Success) {
                val job = (uiState as JobDetailUiState.Success).job
                if (AuthSession.isCompany) {
                    CompanyJobBottomBar(
                        onViewApplicants = { onViewApplicants(job.idJob, job.title) },
                        onEdit = { onEditJob(job.idJob) }
                    )
                } else {
                    JobDetailBottomBar()
                }
            }
        },
        containerColor = Color(0xFFF8F9FB)
    ) { paddingValues ->
        when (val state = uiState) {
            is JobDetailUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = DarkBlue)
                }
            }

            is JobDetailUiState.Success -> {
                JobDetailContent(
                    job = state.job,
                    onCompanyClick = onCompanyClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is JobDetailUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            modifier = Modifier.size(52.dp),
                            tint = Color(0xFFBBBBBB)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.loadJobDetail(jobId) },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JobDetailContent(
    job: JobDetailResponse,
    onCompanyClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }
        item { JobHeaderCard(job = job, onCompanyClick = onCompanyClick) }
        item { InfoCardsRow(job = job) }
        item { CompatibilityCard() }
        item { DescriptionCard(job = job) }
        if (!job.deliverables.isNullOrBlank()) {
            item { DeliverablesCard(deliverables = job.deliverables) }
        }
        item { ScheduleCard(job = job) }
        item { LocationCard(job = job) }
        item { TrustCard() }
    }
}

@Composable
private fun JobHeaderCard(job: JobDetailResponse, onCompanyClick: (String) -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = formatJobType(job.type),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        color = DarkBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8F4FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (job.company.companyName ?: "E").take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = DarkBlue
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = job.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E),
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onCompanyClick(job.company.id) }
                    .padding(vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Business,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = job.company.companyName ?: "Empresa",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkBlue,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
private fun InfoCardsRow(job: JobDetailResponse) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachMoney,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = formatPaymentLabel(job.paymentType),
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = formatPayment(job.payment),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A2E)
                )
            }
        }

        Card(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "DISTANCIA",
                    fontSize = 10.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Cerca de ti",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1A1A2E)
                )
            }
        }
    }
}

@Composable
private fun CompatibilityCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFBDD8F5))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressBadge(progress = 0.85f, label = "85%")
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Alta Compatibilidad",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DarkBlue
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Este trabajo se adapta bien a tu perfil y disponibilidad.",
                    fontSize = 12.sp,
                    color = Color(0xFF5A6A7A),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun CircularProgressBadge(progress: Float, label: String) {
    Box(
        modifier = Modifier.size(64.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(64.dp)) {
            val strokeWidth = 6.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
            val arcSize = Size(radius * 2, radius * 2)

            drawArc(
                color = Color(0xFFD0E8FF),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color(0xFF2196F3),
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = DarkBlue
        )
    }
}

@Composable
private fun DescriptionCard(job: JobDetailResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Descripción del puesto",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = job.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF5A6A7A),
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun DeliverablesCard(deliverables: String) {
    val items = deliverables.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Requisitos y Entregables",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(14.dp))
            items.forEach { item ->
                Row(
                    modifier = Modifier.padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = DarkBlue,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = item,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3A4A5A)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScheduleCard(job: JobDetailResponse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Horario",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A2E)
                )
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Sin conflictos",
                            fontSize = 11.sp,
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = Color(0xFFF8F9FB),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = DarkBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = formatDate(job.workDate),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF1A1A2E)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = DarkBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${formatTime(job.startTime)} - ${formatTime(job.endTime)}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = Color(0xFF1A1A2E)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFFEEEEEE))
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Llega 10 minutos antes de la hora indicada",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationCard(job: JobDetailResponse) {
    val context = LocalContext.current
    val companyName = job.company.companyName ?: job.title

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Ubicación",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                MapPlaceholder()

                Button(
                    onClick = {
                        val query = Uri.encode(companyName)
                        val wazeUri = Uri.parse("waze://?q=$query&navigate=yes")
                        val mapsUri = Uri.parse("geo:0,0?q=$query")
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, wazeUri))
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, mapsUri))
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = Color(0xFF00BCD4),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Abrir en Waze",
                        fontSize = 12.sp,
                        color = Color(0xFF1A1A2E),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = companyName,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF5A6A7A)
                )
            }
        }
    }
}

@Composable
private fun MapPlaceholder() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = Color(0xFFE8EDD9))

        val roadColor = Color(0xFFFFFFFF)
        val roadWidth = 4.dp.toPx()
        val roadBorder = Color(0xFFDDE3CF)
        val borderWidth = 1.dp.toPx()

        listOf(size.height * 0.32f, size.height * 0.62f).forEach { y ->
            drawLine(roadBorder, Offset(0f, y - borderWidth), Offset(size.width, y - borderWidth), strokeWidth = borderWidth)
            drawLine(roadColor, Offset(0f, y), Offset(size.width, y), strokeWidth = roadWidth)
            drawLine(roadBorder, Offset(0f, y + roadWidth), Offset(size.width, y + roadWidth), strokeWidth = borderWidth)
        }
        listOf(size.width * 0.32f, size.width * 0.66f).forEach { x ->
            drawLine(roadBorder, Offset(x - borderWidth, 0f), Offset(x - borderWidth, size.height), strokeWidth = borderWidth)
            drawLine(roadColor, Offset(x, 0f), Offset(x, size.height), strokeWidth = roadWidth)
            drawLine(roadBorder, Offset(x + roadWidth, 0f), Offset(x + roadWidth, size.height), strokeWidth = borderWidth)
        }

        val blockColor = Color(0xFFD2D9C8)
        val blocks = listOf(
            Offset(0.04f, 0.04f) to Size(0.24f, 0.24f),
            Offset(0.04f, 0.36f) to Size(0.24f, 0.22f),
            Offset(0.04f, 0.66f) to Size(0.24f, 0.30f),
            Offset(0.36f, 0.04f) to Size(0.26f, 0.24f),
            Offset(0.36f, 0.36f) to Size(0.26f, 0.22f),
            Offset(0.36f, 0.66f) to Size(0.26f, 0.30f),
            Offset(0.70f, 0.04f) to Size(0.26f, 0.24f),
            Offset(0.70f, 0.36f) to Size(0.26f, 0.22f),
            Offset(0.70f, 0.66f) to Size(0.26f, 0.30f)
        )
        blocks.forEach { (pos, sz) ->
            drawRect(
                color = blockColor,
                topLeft = Offset(pos.x * size.width, pos.y * size.height),
                size = Size(sz.width * size.width, sz.height * size.height)
            )
        }

        val pinX = size.width * 0.5f
        val pinY = size.height * 0.49f
        drawCircle(color = Color(0xFF2196F3), radius = 14.dp.toPx(), center = Offset(pinX, pinY))
        drawCircle(color = Color.White, radius = 6.dp.toPx(), center = Offset(pinX, pinY))
    }
}

@Composable
private fun TrustCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFEEEEEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Contrato Digital Seguro",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A2E)
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Firma digital verificada y pago garantizado antes de comenzar.",
                    fontSize = 12.sp,
                    color = Color(0xFF5A6A7A),
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun JobDetailBottomBar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = {},
                modifier = Modifier.height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, DarkBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Guardar",
                    modifier = Modifier.size(20.dp)
                )
            }

            Button(
                onClick = {},
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Text(
                    text = "Postularse Ahora",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun CompanyJobBottomBar(onViewApplicants: () -> Unit, onEdit: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 12.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onViewApplicants,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.5.dp, DarkBlue),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkBlue)
            ) {
                Text("Ver Postulantes", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }
            Button(
                onClick = onEdit,
                modifier = Modifier.weight(1f).height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Editar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

private fun formatJobType(type: String): String = when (type.lowercase()) {
    "fixed-time" -> "Temporal · Fijo"
    "hourly" -> "Temporal · Por Horas"
    "part-time" -> "Medio Tiempo"
    "full-time" -> "Tiempo Completo"
    else -> "Temporal · ${type.replaceFirstChar { it.uppercase() }}"
}

private fun formatPaymentLabel(paymentType: String): String = when (paymentType.lowercase()) {
    "por hora" -> "PAGO POR HORA"
    "diario" -> "PAGO DIARIO"
    "fijo", "fixed" -> "PAGO FIJO"
    else -> "PAGO"
}

private fun formatPayment(payment: Double): String {
    val value = if (payment == payment.toLong().toDouble()) {
        "%,d".format(payment.toLong())
    } else {
        "%,.2f".format(payment)
    }
    return "₡$value"
}

private fun formatDate(dateStr: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd", Locale("es", "CR"))
        val output = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("es", "CR"))
        val date = input.parse(dateStr)
        date?.let {
            output.format(it).replaceFirstChar { c -> c.uppercase() }
        } ?: dateStr
    } catch (e: Exception) {
        dateStr
    }
}

private fun formatTime(timeStr: String): String {
    return try {
        val input = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val output = SimpleDateFormat("h:mm a", Locale.ENGLISH)
        val time = input.parse(timeStr)
        time?.let { output.format(it) } ?: timeStr
    } catch (e: Exception) {
        timeStr
    }
}

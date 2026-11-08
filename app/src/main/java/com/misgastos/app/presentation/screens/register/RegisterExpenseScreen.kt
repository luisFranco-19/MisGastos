package com.misgastos.app.presentation.screens.register

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misgastos.app.domain.model.PaymentMethod
import com.misgastos.app.presentation.components.AppDuration
import com.misgastos.app.presentation.components.AppEasing
import com.misgastos.app.presentation.components.getCategoryEmoji
import com.misgastos.app.presentation.components.getCategoryVectorIcon
import com.misgastos.app.presentation.components.pressScale
import com.misgastos.app.presentation.theme.Spacing
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun RegisterExpenseScreen(
    onNavigateBack: () -> Unit,
    expenseId: Long? = null,
    viewModel: RegisterExpenseViewModel = viewModel(factory = RegisterExpenseViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(expenseId) {
        if (expenseId != null && expenseId > 0) {
            viewModel.loadExpenseForEditing(expenseId)
        } else {
            // Cada nuevo gasto comienza 100% limpio
            viewModel.resetForm()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    // Pantalla de confirmación de éxito
    if (uiState.showConfirmation) {
        ConfirmationScreen(
            amount = uiState.savedAmount,
            paymentMethod = uiState.savedPaymentMethod,
            category = uiState.savedCategory,
            categoryIcon = uiState.savedCategoryIcon,
            date = uiState.savedDate,
            currency = uiState.currency,
            onDone = onNavigateBack
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditing) "Editar gasto" else "Registrar gasto",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (uiState.currentStep > 1) viewModel.previousStep()
                        else onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Indicador de pasos discreto
            StepProgressIndicator(
                currentStep = uiState.currentStep,
                totalSteps = 4,
                labels = listOf("Monto", "Pago", "Categoría", "Detalles"),
                modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.md)
            )

            // Transiciones entre pasos rápidas y naturales (200ms EaseOut)
            AnimatedContent(
                targetState = uiState.currentStep,
                transitionSpec = {
                    val direction = if (targetState > initialState) 1 else -1
                    slideInHorizontally(
                        initialOffsetX = { it / 3 * direction },
                        animationSpec = tween(AppDuration.TRANSITION, easing = AppEasing.EaseOut)
                    ) + fadeIn(
                        animationSpec = tween(AppDuration.TRANSITION, easing = AppEasing.EaseOut)
                    ) togetherWith slideOutHorizontally(
                        targetOffsetX = { -it / 3 * direction },
                        animationSpec = tween(AppDuration.QUICK, easing = AppEasing.EaseOut)
                    ) + fadeOut(
                        animationSpec = tween(AppDuration.QUICK, easing = AppEasing.EaseOut)
                    )
                },
                label = "step_transition"
            ) { step ->
                when (step) {
                    1 -> Step1Amount(
                        amount = uiState.amount,
                        amountError = uiState.amountError,
                        currency = uiState.currency,
                        onAmountChange = viewModel::onAmountChange,
                        onNext = viewModel::nextStep,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.xl)
                    )
                    2 -> Step2PaymentMethod(
                        selectedMethod = uiState.selectedPaymentMethod,
                        paymentMethodError = uiState.paymentMethodError,
                        onMethodSelected = viewModel::onPaymentMethodChange,
                        onNext = viewModel::nextStep,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.xl)
                    )
                    3 -> Step3Category(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        categoryError = uiState.categoryError,
                        onCategorySelected = viewModel::onCategoryChange,
                        onNext = viewModel::nextStep,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.xl)
                    )
                    4 -> Step4Details(
                        description = uiState.description,
                        date = uiState.date,
                        isSaving = uiState.isSaving,
                        isEditing = uiState.isEditing,
                        onDescriptionChange = viewModel::onDescriptionChange,
                        onDateChange = viewModel::onDateChange,
                        onSave = viewModel::nextStep,
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(Spacing.xl)
                    )
                }
            }
        }
    }
}

// ─── Step Progress Indicator ────────────────────────────────────────────────

@Composable
private fun StepProgressIndicator(
    currentStep: Int,
    totalSteps: Int,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(totalSteps) { index ->
                val step = index + 1
                val isCompleted = step < currentStep
                val isCurrent = step == currentStep

                val scale by animateFloatAsState(
                    targetValue = if (isCurrent) 1f else 0.9f,
                    animationSpec = tween(AppDuration.QUICK, easing = AppEasing.EaseOut),
                    label = "step_scale_$step"
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .graphicsLayer { scaleX = scale; scaleY = scale }
                        .clip(CircleShape)
                        .background(
                            when {
                                isCompleted -> MaterialTheme.colorScheme.primary
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                        .then(
                            if (isCurrent) Modifier.border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            ) else Modifier
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "$step",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isCurrent -> MaterialTheme.colorScheme.primary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                if (index < totalSteps - 1) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.5.dp)
                            .padding(horizontal = 4.dp)
                            .background(
                                if (step < currentStep) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                            )
                    )
                }
            }
        }
        Spacer(Modifier.height(Spacing.xs))
        Row(modifier = Modifier.fillMaxWidth()) {
            labels.forEachIndexed { index, label ->
                val step = index + 1
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (step == currentStep)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = if (step == currentStep) FontWeight.SemiBold else FontWeight.Normal,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// ─── Step 1: Amount ─────────────────────────────────────────────────────────

@Composable
private fun Step1Amount(
    amount: String,
    amountError: String?,
    currency: String,
    onAmountChange: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                text = amount,
                selection = TextRange(amount.length)
            )
        )
    }

    LaunchedEffect(amount) {
        if (textFieldValue.text != amount) {
            textFieldValue = TextFieldValue(
                text = amount,
                selection = TextRange(amount.length)
            )
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Spacer(Modifier.height(Spacing.lg))

        Text(
            text = "¿Cuánto gastaste?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Ingresa el monto del gasto",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Spacing.sm))

        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                val filteredText = newValue.text.filter { it.isDigit() || it == '.' }
                val newSelection = if (filteredText.length != newValue.text.length) {
                    TextRange(filteredText.length)
                } else {
                    newValue.selection
                }
                textFieldValue = newValue.copy(text = filteredText, selection = newSelection)
                onAmountChange(filteredText)
            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            placeholder = {
                Text("0.00", style = MaterialTheme.typography.headlineMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ))
            },
            prefix = {
                Text(
                    "$currency ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            textStyle = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Decimal,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { onNext() }),
            isError = amountError != null,
            supportingText = amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            singleLine = true,
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pressScale(onClick = onNext),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Continuar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Step 2: Payment Method ─────────────────────────────────────────────────

@Composable
private fun Step2PaymentMethod(
    selectedMethod: PaymentMethod?,
    paymentMethodError: String?,
    onMethodSelected: (PaymentMethod) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "¿Con qué pagaste?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Selecciona tu método de pago",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (paymentMethodError != null) {
            Text(
                text = paymentMethodError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(Spacing.xs))

        PaymentMethod.entries.forEach { method ->
            val isSelected = selectedMethod == method

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { onMethodSelected(method) },
                color = if (isSelected)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = method.icon, fontSize = 22.sp)
                    Spacer(Modifier.width(Spacing.md))
                    Text(
                        text = method.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (isSelected) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pressScale(onClick = onNext),
            enabled = selectedMethod != null,
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Continuar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Step 3: Category ───────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Step3Category(
    categories: List<com.misgastos.app.domain.model.Category>,
    selectedCategory: String,
    categoryError: String?,
    onCategorySelected: (String, String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "¿En qué gastaste?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Selecciona una categoría",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (categoryError != null) {
            Text(
                text = categoryError,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            categories.forEach { category ->
                val isSelected = selectedCategory == category.name

                Surface(
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { onCategorySelected(category.name, category.icon) },
                    color = if (isSelected)
                        MaterialTheme.colorScheme.secondaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getCategoryEmoji(category.name, category.icon),
                            fontSize = 26.sp
                        )
                        Spacer(Modifier.height(Spacing.xs))
                        Text(
                            text = category.name,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected)
                                MaterialTheme.colorScheme.onSecondaryContainer
                            else
                                MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pressScale(onClick = onNext),
            enabled = selectedCategory.isNotBlank(),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Continuar", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ─── Step 4: Details ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step4Details(
    description: String,
    date: LocalDate,
    isSaving: Boolean,
    isEditing: Boolean,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Spacer(Modifier.height(Spacing.sm))

        Text(
            text = "Detalles del gasto",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "Agrega una descripción y confirma la fecha",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(Spacing.sm))

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Descripción (opcional)") },
            placeholder = { Text("Ej: Almuerzo en el trabajo") },
            singleLine = true,
            maxLines = 1,
            shape = MaterialTheme.shapes.medium
        )

        OutlinedTextField(
            value = date.format(dateFormatter),
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showDatePicker = true },
            label = { Text("Fecha") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Seleccionar fecha")
                }
            },
            shape = MaterialTheme.shapes.medium
        )

        Spacer(Modifier.weight(1f))

        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pressScale(onClick = onSave),
            enabled = !isSaving,
            shape = MaterialTheme.shapes.medium,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = when {
                    isSaving -> "Guardando..."
                    isEditing -> "Actualizar gasto"
                    else -> "Guardar gasto"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.height(Spacing.sm))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = date
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateChange(selectedDate)
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

// ─── Confirmation Screen ────────────────────────────────────────────────────

@Composable
private fun ConfirmationScreen(
    amount: Double,
    paymentMethod: PaymentMethod?,
    category: String,
    categoryIcon: String,
    date: LocalDate,
    currency: String,
    onDone: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es"))

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut),
        label = "confirm_alpha"
    )
    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(AppDuration.MODAL, easing = AppEasing.EaseOut),
        label = "confirm_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Spacing.xxl)
            .graphicsLayer {
                this.alpha = alpha
                scaleX = scale
                scaleY = scale
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(Modifier.height(Spacing.xl))

        Text(
            text = "Gasto guardado",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(Modifier.height(Spacing.xl))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .padding(Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            Text(
                text = "$currency ${String.format("%,.2f", amount)}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            val paymentText = paymentMethod?.displayName ?: ""
            val categoryText = "${getCategoryEmoji(category, categoryIcon)} $category"

            Text(
                text = if (paymentText.isNotBlank()) "$paymentText · $categoryText" else categoryText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = date.format(dateFormatter),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(Modifier.height(Spacing.xxl))

        Button(
            onClick = onDone,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .pressScale(onClick = onDone),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("Listo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
    }
}

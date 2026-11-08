package com.misgastos.app.data.local

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.misgastos.app.domain.model.AppCurrency
import com.misgastos.app.presentation.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mis_gastos_preferences")

class PreferencesManager(private val context: Context) {

    companion object {
        private val THEME_MODE = stringPreferencesKey("theme_mode")
        private val CURRENCY = stringPreferencesKey("currency")
        private val CURRENCY_CODE = stringPreferencesKey("currency_code")
        private val INITIAL_CURRENCY_SET = booleanPreferencesKey("initial_currency_set")
        private val SPREADSHEET_ID = stringPreferencesKey("spreadsheet_id")
        private val SHEET_NAME = stringPreferencesKey("sheet_name")
        private val BUDGET_ALERTS = booleanPreferencesKey("budget_alerts")
        private val LAST_PAYMENT_METHOD = stringPreferencesKey("last_payment_method")
        private val LAST_CATEGORY = stringPreferencesKey("last_category")
        private val DAILY_REMINDER_ENABLED = booleanPreferencesKey("daily_reminder_enabled")
        private val LAST_NOTIFICATION_TIMESTAMP = longPreferencesKey("last_notification_timestamp")
        private val LAST_NOTIFICATION_DATE = stringPreferencesKey("last_notification_date")
        private val DAILY_NOTIFICATION_COUNT = intPreferencesKey("daily_notification_count")
        private val LAST_REMINDER_INDEX = intPreferencesKey("last_reminder_index")
        private val LAST_SUMMARY_INDEX = intPreferencesKey("last_summary_index")
    }

    val themeMode: Flow<ThemeMode> = context.dataStore.data.map { prefs ->
        when (prefs[THEME_MODE]) {
            "LIGHT" -> ThemeMode.LIGHT
            "DARK" -> ThemeMode.DARK
            else -> ThemeMode.SYSTEM
        }
    }

    val currency: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENCY] ?: AppCurrency.DEFAULT.symbol
    }

    val currencyCode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[CURRENCY_CODE] ?: AppCurrency.DEFAULT.code
    }

    val initialCurrencySet: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[INITIAL_CURRENCY_SET] ?: false
    }

    val spreadsheetId: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SPREADSHEET_ID]
    }

    val sheetName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SHEET_NAME] ?: "Gastos"
    }

    val budgetAlerts: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[BUDGET_ALERTS] ?: true
    }

    val lastPaymentMethod: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[LAST_PAYMENT_METHOD]
    }

    val lastCategory: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[LAST_CATEGORY]
    }

    val dailyReminderEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[DAILY_REMINDER_ENABLED] ?: true
    }

    val lastNotificationTimestamp: Flow<Long> = context.dataStore.data.map { prefs ->
        prefs[LAST_NOTIFICATION_TIMESTAMP] ?: 0L
    }

    val lastNotificationDate: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[LAST_NOTIFICATION_DATE]
    }

    val dailyNotificationCount: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[DAILY_NOTIFICATION_COUNT] ?: 0
    }

    val lastReminderIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[LAST_REMINDER_INDEX] ?: -1
    }

    val lastSummaryIndex: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[LAST_SUMMARY_INDEX] ?: -1
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode.name
        }
        applyNightMode(mode)
    }

    fun applyNightMode(mode: ThemeMode) {
        val nightMode = when (mode) {
            ThemeMode.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            ThemeMode.DARK -> AppCompatDelegate.MODE_NIGHT_YES
            ThemeMode.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        AppCompatDelegate.setDefaultNightMode(nightMode)
    }

    suspend fun setCurrency(currencySymbol: String) {
        context.dataStore.edit { prefs ->
            prefs[CURRENCY] = currencySymbol
            val appCurrency = AppCurrency.fromSymbol(currencySymbol)
            prefs[CURRENCY_CODE] = appCurrency.code
        }
    }

    suspend fun setAppCurrency(appCurrency: AppCurrency) {
        context.dataStore.edit { prefs ->
            prefs[CURRENCY] = appCurrency.symbol
            prefs[CURRENCY_CODE] = appCurrency.code
            prefs[INITIAL_CURRENCY_SET] = true
        }
    }

    suspend fun setInitialCurrencySet(isSet: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[INITIAL_CURRENCY_SET] = isSet
        }
    }

    suspend fun setSpreadsheetId(id: String) {
        context.dataStore.edit { prefs ->
            prefs[SPREADSHEET_ID] = id
        }
    }

    suspend fun setSheetName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[SHEET_NAME] = name
        }
    }

    suspend fun setBudgetAlerts(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[BUDGET_ALERTS] = enabled
        }
    }

    suspend fun setLastPaymentMethod(method: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_PAYMENT_METHOD] = method
        }
    }

    suspend fun setLastCategory(category: String) {
        context.dataStore.edit { prefs ->
            prefs[LAST_CATEGORY] = category
        }
    }

    suspend fun clearSpreadsheetId() {
        context.dataStore.edit { prefs ->
            prefs.remove(SPREADSHEET_ID)
        }
    }

    suspend fun setDailyReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[DAILY_REMINDER_ENABLED] = enabled
        }
    }

    suspend fun recordNotificationSent(
        timestamp: Long,
        date: String,
        count: Int,
        reminderIndex: Int? = null,
        summaryIndex: Int? = null
    ) {
        context.dataStore.edit { prefs ->
            prefs[LAST_NOTIFICATION_TIMESTAMP] = timestamp
            prefs[LAST_NOTIFICATION_DATE] = date
            prefs[DAILY_NOTIFICATION_COUNT] = count
            reminderIndex?.let { prefs[LAST_REMINDER_INDEX] = it }
            summaryIndex?.let { prefs[LAST_SUMMARY_INDEX] = it }
        }
    }
}

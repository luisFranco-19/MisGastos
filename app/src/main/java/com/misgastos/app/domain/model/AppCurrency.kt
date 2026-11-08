package com.misgastos.app.domain.model

/**
 * Representa las monedas soportadas en Mis Gastos:
 * 1. 🇳🇮 NIO — Córdoba nicaragüense (C$)
 * 2. 🇨🇷 CRC — Colón costarricense (₡)
 */
enum class AppCurrency(
    val code: String,
    val symbol: String,
    val countryName: String,
    val currencyName: String,
    val flag: String
) {
    NIO(
        code = "NIO",
        symbol = "C$",
        countryName = "Nicaragua",
        currencyName = "Córdoba nicaragüense",
        flag = "🇳🇮"
    ),
    CRC(
        code = "CRC",
        symbol = "₡",
        countryName = "Costa Rica",
        currencyName = "Colón costarricense",
        flag = "🇨🇷"
    );

    val displayName: String
        get() = "$flag $currencyName ($symbol)"

    val fullDescription: String
        get() = "$flag $code — $currencyName ($symbol)"

    companion object {
        val DEFAULT = NIO

        fun fromCode(code: String?): AppCurrency {
            if (code.isNullOrBlank()) return DEFAULT
            return entries.find {
                it.code.equals(code, ignoreCase = true) || it.symbol == code
            } ?: DEFAULT
        }

        fun fromSymbol(symbol: String?): AppCurrency {
            if (symbol.isNullOrBlank()) return DEFAULT
            return entries.find {
                it.symbol == symbol || it.code.equals(symbol, ignoreCase = true)
            } ?: DEFAULT
        }
    }
}

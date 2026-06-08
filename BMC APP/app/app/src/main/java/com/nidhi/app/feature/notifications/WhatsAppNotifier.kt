package com.nidhi.app.feature.notifications

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast

/**
 * Sends a notification message via WhatsApp.
 *
 * Two modes:
 *  1. Direct message to a specific phone number (requires number)
 *  2. Share sheet fallback if WhatsApp is not installed or number is unknown
 */
object WhatsAppNotifier {

    private const val WHATSAPP_PACKAGE        = "com.whatsapp"
    private const val WHATSAPP_BUSINESS_PKG   = "com.whatsapp.w4b"

    /**
     * Sends [message] to [phoneNumber] (international format, no spaces, e.g. "+919876543210").
     * If [phoneNumber] is null or blank, opens WhatsApp share sheet.
     */
    fun send(
        context: Context,
        message: String,
        phoneNumber: String? = null
    ) {
        val trimmedPhone = phoneNumber?.replace(Regex("[^+0-9]"), "")

        if (!trimmedPhone.isNullOrBlank()) {
            // Direct message to a specific number
            sendDirect(context, trimmedPhone, message)
        } else {
            // Generic share to WhatsApp
            shareToWhatsApp(context, message)
        }
    }

    private fun sendDirect(context: Context, phone: String, message: String) {
        // Use wa.me deep-link — works for both WhatsApp and WhatsApp Business
        val encoded = Uri.encode(message)
        val uri = Uri.parse("https://wa.me/$phone?text=$encoded")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        if (isWhatsAppInstalled(context)) {
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
                fallbackShare(context, message)
            }
        } else {
            Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            fallbackShare(context, message)
        }
    }

    private fun shareToWhatsApp(context: Context, message: String) {
        if (!isWhatsAppInstalled(context)) {
            Toast.makeText(context, "WhatsApp is not installed", Toast.LENGTH_SHORT).show()
            fallbackShare(context, message)
            return
        }

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            setPackage(WHATSAPP_PACKAGE)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        try {
            context.startActivity(intent)
        } catch (_: Exception) {
            // Try WhatsApp Business
            try {
                context.startActivity(intent.apply { setPackage(WHATSAPP_BUSINESS_PKG) })
            } catch (_: Exception) {
                fallbackShare(context, message)
            }
        }
    }

    private fun fallbackShare(context: Context, message: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(Intent.createChooser(intent, "Share via").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun isWhatsAppInstalled(context: Context): Boolean {
        val pm = context.packageManager
        return try {
            pm.getPackageInfo(WHATSAPP_PACKAGE, 0)
            true
        } catch (_: PackageManager.NameNotFoundException) {
            try {
                pm.getPackageInfo(WHATSAPP_BUSINESS_PKG, 0)
                true
            } catch (_: PackageManager.NameNotFoundException) {
                false
            }
        }
    }

    /**
     * Builds a formatted notification message for document expiry.
     */
    fun buildExpiryMessage(
        documentTitle: String,
        daysLeft: Long,
        userName: String = "there"
    ): String = """
        🔔 *NIDHI Alert* — Document Expiring Soon
        
        Hi $userName!
        
        📄 *$documentTitle* expires in *$daysLeft day${if (daysLeft == 1L) "" else "s"}*.
        
        Please renew it to avoid disruptions.
        
        — NIDHI, Your Family's Financial Guardian
    """.trimIndent()

    /**
     * Builds a formatted message for a benefit opportunity.
     */
    fun buildBenefitMessage(
        benefitName: String,
        userName: String = "there"
    ): String = """
        🎯 *NIDHI — New Benefit Found*
        
        Hi $userName!
        
        You may be eligible for *$benefitName*.
        
        Open NIDHI to check eligibility and apply.
        
        — NIDHI, Your Family's Financial Guardian
    """.trimIndent()
}

package uz.tayanch.app.ui.home

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import androidx.core.content.FileProvider
import uz.tayanch.app.R
import java.io.File
import java.io.FileOutputStream

/**
 * The "assignment bridge". Coding is done on a PC, so we generate the task as a
 * real PDF (via Android's PdfDocument), expose it through a secure FileProvider
 * URI (Pillar 5 — temporary read-only grant), and hand it to ACTION_SEND so the
 * student can fire it to Telegram/email and open it on their laptop.
 */
object AssignmentShare {

    fun createPdf(context: Context, title: String): Uri {
        val doc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 @ 72dpi
        val page = doc.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply { textSize = 20f; isFakeBoldText = true }
        val bodyPaint = Paint().apply { textSize = 13f }

        var y = 70f
        canvas.drawText(context.getString(R.string.pdf_title), 40f, y, titlePaint)
        y += 34f
        canvas.drawText(title, 40f, y, bodyPaint)
        y += 30f

        val lines = context.getString(R.string.pdf_body).split("\n")
        for (line in lines) {
            canvas.drawText(line, 40f, y, bodyPaint)
            y += 20f
        }

        doc.finishPage(page)

        val dir = File(context.cacheDir, "assignments").apply { mkdirs() }
        val file = File(dir, "tayanch_task.pdf")
        FileOutputStream(file).use { doc.writeTo(it) }
        doc.close()

        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    fun chooser(context: Context, uri: Uri): Intent {
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Intent.createChooser(send, context.getString(R.string.pdf_chooser))
    }
}

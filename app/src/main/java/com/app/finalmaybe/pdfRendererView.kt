package com.app.finalmaybe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.View
import java.io.File

class pdfRendererView: View {
    private var renderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null
    private val paint = Paint()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun openPdf(file: File) {
        closePdf()
        renderer = PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY))
        currentPage = renderer?.openPage(0)
        invalidate()
    }

    fun closePdf() {
        currentPage?.close()
        currentPage = null
        renderer?.close()
        renderer = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        currentPage?.let { page ->
            val scaleX = width.toFloat() / page.width
            val scaleY = height.toFloat() / page.height
            val scale = Math.min(scaleX, scaleY)
            val offsetX = (width - page.width * scale) / 2
            val offsetY = (height - page.height * scale) / 2 + height.toFloat()/6

            val bitmap = Bitmap.createBitmap(
                (page.width * scale).toInt(),
                (page.height* scale).toInt(),
                Bitmap.Config.ARGB_8888
            )
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            canvas.drawBitmap(bitmap, offsetX, offsetY, paint)
            bitmap.recycle()
        }
    }
}
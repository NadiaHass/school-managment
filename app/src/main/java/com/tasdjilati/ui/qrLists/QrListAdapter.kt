package com.tasdjilati.ui.qrLists

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.recyclerview.widget.RecyclerView
import com.tasdjilati.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class QrListAdapter  (
    private val qrsLists: MutableList<MutableList<String>> ,
    private val activity : Activity
)
    : RecyclerView.Adapter<QrListAdapter.ItemViewHolder>() {

    private lateinit var bitmap: Bitmap
    private lateinit var qrEncoder: QRGEncoder

    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvName: TextView = view.findViewById(R.id.tv_fileName)
        var exportImage : ImageView = view.findViewById(R.id.iv_export_file)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.qr_list_rv_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.tvName.text = "List Pdf $position"

        holder.exportImage.setOnClickListener {

            val list : ArrayList<String> = ArrayList()
            list.addAll(qrsLists[position])

            CoroutineScope(Dispatchers.IO).launch{
                generateQrCodesPdf(list , "list$position")
            }

        }

    }

    override fun getItemCount()= qrsLists.size

    suspend fun generateQrCodesPdf(studentsList: ArrayList<String>?, fileName : String) =
        withContext(Dispatchers.IO) {
            withContext(Dispatchers.Main){
                Toast.makeText(activity, "Attendez ne quitter pas l'application", Toast.LENGTH_LONG).show()
            }
            var pdfDocument = PdfDocument()
            var i = 0
            studentsList!!.forEach { student ->
                i++
                generateQrCode(student)
                val pi = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, i).create()
                val page = pdfDocument.startPage(pi)
                val paint = Paint()
                val x = 5F
                val y = 5F
                val canvas: Canvas = page.canvas
                canvas.drawBitmap(bitmap, x, y, paint)
                pdfDocument.finishPage(page)
            }
            withContext(Dispatchers.Main){
                Toast.makeText(activity, "Vous treverez le fichier dans vos telechargements", Toast.LENGTH_LONG).show()
            }
            val myFilePath =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/$fileName.pdf").path
            val myFile = File(myFilePath)
            pdfDocument.writeTo(FileOutputStream(myFile))

            pdfDocument.close()
        }

    private fun generateQrCode(id: String?) {
        val windowManager: WindowManager = activity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display: Display = windowManager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width = point.x
        val height = point.y
        var dimen = if (width < height) width else height
        dimen = dimen * 3 / 4
        qrEncoder = QRGEncoder(id , null, QRGContents.Type.TEXT, dimen)
        try {
            bitmap = qrEncoder.encodeAsBitmap()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}

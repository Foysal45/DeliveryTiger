package com.bd.deliverytiger.app.ui.payment_statement.excel_generator

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.bd.deliverytiger.app.R
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.utils.FileUtils
import org.apache.poi.hssf.usermodel.HSSFFont
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.IndexedColors
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream

class ExcelGenerator(private val context: Context) {

    private var filePath: String = ""

    fun writeExcel(model: PaymentDetailsResponse?): String {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet("TransactionNo_${model?.transactionNo}")
        val font1 = workBook.createFont().apply {
            fontHeightInPoints = 12
            boldweight = HSSFFont.BOLDWEIGHT_BOLD
            color = IndexedColors.BLACK.getIndex()
            fontName = HSSFFont.FONT_ARIAL
        }
        val font2 = workBook.createFont().apply {
            fontHeightInPoints = 12
            color = IndexedColors.BLACK.getIndex()
            fontName = HSSFFont.FONT_ARIAL
        }
        val cs = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
            setFont(font1)
        }
        val cs1 = workBook.createCellStyle().apply {
            alignment = CellStyle.ALIGN_CENTER
            setFont(font2)
        }

        val row0 = sheet.createRow(0)
        with(row0) {
            createCell(0).apply {
                setCellValue("TransactionNo")
                setCellStyle(cs)
            }
            createCell(1).apply {
                setCellValue("ModeOfPayment")
                setCellStyle(cs)
            }
            createCell(2).apply {
                setCellValue("TotalOrderCount")
                setCellStyle(cs)
            }
            createCell(3).apply {
                setCellValue("NetCollectedAmount")
                setCellStyle(cs)
            }
            createCell(4).apply {
                setCellValue("NetDeliveryCharge")
                setCellStyle(cs)
            }
            createCell(5).apply {
                setCellValue("NetCODCharge")
                setCellStyle(cs)
            }
            createCell(6).apply {
                setCellValue("NetBreakableCharge")
                setCellStyle(cs)
            }
            createCell(7).apply {
                setCellValue("NetCollectionCharge")
                setCellStyle(cs)
            }
            createCell(8).apply {
                setCellValue("NetPackagingCharge")
                setCellStyle(cs)
            }
            createCell(9).apply {
                setCellValue("NetReturnCharge")
                setCellStyle(cs)
            }
            createCell(10).apply {
                setCellValue("NetTotalCharge")
                setCellStyle(cs)
            }
            createCell(11).apply {
                setCellValue("NetAdjustedAmount")
                setCellStyle(cs)
            }
            createCell(12).apply {
                setCellValue("NetPaidAmount")
                setCellStyle(cs)
            }
        }

        val row1 = sheet.createRow(1)
        with(row1) {
            createCell(0).apply {
                setCellValue(model?.transactionNo)
                setCellStyle(cs1)
            }
            createCell(1).apply {
                setCellValue(model?.modeOfPayment)
                setCellStyle(cs1)
            }
            createCell(2).apply {
                setCellValue(model?.totalOrderCount.toString())
                setCellStyle(cs1)
            }
            createCell(3).apply {
                setCellValue(model?.netCollectedAmount.toString())
                setCellStyle(cs1)
            }
            createCell(4).apply {
                setCellValue(model?.netDeliveryCharge.toString())
                setCellStyle(cs1)
            }
            createCell(5).apply {
                setCellValue(model?.netCODCharge.toString())
                setCellStyle(cs1)
            }
            createCell(6).apply {
                setCellValue(model?.netBreakableCharge.toString())
                setCellStyle(cs1)
            }
            createCell(7).apply {
                setCellValue(model?.netCollectionCharge.toString())
                setCellStyle(cs1)
            }
            createCell(8).apply {
                setCellValue(model?.netPackagingCharge.toString())
                setCellStyle(cs1)
            }
            createCell(9).apply {
                setCellValue(model?.netReturnCharge.toString())
                setCellStyle(cs1)
            }
            createCell(10).apply {
                setCellValue(model?.netTotalCharge.toString())
                setCellStyle(cs1)
            }
            createCell(11).apply {
                setCellValue(model?.netAdjustedAmount.toString())
                setCellStyle(cs1)
            }
            createCell(12).apply {
                setCellValue(model?.netPaidAmount.toString())
                setCellStyle(cs1)
            }
        }

        val row3 = sheet.createRow(3)
        with(row3) {
            createCell(0).apply {
                setCellValue("OrderCode")
                setCellStyle(cs)
            }
            createCell(1).apply {
                setCellValue("Paid/Adjusted")
                setCellStyle(cs)
            }
            createCell(2).apply {
                setCellValue("CollectedAmount")
                setCellStyle(cs)
            }
            createCell(3).apply {
                setCellValue("DeliveryCharge")
                setCellStyle(cs)
            }
            createCell(4).apply {
                setCellValue("CODCharge")
                setCellStyle(cs)
            }
            createCell(5).apply {
                setCellValue("BreakableCharge")
                setCellStyle(cs)
            }
            createCell(6).apply {
                setCellValue("CollectionCharge")
                setCellStyle(cs)
            }
            createCell(7).apply {
                setCellValue("PackagingCharge")
                setCellStyle(cs)
            }
            createCell(8).apply {
                setCellValue("ReturnCharge")
                setCellStyle(cs)
            }
            createCell(9).apply {
                setCellValue("TotalCharge")
                setCellStyle(cs)
            }
            createCell(10).apply {
                setCellValue("PaidAmount")
                setCellStyle(cs)
            }
        }

        model?.orderList?.forEachIndexed { index, data ->

            val row = sheet.createRow(4 + index)
            with(row) {
                createCell(0).apply {
                    setCellValue(data.orderCode)
                    setCellStyle(cs1)
                }
                createCell(1).apply {
                    if (data.type == "CR") {
                        setCellValue("Paid")
                    } else {
                        setCellValue("Adjusted")
                    }
                    setCellStyle(cs1)
                }
                createCell(2).apply {
                    setCellValue(data.collectedAmount.toString())
                    setCellStyle(cs1)
                }
                createCell(3).apply {
                    setCellValue(data.deliveryCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(4).apply {
                    setCellValue(data.CODCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(5).apply {
                    setCellValue(data.breakableCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(6).apply {
                    setCellValue(data.collectionCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(7).apply {
                    setCellValue(data.packagingCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(8).apply {
                    setCellValue(data.returnCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(9).apply {
                    setCellValue(data.totalCharge.toString())
                    setCellStyle(cs1)
                }
                createCell(10).apply {
                    setCellValue(data.amount.toString())
                    setCellStyle(cs1)
                }
            }
        }

        /*try {
            val outputStream = FileOutputStream(createNewFile(model?.transactionNo ?: "statement"))
            workBook.write(outputStream)
            outputStream.close()
            context?.toast("আপনি সফলভাবে এক্সেল শীটে সেভ করেছেন\n$filePath", Toast.LENGTH_LONG)
        } catch (e: Exception) {
            Timber.d(e)
        }*/

        var filePath = ""
        val fileName = "${model?.transactionNo}"
        val outputStream = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getString(R.string.app_name))
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, fileName)
            filePath = file.absolutePath
            FileOutputStream(file)
        } else {
            val resolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.ms-excel")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/" + context.getString(R.string.app_name))
            }
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)?.let { uri ->
                Timber.d("writeExcel Uri $uri")
                filePath = FileUtils(context).getPath(uri)
                Timber.d("writeExcel filePath $filePath")
                //val fileUri = FileProvider.getUriForFile(context, context.getString(R.string.file_provider_authority), File(uri.path ?: ""))
                //Timber.d("writeExcel fileUri $fileUri")
                resolver.openOutputStream(uri)
            }
        }
        outputStream?.use { stream ->
            workBook.write(stream)
        }
        Timber.d("writeExcel $filePath")
        //context.toast("আপনি সফলভাবে এক্সেল শীটে সেভ করেছেন\n$filePath", Toast.LENGTH_LONG)

        return filePath

    }

    /*private fun createNewFile(name: String): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd", Locale.US).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            File.createTempFile("Statement_${name}_${timeStamp}_", ".xls", storageDir).apply {
                filePath = absolutePath
                Timber.d("ExcelGenerator TempFilePath: $filePath")
            }
        } catch (e: Exception) {
            Timber.d(e)
            null
        }
    }*/

}
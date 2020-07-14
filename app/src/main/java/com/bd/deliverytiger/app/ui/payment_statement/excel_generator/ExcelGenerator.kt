package com.bd.deliverytiger.app.ui.payment_statement.excel_generator

import android.content.Context
import android.os.Environment
import com.bd.deliverytiger.app.api.model.payment_statement.PaymentDetailsResponse
import com.bd.deliverytiger.app.utils.toast
import org.apache.poi.hssf.record.cf.PatternFormatting.SOLID_FOREGROUND
import org.apache.poi.hssf.usermodel.HSSFCellStyle
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.hssf.util.HSSFColor
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.PatternFormatting.SOLID_FOREGROUND
import org.apache.poi.xssf.usermodel.XSSFColor
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.openxmlformats.schemas.drawingml.x2006.main.STPresetColorVal.LIME
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ExcelGenerator(private val context: Context) {

    private var filePath: String = ""

    fun writeExcel(model: PaymentDetailsResponse?) {
        val workBook = HSSFWorkbook()
        val sheet = workBook.createSheet("statement")
        val cs = workBook.createCellStyle().apply {
            alignment = HorizontalAlignment.CENTER
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
                setCellStyle(cs)
            }
            createCell(1).apply {
                setCellValue(model?.modeOfPayment)
                setCellStyle(cs)
            }
            createCell(2).apply {
                setCellValue(model?.totalOrderCount.toString())
                setCellStyle(cs)
            }
            createCell(3).apply {
                setCellValue(model?.netCollectedAmount.toString())
                setCellStyle(cs)
            }
            createCell(4).apply {
                setCellValue(model?.netDeliveryCharge.toString())
                setCellStyle(cs)
            }
            createCell(5).apply {
                setCellValue(model?.netCODCharge.toString())
                setCellStyle(cs)
            }
            createCell(6).apply {
                setCellValue(model?.netBreakableCharge.toString())
                setCellStyle(cs)
            }
            createCell(7).apply {
                setCellValue(model?.netCollectionCharge.toString())
                setCellStyle(cs)
            }
            createCell(8).apply {
                setCellValue(model?.netPackagingCharge.toString())
                setCellStyle(cs)
            }
            createCell(9).apply {
                setCellValue(model?.netReturnCharge.toString())
                setCellStyle(cs)
            }
            createCell(10).apply {
                setCellValue(model?.netTotalCharge.toString())
                setCellStyle(cs)
            }
            createCell(11).apply {
                setCellValue(model?.netAdjustedAmount.toString())
                setCellStyle(cs)
            }
            createCell(12).apply {
                setCellValue(model?.netPaidAmount.toString())
                setCellStyle(cs)
            }
        }

        try {
            val outputStream = FileOutputStream(createNewFile(model?.transactionNo ?: "statement"))
            workBook.write(outputStream)
            outputStream.close()
            workBook.close()

            context?.toast("আপনি সফলভাবে এক্সেল শীটে সেভ করেছেন")
        } catch (e: Exception) {
            Timber.d(e)
            workBook.close()
        }

    }

    private fun createNewFile(name: String): File? {
        return try {
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            File.createTempFile("${name}_${timeStamp}", ".xlsx", storageDir).apply {
                filePath = absolutePath
                Timber.d("ExcelGenerator TempFilePath: $filePath")
            }
        } catch (e: Exception) {
            Timber.d(e)
            null
        }
    }

}
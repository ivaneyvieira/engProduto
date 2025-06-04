package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.util.SystemUtils.readStream
import br.com.astrosoft.produto.model.nfeXml.IItensNotaReport
import net.sf.jasperreports.engine.JasperCompileManager
import net.sf.jasperreports.engine.JasperFillManager
import net.sf.jasperreports.engine.JasperPrint
import net.sf.jasperreports.engine.JasperReport
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

object DanfeReport {
  private fun jasperReport() = compileReport()

  fun create(listItens: List<List<IItensNotaReport>>, tipo: ETIPO_COPIA): ByteArray {
    val listPrintReport = listItens.map { itens ->
      fillReport(itens, tipo)
    }

    val exporter = JRPdfExporter()
    val baos = ByteArrayOutputStream()
    exporter.setExporterInput(SimpleExporterInput.getInstance(listPrintReport))
    exporter.exporterOutput = SimpleOutputStreamExporterOutput(baos)
    exporter.exportReport()

    return baos.toByteArray() //val printReport = fillReport(itens, tipo)
    //return JasperExportManager.exportReportToPdf(printReport) ?: ByteArray(0)
  }

  private fun fillReport(itens: List<IItensNotaReport>, tipo: ETIPO_COPIA): JasperPrint? {
    val parameter = hashMapOf<String, Any>()
    parameter["PRINT_MARCA"] = tipo.parametro
    val collection = JRBeanCollectionDataSource(itens)
    return JasperFillManager.fillReport(jasperReport(), parameter, collection)
  }

  private fun compileReport(): JasperReport {
    val jasperFile = "/projeto/notafiscal.jrxml"
    val jasperInputStream = readStream(jasperFile)
    return JasperCompileManager.compileReport(jasperInputStream)
  }
}

enum class ETIPO_COPIA(val parametro: String, val descricao: String) {
  COPIA("C", "Cópia"),
  SEGUNDA_VIA("2", "2ª Via"),
  REIMPRESSAO("R", "Reimpressão"),
  ESPELHO("E", "Espelho da NFD"),
}
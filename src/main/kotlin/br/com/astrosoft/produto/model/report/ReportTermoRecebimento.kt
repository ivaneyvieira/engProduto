package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.Templates
import br.com.astrosoft.framework.model.reports.horizontalBlock
import br.com.astrosoft.framework.model.reports.text
import br.com.astrosoft.framework.model.reports.verticalBlock
import br.com.astrosoft.produto.model.beans.TermoRecebimento
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.RIGHT
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

class ReportTermoRecebimento(val termo: TermoRecebimento) {
  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalBlock {
        it.text("Termo de Recebimento")
      }
    }
  }

  fun makeReport(): JasperReportBuilder? {
    val itens = listOf(termo)
    val pageOrientation = PORTRAIT
    return report()
      .title(titleBuiderPedido())
      .setTemplate(Templates.reportTemplate)
      .setColumnStyle(stl.style().setFontSize(7))
      .setDataSource(itens.toList())
      .setPageFormat(A4, pageOrientation)
      .setPageMargin(margin(28))
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(termo: TermoRecebimento): ByteArray? {
      val print = ReportTermoRecebimento(termo).makeReport()?.toJasperPrint() ?: return null
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(listOf(print)))

      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

      exporter.exportReport()
      return out.toByteArray()
    }
  }
}
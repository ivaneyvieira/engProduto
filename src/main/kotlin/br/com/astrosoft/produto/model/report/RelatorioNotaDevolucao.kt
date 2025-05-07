package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.Templates
import br.com.astrosoft.framework.model.reports.Templates.fieldFontGrande
import br.com.astrosoft.framework.model.reports.horizontalList
import br.com.astrosoft.framework.model.reports.text
import br.com.astrosoft.framework.model.reports.verticalBlock
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import br.com.astrosoft.produto.model.beans.NotaRecebimentoProdutoDev
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.column.ColumnBuilder
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.PageOrientation.LANDSCAPE
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.TextAdjust.CUT_TEXT
import net.sf.dynamicreports.report.constant.TextAdjust.SCALE_FONT
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

class RelatorioNotaDevolucao(val notaSaida: NotaRecebimentoDev, private val resumida: Boolean) {
  private val codigoCol: TextColumnBuilder<String> =
      col.column("Cód Saci", NotaRecebimentoProdutoDev::codigoStr.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(35)
      }
  private val dataInvCol: TextColumnBuilder<String> =
      col.column("Emissão", NotaRecebimentoProdutoDev::dateInvStr.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val notaInvCol: TextColumnBuilder<String> =
      col.column("NF", NotaRecebimentoProdutoDev::notaInv.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val ncmInvCol: TextColumnBuilder<String> =
      col.column("NCM", NotaRecebimentoProdutoDev::ncm.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val refForCol: TextColumnBuilder<String> =
      col.column("Ref do Fab", NotaRecebimentoProdutoDev::refFor.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(60)
      }
  private val descricaoCol: TextColumnBuilder<String> =
      col.column("Descrição", NotaRecebimentoProdutoDev::descricao.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(LEFT)
        this.setTextAdjust(CUT_TEXT)
      }
  private val gradeCol: TextColumnBuilder<String> =
      col.column("Grade", NotaRecebimentoProdutoDev::grade.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val invnoCol: TextColumnBuilder<String> =
      col.column("NI", NotaRecebimentoProdutoDev::invnoObs.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("0")
        this.setFixedWidth(30)
      }
  private val qtdeCol: TextColumnBuilder<Int> =
      col.column("Quant", NotaRecebimentoProdutoDev::qtde.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("0")
        this.setFixedWidth(35)
      }
  private val itemCol: TextColumnBuilder<Int> =
      col.column("Item", NotaRecebimentoProdutoDev::item.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setPattern("000")
        this.setFixedWidth(25)
      }
  private val valorUnitarioCol: TextColumnBuilder<Double> =
      col.column("V. Unit", NotaRecebimentoProdutoDev::valorUnitario.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.0000")
        this.setFixedWidth(40)
      }
  private val valorTotalCol: TextColumnBuilder<Double> =
      col.column("V. Total", NotaRecebimentoProdutoDev::valorTotalDevolucao.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val baseICMSCol: TextColumnBuilder<Double> =
      col.column("B. Cálc. ICMS", NotaRecebimentoProdutoDev::baseIcmsDevolucao.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorICMSCol: TextColumnBuilder<Double> =
      col.column("Valor ICMS", NotaRecebimentoProdutoDev::valIcmsDevolucao.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorIPICol: TextColumnBuilder<Double> =
      col.column("Valor IPI", NotaRecebimentoProdutoDev::valIPIDevolucao.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val aliqICMSCol: TextColumnBuilder<Double> =
      col.column("Alíq. ICMS", NotaRecebimentoProdutoDev::icms.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val aliqIPICol: TextColumnBuilder<Double> =
      col.column("Alíq. IPI", NotaRecebimentoProdutoDev::ipi.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }

  private val barcodeCol: TextColumnBuilder<String> =
      col.column("Cód Barra", NotaRecebimentoProdutoDev::barcode.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(80)
      }

  private val cstCol: TextColumnBuilder<String> =
      col.column("CST", NotaRecebimentoProdutoDev::cst.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(25)
      }

  private val cfopCol: TextColumnBuilder<String> =
      col.column("CFOP", NotaRecebimentoProdutoDev::cfop.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(25)
      }

  private val unCol: TextColumnBuilder<String> =
      col.column("Unid", NotaRecebimentoProdutoDev::un.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(30)
      }

  private fun columnBuilder(): List<ColumnBuilder<*, *>> {
    return when {
      resumida -> listOf(
        itemCol,
        barcodeCol,
        refForCol,
        codigoCol,
        descricaoCol,
        gradeCol,
        unCol,
        qtdeCol,
        valorUnitarioCol,
        valorTotalCol
      )

      else     -> listOf(
        invnoCol,
        dataInvCol,
        notaInvCol,
        refForCol,
        codigoCol,
        descricaoCol,
        gradeCol,
        ncmInvCol,
        cstCol,
        cfopCol,
        unCol,
        qtdeCol,
        valorUnitarioCol,
        valorTotalCol,
        baseICMSCol,
        valorICMSCol,
        valorIPICol,
        aliqICMSCol,
        aliqIPICol,
      )
    }
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      text("Espelho Nota Fiscal Devolução Fornecedor", CENTER).apply {
        this.setStyle(fieldFontGrande)
      }
      text("ENGECOPI ${notaSaida.lojaSigla}", LEFT).apply {
        this.setStyle(fieldFontGrande)
      }
      text("Motivo de Devolução: ${notaSaida.tipoDevolucaoEnun?.name ?: ""}", LEFT)
      horizontalList {
        val fornecedor = notaSaida.fornecedor
        val vendno = notaSaida.vendno
        val pedido = notaSaida.numeroDevolucao
        text("Fornecedor: $vendno - $fornecedor", LEFT)
        text("NI Devolução. $pedido", RIGHT, 150)
      }
    }
  }

  private fun titleBuider(): ComponentBuilder<*, *> {
    return titleBuiderPedido()
  }
fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }

  fun makeReport(): JasperReportBuilder? {
    val colunms = columnBuilder().toTypedArray()
    var index = 1
    val itens = notaSaida.produtos.sortedBy { it.codigo }.map {
      it.apply {
        item = index++
      }
    }
    val pageOrientation = if (resumida) PORTRAIT
    else LANDSCAPE
    return report()
      .title(titleBuider())
      .setTemplate(Templates.reportTemplate)
      .columns(* colunms)
      .setColumnStyle(stl.style().setFontSize(7))
      .columnGrid(* colunms)
      .setDataSource(itens.toList())
      .setPageFormat(A4, pageOrientation)
      .setPageMargin(margin(28))
      .summary(pageFooterBuilder())
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(listNota: List<NotaRecebimentoDev>, resumida: Boolean = false): ByteArray {
      val printList = listNota.map { nota ->
        val report = RelatorioNotaDevolucao(nota, resumida).makeReport()
        report?.toJasperPrint()
      }
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(printList))

      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)

      exporter.exportReport()
      return out.toByteArray()
    }
  }
}


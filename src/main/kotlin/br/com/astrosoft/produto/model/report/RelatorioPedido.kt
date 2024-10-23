package br.com.astrosoft.devolucao.model.reports

import br.com.astrosoft.framework.model.reports.Templates
import br.com.astrosoft.framework.model.reports.Templates.fieldFontGrande
import br.com.astrosoft.framework.model.reports.horizontalList
import br.com.astrosoft.framework.model.reports.text
import br.com.astrosoft.framework.model.reports.verticalBlock
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.PedidoCapa
import br.com.astrosoft.produto.model.beans.PedidoProdutoCompra
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.column.ColumnBuilder
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder
import net.sf.dynamicreports.report.builder.subtotal.SubtotalBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.PageOrientation.PORTRAIT
import net.sf.dynamicreports.report.constant.PageType.A4
import net.sf.dynamicreports.report.constant.TextAdjust.CUT_TEXT
import net.sf.dynamicreports.report.constant.TextAdjust.SCALE_FONT
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

class RelatorioPedido(val pedido: PedidoCapa) {
  private val codigoCol: TextColumnBuilder<String> =
      col.column("Cód Saci", PedidoProdutoCompra::codigo.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(35)
      }

  private val refForCol: TextColumnBuilder<String> =
      col.column("Ref do Fab", PedidoProdutoCompra::refFor.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(60)
      }
  private val descricaoCol: TextColumnBuilder<String> =
      col.column("Descrição", PedidoProdutoCompra::descricao.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(LEFT)
        this.setTextAdjust(CUT_TEXT)
      }
  private val gradeCol: TextColumnBuilder<String> =
      col.column("Grade", PedidoProdutoCompra::grade.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val qtdeCol: TextColumnBuilder<Int> =
      col.column("Quant", PedidoProdutoCompra::qttyPedido.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("0")
        this.setFixedWidth(35)
      }
  private val itemCol: TextColumnBuilder<Int> =
      col.column("Item", PedidoProdutoCompra::item.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setPattern("000")
        this.setFixedWidth(25)
      }
  private val valorUnitarioCol: TextColumnBuilder<Double> =
      col.column("V. Unit", PedidoProdutoCompra::custo.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorTotalCol: TextColumnBuilder<Double> =
      col.column("V. Total", PedidoProdutoCompra::valorTotal.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val barcodeCol: TextColumnBuilder<String> =
      col.column("Cód Barra", PedidoProdutoCompra::barcode.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(80)
      }

  private val unCol: TextColumnBuilder<String> =
      col.column("Unid", PedidoProdutoCompra::un.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(30)
      }

  private fun columnBuilder(): List<ColumnBuilder<*, *>> {
    return listOf(
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
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      text("ENGECOPI ${pedido.sigla}", LEFT).apply {
        this.setStyle(fieldFontGrande)
      }
      horizontalList {
        val fornecedor = pedido.fornecedor
        val dataPedido = pedido.data.format()
        val pedidoNum = pedido.pedido
        text("Fornecedor: $fornecedor", LEFT)
        text("PED. $pedidoNum - $dataPedido", RIGHT, 150)
      }
    }
  }

  private fun titleBuider(): ComponentBuilder<*, *> {
    return titleBuiderPedido()
  }

  private fun TextFieldBuilder<String>.fonteSumarioImposto() {
    this.setTextAdjust(SCALE_FONT)
    this.setStyle(stl.style().setFontSize(8))
  }

  private fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }

  private fun subtotalBuilder(): List<SubtotalBuilder<*, *>> {
    return listOf(
      sbt.text("Total R$", valorUnitarioCol),
      sbt.sum(valorTotalCol),
    )
  }

  fun makeReport(): JasperReportBuilder? {
    val colunms = columnBuilder().toTypedArray()
    var index = 1
    val itens = pedido.produtosCompra().sortedBy { it.codigo }.map {
      it.apply {
        this.item = index++
      }
    }
    val pageOrientation = PORTRAIT
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
      .subtotalsAtSummary(* subtotalBuilder().toTypedArray())
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(listNota: List<PedidoCapa>): ByteArray {
      val printList = listNota.map { nota ->
        val report = RelatorioPedido(nota).makeReport()
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


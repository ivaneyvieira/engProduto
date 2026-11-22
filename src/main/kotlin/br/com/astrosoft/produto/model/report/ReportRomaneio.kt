package br.com.astrosoft.produto.model.report

import br.com.astrosoft.framework.model.reports.*
import br.com.astrosoft.framework.model.reports.Templates.columnStyle
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaVendaRef
import br.com.astrosoft.produto.model.beans.ProdutoNFS
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.column.ColumnBuilder
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.subtotal.SubtotalBuilder
import net.sf.dynamicreports.report.constant.HorizontalTextAlignment.*
import net.sf.dynamicreports.report.constant.Position
import net.sf.jasperreports.engine.export.JRPdfExporter
import net.sf.jasperreports.export.SimpleExporterInput
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput
import java.io.ByteArrayOutputStream

class ReportRomaneio(val nota: NotaVendaRef) {
  val colCodigo: TextColumnBuilder<String?>? = col.column("Código", ProdutoNFS::codigo.name, type.stringType()).apply {
    this.setHorizontalTextAlignment(LEFT)
    this.setFixedWidth(40)
  }
  val colDescricao: TextColumnBuilder<String?>? =
    col.column("Descrição", ProdutoNFS::descricao.name, type.stringType()).apply {}
  val colGrade: TextColumnBuilder<String?>? = col.column("Grade", ProdutoNFS::grade.name, type.stringType()).apply {
    this.setHorizontalTextAlignment(LEFT)
    this.setFixedWidth(50)
  }
  val colCodBarras: TextColumnBuilder<String?>? = col.column(
    "Cod Barras", ProdutoNFS::barcodeProd.name, type.stringType()
  ).apply {
    this.setHorizontalTextAlignment(LEFT)
    this.setFixedWidth(80)
  }
  val colQtd: TextColumnBuilder<Int?>? = col.column("Qtd", ProdutoNFS::quantidade.name, type.integerType()).apply {
    this.setPattern("#,##0.####")
    this.setFixedWidth(40)
  }
  val colVlUnit: TextColumnBuilder<Double?>? = col.column("R$ Unit", ProdutoNFS::preco.name, type.doubleType()).apply {
    this.setPattern("#,##0.00")
    this.setFixedWidth(80)
  }
  val vlTotal: TextColumnBuilder<Double?>? = col.column("R$ Total", ProdutoNFS::total.name, type.doubleType()).apply {
    this.setPattern("#,##0.00")
    this.setFixedWidth(80)
  }
  
  fun makeReportPedido(): JasperReportBuilder? {
    val colunms = columnBuilder().toTypedArray()
    return report().title(titleBuider()).setTemplate(Templates.reportTemplate).columns(*colunms).columnGrid(*colunms)
      .subtotalsAtSummary(*subtotalBuilder().toTypedArray()).setDataSource(dataSource()).summary(pageFooterBuilder())
      .setSubtotalStyle(stl.style().setPadding(2).setTopBorder(stl.pen1Point()))
  }
  
  fun makeReportMinuta(): JasperReportBuilder? {
    return report().title(titleBuiderMinutaCompacta()).setTemplate(Templates.reportTemplate)
      .setDataSource(nota.produtos())
  }
  
  fun titleBuiderReportDuplo(): ComponentBuilder<*, *> {
    return verticalBlock {
      this.add(cmp.subreport(makeReportPedido()))
      this.add(cmp.subreport(makeReportMinuta()))
    }
  }
  
  fun makeReportDuplo(): JasperReportBuilder? {
    return report().title(titleBuiderReportDuplo())
  }
  
  private fun titleBuiderMinutaCompacta(): ComponentBuilder<*, *> {
    return verticalBlock {
      breakLine()
      horizontalList {
        text("MINUTA DE ENTREGA", CENTER)
      }
      
      text("END DE ENTREGA: ${nota.enderecoEntrega} - ${nota.bairroEntrega}    ${nota.area}    ${nota.rota}")
      
      text("Observação Vendedor:")
      
      if (nota.obs1.isNullOrBlank().not()) text(nota.obs1.trim())
      if (nota.obs2.isNullOrBlank().not()) text(nota.obs2.trim())
      if (nota.obs3.isNullOrBlank().not()) text(nota.obs3.trim())
      if (nota.obs4.isNullOrBlank().not()) text(nota.obs4.trim())
      if (nota.obs5.isNullOrBlank().not()) text(nota.obs5.trim())
      if (nota.obs6.isNullOrBlank().not()) text(nota.obs6.trim())
      if (nota.obs7.isNullOrBlank().not()) text(nota.obs7.trim())
      
      breakLine()
      horizontalList {
        text("Separador:  __________________________________", LEFT)
        text("Motorista: __________________________________", RIGHT)
      }
      horizontalList {
        text("Hora Saída: ___________________", LEFT)
        text("Hora Chegada: ___________________", CENTER)
        text("Quilometragem: ___________________", RIGHT)
      }
      breakLine()
      horizontalList {
        text("Data Recebimento\n_____/_____/_____\n\nObservação Motorista:", CENTER, 150)
        text(
          "\n_____________________________________________________\nIdentificação e Assinatura do(a) Recebedor(a)",
          CENTER
        )
      }
    }
  }
  
  private fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }
  
  private fun titleBuider(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalList {
        text("ENGECOPI ${nota.siglaLoja}", LEFT)
        val tipoPedido = if (nota.tipo?.startsWith("E") == true) "ENTREGA" else "RETIRA"
        text("ROMANEIO DE SEPARAÇÃO PEDIDO DE $tipoPedido: ${this@ReportRomaneio.nota.pedido}", CENTER, 300)
        text("${nota.data?.format()}-${nota.hora.format()}", RIGHT)
      }
      horizontalList {
        val nfText = "NF: ${nota.nota}"
        val dataText = "DATA: ${nota.data.format()}-${nota.hora.format()}"
        val valorText = "VALOR R$: ${nota.valorComFrete.format()}"
        val pdvText = "PDV: ${nota.pdv}"
        val metodo = "MET PGTO: ${this@ReportRomaneio.nota.nomeMetodo}"
        text("$nfText $dataText $valorText      $pdvText      $metodo")
      }
      horizontalList {
        text("VENDEDOR: ${nota.vendedor?.replace(" +".toRegex(), " ") ?: ""}", LEFT)
      }
      horizontalList {
        text("CLIENTE: ${nota.cliente}       ${nota.endereco}   ${this@ReportRomaneio.nota.bairro}", LEFT)
      }
    }
  }
  
  private fun dataSource(): List<ProdutoNFS> {
    return nota.produtos().sortedBy { "${it.descricao} ${it.grade}" }
  }
  
  private fun subtotalBuilder(): List<SubtotalBuilder<*, *>> {
    val style = stl.style(columnStyle).setTopBorder(stl.pen1Point())
    return listOf(
      sbt.text("", colCodigo),
      sbt.text("", colDescricao),
      sbt.text("", colGrade),
      sbt.text("", colCodBarras),
      sbt.text("", colQtd),
      sbt.text("", colVlUnit),
      sbt.sum(vlTotal).setLabel("Total R$").setLabelStyle(style).setLabelPosition(Position.LEFT).setStyle(style)
    )
  }
  
  private fun columnBuilder(): List<ColumnBuilder<*, *>> {
    return listOfNotNull(colCodigo, colDescricao, colGrade, colCodBarras, colQtd, colVlUnit, vlTotal)
  }
  
  companion object {
    fun processaRelatorio(list: List<NotaVendaRef>): ByteArray {
      val reports = list.flatMap { pedido ->
        val report = ReportRomaneio(pedido)
        listOf(report.makeReportDuplo())
      }.filterNotNull()
      val jasperPrints = reports.map { jasperReportBuild ->
        jasperReportBuild.toJasperPrint()
      }
      val exporter = JRPdfExporter()
      val out = ByteArrayOutputStream()
      exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrints))
      
      exporter.exporterOutput = SimpleOutputStreamExporterOutput(out)
      
      exporter.exportReport()
      return out.toByteArray()
    }
  }
}

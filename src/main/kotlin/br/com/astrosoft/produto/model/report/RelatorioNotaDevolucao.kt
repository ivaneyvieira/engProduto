package br.com.astrosoft.produto.model.report

import br.com.astrosoft.devolucao.model.beans.NotaSaida
import br.com.astrosoft.devolucao.model.beans.ProdutosNotaSaida
import br.com.astrosoft.framework.model.reports.*
import br.com.astrosoft.framework.model.reports.Templates.fieldBorder
import br.com.astrosoft.framework.model.reports.Templates.fieldFontGrande
import br.com.astrosoft.framework.util.format
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import net.sf.dynamicreports.jasper.builder.JasperReportBuilder
import net.sf.dynamicreports.report.builder.DynamicReports.*
import net.sf.dynamicreports.report.builder.column.ColumnBuilder
import net.sf.dynamicreports.report.builder.column.TextColumnBuilder
import net.sf.dynamicreports.report.builder.component.ComponentBuilder
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder
import net.sf.dynamicreports.report.builder.subtotal.SubtotalBuilder
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
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.map

class RelatorioNotaDevolucao(val notaSaida: NotaRecebimentoDev, private val resumida: Boolean, val pendente: Boolean) {
  private val codigoCol: TextColumnBuilder<String> =
      col.column("Cód Saci", ProdutosNotaSaida::codigoStr.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(35)
      }
  private val dataInvCol: TextColumnBuilder<String> =
      col.column("Emissão", ProdutosNotaSaida::dateInvStr.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val notaInvCol: TextColumnBuilder<String> =
      col.column("NF", ProdutosNotaSaida::notaInv.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val ncmInvCol: TextColumnBuilder<String> =
      col.column("NCM", ProdutosNotaSaida::ncm.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val refForCol: TextColumnBuilder<String> =
      col.column("Ref do Fab", ProdutosNotaSaida::refFor.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(60)
      }
  private val descricaoCol: TextColumnBuilder<String> =
      col.column("Descrição", ProdutosNotaSaida::descricao.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(LEFT)
        this.setTextAdjust(CUT_TEXT)
      }
  private val gradeCol: TextColumnBuilder<String> =
      col.column("Grade", ProdutosNotaSaida::grade.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setTextAdjust(SCALE_FONT)
        this.setFixedWidth(40)
      }
  private val stCol: TextColumnBuilder<String> =
      col.column("ST", ProdutosNotaSaida::st.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(25)
      }
  private val invnoCol: TextColumnBuilder<String> =
      col.column("NI", ProdutosNotaSaida::invnoObs.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("0")
        this.setFixedWidth(30)
      }
  private val qtdeCol: TextColumnBuilder<Int> =
      col.column("Quant", ProdutosNotaSaida::qtde.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("0")
        this.setFixedWidth(35)
      }
  private val itemCol: TextColumnBuilder<Int> =
      col.column("Item", ProdutosNotaSaida::item.name, type.integerType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setPattern("000")
        this.setFixedWidth(25)
      }
  private val valorUnitarioCol: TextColumnBuilder<Double> =
      col.column("V. Unit", ProdutosNotaSaida::valorUnitario.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorTotalCol: TextColumnBuilder<Double> =
      col.column("V. Total", ProdutosNotaSaida::valorTotal.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorTotalIpiCol: TextColumnBuilder<Double> =
      col.column("R$ Total Geral", ProdutosNotaSaida::valorTotalIpi.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val ipiCol: TextColumnBuilder<Double> =
      col.column("Valor Ipi", ProdutosNotaSaida::ipi.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val vstCol: TextColumnBuilder<Double> =
      col.column("Valor ST", ProdutosNotaSaida::vst.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val baseICMSCol: TextColumnBuilder<Double> =
      col.column("B. Cálc. ICMS", ProdutosNotaSaida::baseICMS.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorICMSCol: TextColumnBuilder<Double> =
      col.column("Valor ICMS", ProdutosNotaSaida::valorICMS.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val valorIPICol: TextColumnBuilder<Double> =
      col.column("Valor IPI", ProdutosNotaSaida::valorIPI.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val aliqICMSCol: TextColumnBuilder<Double> =
      col.column("Alíq. ICMS", ProdutosNotaSaida::icmsAliq.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }
  private val aliqIPICol: TextColumnBuilder<Double> =
      col.column("Alíq. IPI", ProdutosNotaSaida::ipiAliq.name, type.doubleType()).apply {
        this.setHorizontalTextAlignment(RIGHT)
        this.setTextAdjust(SCALE_FONT)
        this.setPattern("#,##0.00")
        this.setFixedWidth(40)
      }

  private val barcodeCol: TextColumnBuilder<String> =
      col.column("Cód Barra", ProdutosNotaSaida::barcode.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(80)
      }

  private val cstCol: TextColumnBuilder<String> =
      col.column("CST", ProdutosNotaSaida::cst.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(25)
      }

  private val cfopCol: TextColumnBuilder<String> =
      col.column("CFOP", ProdutosNotaSaida::cfop.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(25)
      }

  private val unCol: TextColumnBuilder<String> =
      col.column("Unid", ProdutosNotaSaida::un.name, type.stringType()).apply {
        this.setHorizontalTextAlignment(CENTER)
        this.setFixedWidth(30)
      }

  private fun columnBuilder(): List<ColumnBuilder<*, *>> {
    return when (notaSaida.tipo) {
      "66", "AJT", "FIN" -> when {
        resumida -> listOf(
          itemCol,
          barcodeCol,
          refForCol,
          codigoCol,
          descricaoCol,
          gradeCol,
          unCol,
          qtdeCol,
        )

        else     -> listOf(
          itemCol,
          barcodeCol,
          refForCol,
          codigoCol,
          descricaoCol,
          gradeCol,
          unCol,
          stCol,
          qtdeCol,
          valorUnitarioCol,
          valorTotalCol,
          ipiCol,
          vstCol,
          valorTotalIpiCol,
        )
      }

      "PED", "AVA"       -> when {
        resumida -> if (pendente) listOf(
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
        else listOf(itemCol, barcodeCol, refForCol, codigoCol, descricaoCol, gradeCol, unCol, qtdeCol)

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

      else               -> listOf(
        itemCol,
        barcodeCol,
        codigoCol,
        descricaoCol,
        gradeCol,
        cstCol,
        cfopCol,
        unCol,
        qtdeCol,
        valorUnitarioCol,
        valorTotalCol
      )
    }
  }

  private fun titleBuiderPedido(): ComponentBuilder<*, *> {
    return verticalBlock {
      text("Espelho Nota Fiscal Devolução Fornecedor", CENTER).apply {
        this.setStyle(fieldFontGrande)
      }
      text("ENGECOPI ${notaSaida.sigla}", LEFT).apply {
        this.setStyle(fieldFontGrande)
      }
      text("Natureza da operação: ${notaSaida.natureza}", LEFT)
      horizontalList {
        val custno = notaSaida.custno
        val fornecedor = notaSaida.fornecedor
        val fornecedorSap = notaSaida.fornecedorSap
        val vendno = notaSaida.vendno
        val pedido = notaSaida.pedido
        val dataPedido = notaSaida.dataPedido.format()
        text("Fornecedor: $custno - $fornecedor (FOR - $vendno  SAP - $fornecedorSap)", LEFT)
        text("PED. $pedido - $dataPedido", RIGHT, 150)
      }
    }
  }

  private fun titleBuiderNota66(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalList {
        text("ENGECOPI ${notaSaida.sigla}", CENTER).apply {
          this.setStyle(fieldFontGrande)
        }
      }
      horizontalList {
        val dataAtual = LocalDate.now().format()
        val horaAtual = LocalTime.now().format()
        val custno = notaSaida.custno
        val fornecedor = notaSaida.fornecedor
        val vendno = notaSaida.vendno
        val pedido = notaSaida.pedido
        val dataPedido = notaSaida.dataPedido.format()
        val nota = notaSaida.nota
        val dataNota = notaSaida.dataNota.format()
        val fornecedorSap = notaSaida.fornecedorSap

        text(
          "$custno - $fornecedor (FOR - $vendno  SAP - $fornecedorSap)   PED. $pedido - $dataPedido   NOTA $nota - $dataNota",
          LEFT
        )
        text("$dataAtual-$horaAtual", RIGHT, 100)
      }
    }
  }

  private fun titleBuilderFinanceiro(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalList {
        text("ENGECOPI ${notaSaida.sigla}", CENTER).apply {
          this.setStyle(fieldFontGrande)
        }
      }
      horizontalList {
        val dataAtual = LocalDate.now().format()
        val horaAtual = LocalTime.now().format()
        val custno = notaSaida.custno
        val fornecedor = notaSaida.fornecedor
        val vendno = notaSaida.vendno
        val nota = notaSaida.nota
        val dataNota = notaSaida.dataNota.format()
        val fornecedorSap = notaSaida.fornecedorSap

        text("$custno - $fornecedor (FOR - $vendno  SAP - $fornecedorSap)   NFA $nota - $dataNota", LEFT)
        text("$dataAtual-$horaAtual", RIGHT, 100)
      }
    }
  }

  private fun titleBuiderAjuste(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalList {
        text("ENGECOPI ${notaSaida.sigla}", CENTER).apply {
          this.setStyle(fieldFontGrande)
        }
      }
      horizontalList {
        val dataAtual = LocalDate.now().format()
        val horaAtual = LocalTime.now().format()
        val custno = notaSaida.custno
        val fornecedor = notaSaida.fornecedor
        val vendno = notaSaida.vendno
        val nota = notaSaida.nota
        val dataNota = notaSaida.dataNota.format()
        val fornecedorSap = notaSaida.fornecedorSap

        text("$custno - $fornecedor (FOR - $vendno  SAP - $fornecedorSap)   NFA $nota - $dataNota", LEFT)
        text("$dataAtual-$horaAtual", RIGHT, 100)
      }
    }
  }

  private fun titleBuiderNota01(): ComponentBuilder<*, *> {
    return verticalBlock {
      horizontalList {
        text("Natureza: Devolução", LEFT)
        text("ENGECOPI ${notaSaida.sigla}", CENTER).apply {
          this.setStyle(fieldFontGrande)
          this.setFixedWidth(120)
        }
        text("", RIGHT)
      }
      horizontalList {
        val dataAtual = LocalDate.now().format()
        val horaAtual = LocalTime.now().format()
        val custno = notaSaida.custno
        val fornecedor = notaSaida.fornecedor
        val vendno = notaSaida.vendno
        val nota = notaSaida.nota
        val dataNota = notaSaida.dataNota.format()
        val fatura = notaSaida.fatura
        val fornecedorSap = notaSaida.fornecedorSap

        text(
          "$custno - $fornecedor (FOR - $vendno  SAP - $fornecedorSap)   NFD $nota - $dataNota   DUP $fatura",
          LEFT
        )
        text("$dataAtual-$horaAtual", RIGHT, 100)
      }
    }
  }

  private fun titleBuider(): ComponentBuilder<*, *> {
    return when (notaSaida.tipo) {
      "PED" -> titleBuiderPedido()
      "AVA" -> titleBuiderPedido()
      "AJT" -> titleBuiderAjuste()
      "FIN" -> titleBuilderFinanceiro()
      "66"  -> titleBuiderNota66()
      else  -> titleBuiderNota01()
    }
  }

  private fun sumaryBuild(): ComponentBuilder<*, *> {
    return verticalBlock {
      when (notaSaida.tipo) {
        "1"   -> sumaryNota()
        "PED" -> sumaryPedido()
        "AVA" -> sumaryPedido()
      }

      breakLine()

      text("Dados Adicionais:", LEFT, 100)
      text(notaSaida.obsNota, LEFT)
      if (notaSaida.tipo in listOf("66", "PED", "AVA", "AJT", "FIN")) text(notaSaida.obsPedido)
    }
  }

  private fun VerticalListBuilder.sumaryNota() {
    breakLine()
    this.horizontalList {
      this.verticalList {
        setStyle(fieldBorder)
        text("BASE DE CÁLCULO DO ICMS", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.baseIcmsProdutos.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO ICMS", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorIcmsProdutos.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("BASE DE CÁLCULO DO ICMS SUBSTITUIÇÃO", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.baseIcmsSubstProduto.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR ICMS SUBSTITUIÇÃO", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.icmsSubstProduto.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR TOTAL DOS PRODUTOS", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorTotalProduto.format(), RIGHT)
      }
    }
    this.horizontalList {
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO FRETE", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorFrete.format(), RIGHT)
      }

      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO SEGURO", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorSeguro.format(), RIGHT)

      }
      this.verticalList {
        setStyle(fieldBorder)
        text("DESCONTO", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorDesconto.format(), RIGHT)

      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR ICMS SUBSTITUIÇÃO", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.icmsSubst.format(), RIGHT)

      }
      this.verticalList {
        setStyle(fieldBorder)
        text("OUTRAS DESPESAS ACESSÓRIAS", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.outrasDespesas.format(), RIGHT)

      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR TOTAL DA NOTA", LEFT) {
          this.setTextAdjust(SCALE_FONT)
        }
        text(notaSaida.valorTotalProduto.format(), RIGHT)
      }
    }
  }

  private fun VerticalListBuilder.sumaryPedido() {
    breakLine()
    this.horizontalList {
      this.verticalList {
        setStyle(fieldBorder)
        text("BASE DE CÁLCULO DO ICMS", LEFT).fonteSumarioImposto()
        text(notaSaida.baseIcmsProdutos.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO ICMS", LEFT).fonteSumarioImposto()
        text(notaSaida.valorIcmsProdutos.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("BASE DE CÁLCULO DO ICMS ST", LEFT).fonteSumarioImposto()
        text(notaSaida.baseIcmsSubstProduto.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR ICMS ST", LEFT).fonteSumarioImposto()
        text(notaSaida.icmsSubstProduto.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR TOTAL DOS PRODUTOS", LEFT).fonteSumarioImposto()
        text(notaSaida.valorTotalProduto.format(), RIGHT)
      }
    }
    this.horizontalList {
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO FRETE", LEFT).fonteSumarioImposto()
        text(notaSaida.valorFrete.format(), RIGHT)
      }

      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR DO SEGURO", LEFT).fonteSumarioImposto()
        text(notaSaida.valorSeguro.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("DESCONTO", LEFT).fonteSumarioImposto()
        text(notaSaida.valorDesconto.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("OUTRAS DESPESAS", LEFT).fonteSumarioImposto()
        text(notaSaida.outrasDespesas.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR IPI", LEFT).fonteSumarioImposto()
        text(notaSaida.valorIpiProdutos.format(), RIGHT)
      }
      this.verticalList {
        setStyle(fieldBorder)
        text("VALOR TOTAL DA NOTA", LEFT).fonteSumarioImposto()
        text(notaSaida.valorTotalNota.format(), RIGHT)
      }
    }
  }

  private fun TextFieldBuilder<String>.fonteSumarioImposto() {
    this.setTextAdjust(SCALE_FONT)
    this.setStyle(stl.style().setFontSize(8))
  }

  private fun pageFooterBuilder(): ComponentBuilder<*, *>? {
    return cmp.verticalList()
  }

  private fun subtotalBuilder(): List<SubtotalBuilder<*, *>> {
    return when (notaSaida.tipo) {
      in listOf("PED", "AVA")              -> emptyList()
      in listOf("66", "AJT", "FIN") -> listOf(
        sbt.text("Total R$", valorUnitarioCol),
        sbt.sum(valorTotalCol),
        sbt.sum(baseICMSCol),
        sbt.sum(valorICMSCol),
        sbt.sum(valorIPICol),
        sbt.sum(ipiCol),
        sbt.sum(vstCol),
        sbt.sum(valorTotalIpiCol),
      )

      else                          -> listOf(
        sbt.text("Total R$", valorUnitarioCol),
        sbt.sum(valorTotalCol),
        sbt.sum(ipiCol),
        sbt.sum(vstCol),
        sbt.sum(valorTotalIpiCol),
      )
    }
  }

  fun makeReport(): JasperReportBuilder? {
    val colunms = columnBuilder().toTypedArray()
    var index = 1
    val itens = notaSaida.listaProdutos().sortedBy { it.codigo }.map {
      it.apply {
        item = index++
      }
    }
    val pageOrientation = if ((notaSaida.tipo in listOf("66", "PED", "AVA", "AJT", "FIN")) && resumida) PORTRAIT
    else LANDSCAPE
    return report()
      .title(titleBuider())
      .setTemplate(Templates.reportTemplate)
      .columns(* colunms)
      .setColumnStyle(stl.style().setFontSize(7))
      .columnGrid(* colunms)
      .setDataSource(itens.toList())
      .summary(sumaryBuild())
      .setPageFormat(A4, pageOrientation)
      .setPageMargin(margin(28))
      .summary(pageFooterBuilder())
      .subtotalsAtSummary(* subtotalBuilder().toTypedArray())
      .setSubtotalStyle(stl.style().setFontSize(8).setPadding(2).setTopBorder(stl.pen1Point()))
      .pageFooter(cmp.pageNumber().setHorizontalTextAlignment(RIGHT).setStyle(stl.style().setFontSize(8)))
  }

  companion object {
    fun processaRelatorio(listNota: List<NotaSaida>, resumida: Boolean = false, pendente: Boolean): ByteArray {
      val printList = listNota.map { nota ->
        val report = RelatorioNotaDevolucao(nota, resumida, pendente).makeReport()
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


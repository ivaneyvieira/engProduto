package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.EMotivoDevolucao
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode
import java.util.*

fun VerticalLayout.formHeader(
  nota: NotaRecebimentoDev,
  readOnly: Boolean = false,
  salvaNota: (notaModificada: NotaRecebimentoDev) -> Unit = {}
) {
  this.setWidthFull()
  this.isPadding = false
  this.isMargin = false
  this.horizontalBlock {
    this.setWidthFull()
    this.isSpacing = true
    verticalBlock {
      this.isPadding = false
      this.isMargin = false
      this.isSpacing = false
      this.width = "100%"

      horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()

        integerField("NI") {
          this.isReadOnly = true
          this.width = "5rem"
          this.value = nota.niPrincipal ?: 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
        textField("NFO") {
          this.isReadOnly = true
          this.width = "5rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.nfEntrada ?: ""
        }
        textField("Emissão") {
          this.isReadOnly = true
          this.width = "6rem"
          this.value = nota.emissao.format()
        }
        textField("Entrada") {
          this.isReadOnly = true
          this.width = "6rem"
          this.value = nota.dataEntrada.format()
        }
        integerField("Cod") {
          this.isReadOnly = true
          this.width = "3.5rem"
          this.value = nota.vendno
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
        textField("Fornecedor") {
          this.isReadOnly = true
          this.isExpand = true
          this.value = nota.fornecedor
        }
      }

      horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()

        textField("Cod") {
          this.isReadOnly = true
          this.width = "3.5rem"
          this.value = nota.transp?.toString() ?: ""
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
        textField("Transportadora") {
          this.isReadOnly = true
          this.isExpand = true
          this.value = nota.transportadora ?: ""
        }
        textField("CTE") {
          this.isReadOnly = true
          this.width = "5rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.cte?.toString()
        }
        textField("Ped Compra") {
          this.isReadOnly = true
          this.width = "5rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.pedComp?.toString()
        }
        textField("Dup Vcto") {
          this.isReadOnly = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.dataVencimentoDup.format()
        }
        textField("Dup Valor") {
          this.isReadOnly = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.valorVencimentoDup.format()
        }
      }

      horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()

        textField("Pedido") {
          this.isReadOnly = true
          this.width = "5rem"
          this.value = nota.numeroDevolucao?.toString() ?: ""
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }

        datePicker("Data") {
          this.isReadOnly = readOnly
          this.localePtBr()
          this.width = "7.5rem"
          this.value = nota.dataColeta

          addValueChangeListener {
            nota.dataColeta = this.value
            salvaNota(nota)
          }
        }

        textField("NFD") {
          this.isReadOnly = true
          this.width = "7rem"
          this.value = nota.notaDevolucao ?: ""
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
        textField("Emissão") {
          this.isReadOnly = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.emissaoDevolucao.format()
        }
        textField("Motivo Devolução") {
          this.isReadOnly = true
          this.isExpand = true
          this.value = nota.motivoDevolucaoName
        }
        textField("Valor Frete") {
          this.isReadOnly = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.freteNota.format()
        }
      }
    }
    verticalBlock {
      this.isPadding = false
      this.isMargin = false
      this.isSpacing = false
      this.setHeightFull()
      this.width = "45rem"
      verticalBlock {
        this.isSpacing = false
        this.setHeightFull()
        textArea("Dados Adicionais") {
          this.width = "100%"
          this.height = "50%"
          this.minRows = 1
          this.maxRows = 4
          this.isExpand = true
          this.isReadOnly = true
          val obsAjustada = nota.obsDevolucaoAjustada()
          val obsCalculado = nota.obsDevolucaoCalculada()
          this.value = if (obsAjustada.isNullOrBlank()) {
            obsCalculado
          } else {
            obsAjustada
          }
        }
        textArea("Observação") {
          this.isReadOnly = readOnly
          this.height = "50%"
          this.minRows = 4
          this.maxRows = 4
          this.width = "100%"
          this.isExpand = true
          this.value = nota.observacaoAdicional ?: ""
          //this.value = nota.obsDevolucaoAjustada() ?: ""

          this.valueChangeMode = ValueChangeMode.LAZY


          addValueChangeListener {
            nota.observacaoAdicional = this.value ?: ""
            salvaNota(nota)
          }
        }
      }
    }
  }
}

private fun NotaRecebimentoDev.obsDevolucaoCalculada(): String? {
  return when (this.motivoDevolucaoEnun) {
    EMotivoDevolucao.AVARIA_TRANSPORTE -> {
      observacaoAvariaTransporte()
    }

    EMotivoDevolucao.ACORDO_COMERCIAL  -> {
      observacaoAcordoComercial()
    }

    EMotivoDevolucao.FALTA_TRANSPORTE  -> {
      observacaoFaltaTransporte()
    }

    EMotivoDevolucao.FALTA_FABRICA     -> {
      observacaoFaltaFabrica()
    }

    EMotivoDevolucao.VALIDADE          -> {
      observacaoValidade()
    }

    EMotivoDevolucao.EM_GARANTIA       -> {
      observacaoGarantia()
    }

    else                               -> {
      ""
    }
  }
}

private fun NotaRecebimentoDev.observacaoAvariaTransporte(): String {
  val linha1 = "Devolução Parcial da NFO ${nfEntrada ?: ""} de ${emissao.format()} Referente"
  val linha2 = if (produtoUnitatio()) {
    "Produtos Avariados no Transporte Notificado No CTe"
  } else {
    "Produtos Avariados no Transporte Notificado No CTe"
  }
  val transportadora = this.nomeTransportadoraDevolucao.nomeProprioCapitalize()
  val linha3 = "${cteDevolucao ?: ""} de ${dataDevolucao.format()} da $transportadora"
  return "$linha1\n$linha2\n$linha3"
}

private fun NotaRecebimentoDev.observacaoAcordoComercial(): String {
  val linha1 = "Devolução Referente Acordo Comercial de Itens das Notas"
  val linha2 = "Fiscais de Origem:"
  return "$linha1\n$linha2"
}

private fun NotaRecebimentoDev.observacaoFaltaTransporte(): String {
  val linha1 = "Devolução Parcial da NFO ${nfEntrada ?: ""} de ${emissao.format()} Referente"
  val linha2 = "Falta No Transporte Notificado no CTe ${cteDevolucao ?: ""} de ${dataDevolucao.format()}"
  val linha3 = "da ${this.nomeTransportadoraDevolucao.nomeProprioCapitalize()}."
  return "$linha1\n$linha2\n$linha3"
}

private fun NotaRecebimentoDev.observacaoFaltaFabrica(): String {
  val linha1 = "Devolução Parcial da NFO ${nfEntrada ?: ""} de ${emissao.format()} Referente"
  val linha2 = "Falta de Fabrica Notificado no CTe ${cteDevolucao ?: ""} de ${dataDevolucao.format()}"
  val linha3 = "da ${this.nomeTransportadoraDevolucao.nomeProprioCapitalize()}."
  return "$linha1\n$linha2\n$linha3"
}

private fun NotaRecebimentoDev.observacaoValidade(): String {
  val linha1 = "Devolução Parcial da NFO ${nfEntrada ?: ""} de ${emissao.format()} Referente"
  val linha2 = if (produtoUnitatio()) {
    "Produto com Validade Proximo do Vencimento Notificado no"
  } else {
    "Produtos com Validade Proximo do Vencimento Notificado no"
  }
  val linha3 =
      "CTe ${cteDevolucao ?: ""} de ${dataDevolucao.format()} da ${this.nomeTransportadoraDevolucao.nomeProprioCapitalize()}."
  return "$linha1\n$linha2\n$linha3"
}

private fun NotaRecebimentoDev.observacaoGarantia(): String {
  val linha1 = if (produtoUnitatio()) {
    "Devolução de Item em Garantia Referente Notas Fiscais"
  } else {
    "Devolução de Itens em Garantia Referente Notas Fiscais"
  }
  val linha2 = "de Origem:"
  return "$linha1\n$linha2"
}

private val preposicoes = setOf("de", "da", "do", "das", "dos", "e")

fun String.nomeProprioCapitalize(): String {
  return this.trim()
    .split("\\s+".toRegex()) // divide por 1+ espaços
    .joinToString(" ") { palavra ->
      val minuscula = palavra.lowercase(Locale.forLanguageTag("pt-BR"))
      if (minuscula in preposicoes) {
        minuscula
      } else {
        minuscula.replaceFirstChar { it.titlecase(Locale.forLanguageTag("pt-BR")) }
      }
    }
}

private fun NotaRecebimentoDev.obsDevolucaoAjustada(): String? {
  val observacao = this.obsDevolucao ?: return null
  val pos1 = observacao.posProxima(41)
  val pos2 = observacao.posProxima(81)
  val pos3 = observacao.posProxima(121)
  val pos4 = observacao.posProxima(161)
  return observacao.substringPos(0, pos1).trim() + "\n" +
         observacao.substringPos(pos1, pos2).trim() + "\n" +
         observacao.substringPos(pos2, pos3).trim() + "\n" +
         observacao.substringPos(pos3, pos4).trim() + "\n" +
         observacao.substringPos(pos4).trim()
}

private fun String.posProxima(pos: Int): Int {
  val posAntes = this.getOrNull(pos - 1) ?: return this.length
  return if (posAntes == ' ') {
    pos
  } else {
    val index = this.indexOf(' ', pos) + 1
    if (index == 0) {
      this.length
    } else {
      index
    }
  }
}

private fun String.substringPos(pos1: Int, pos2: Int = 1000): String {
  if (pos1 < 0) {
    return ""
  }
  if (pos2 < 0) {
    return ""
  }
  if (pos1 > length) {
    return ""
  }
  if (pos2 > length) {
    return this.substring(pos1)
  }
  return this.substring(pos1, pos2)
}

private fun NotaRecebimentoDev.produtoUnitatio(): Boolean {
  val quant = produtos.sumOf { it.quant ?: 0 }
  return quant == 1
}
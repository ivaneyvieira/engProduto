package br.com.astrosoft.produto.view.devForReceb

import br.com.astrosoft.framework.util.format
import br.com.astrosoft.framework.view.vaadin.helper.horizontalBlock
import br.com.astrosoft.framework.view.vaadin.helper.localePtBr
import br.com.astrosoft.framework.view.vaadin.helper.verticalBlock
import br.com.astrosoft.produto.model.beans.NotaRecebimentoDev
import com.github.mvysny.karibudsl.v10.*
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.TextFieldVariant
import com.vaadin.flow.data.value.ValueChangeMode

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

      horizontalBlock {
        this.isSpacing = true
        this.width = "55rem"

        integerField("NI") {
          this.isReadOnly = true
          this.width = "6rem"
          this.value = nota.niPrincipal ?: 0
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
        }
        textField("NFO") {
          this.isReadOnly = true
          this.width = "6rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.nfEntrada ?: ""
        }
        textField("Emissão") {
          this.isReadOnly = true
          this.width = "7rem"
          this.value = nota.emissao.format()
        }
        textField("Entrada") {
          this.isReadOnly = true
          this.width = "7rem"
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
        textField("Ped Compra") {
          this.isReadOnly = true
          this.width = "7rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.pedComp?.toString()
        }
        textField("Dup Vcto") {
          this.isReadOnly = true
          this.width = "7rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.dataVencimentoDup.format()
        }
        textField("Dup Valor") {
          this.isReadOnly = true
          this.width = "7rem"
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
          this.width = "7rem"
          this.addThemeVariants(TextFieldVariant.LUMO_ALIGN_RIGHT)
          this.value = nota.emissaoDevolucao.format()
        }
        textField("Motivo Devolução") {
          this.isReadOnly = true
          this.isExpand = true
          this.value = nota.tipoDevolucaoName ?: ""
        }
      }
    }
    verticalBlock {
      this.isPadding = false
      this.isMargin = false
      this.isSpacing = false
      this.setSizeFull()
      horizontalBlock {
        this.isSpacing = true
        this.setWidthFull()
        textArea("Dados Adicionais") {
          this.width = "100%"
          this.height = "100%"
          this.isExpand = true
          this.isReadOnly = true
          this.value = nota.obsDevolucaoAjustada() ?: ""
        }
        textArea("Observação") {
          this.isReadOnly = readOnly
          this.width = "100%"
          this.height = "100%"
          this.isExpand = true
          this.value = nota.observacaoAdicional ?: ""

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
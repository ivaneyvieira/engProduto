package br.com.astrosoft.produto.view.recebimento

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.component.upload.FileRejectedEvent
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer

class FormArquivo(val addAnexo: (title: String, fileName: String, dados: ByteArray) -> Unit) : FormLayout() {
  private val edtTitulo = TextField("Titulo").apply {
    this.width = "300px"
  }

  private val uploadPair = uploadFile()

  init {
    add(edtTitulo)
    val buffer = uploadPair.first
    val upload = uploadPair.second
    upload.addSucceededListener {
      val fileName = it.fileName
      val bytes = buffer.inputStream.readBytes()
      addAnexo(edtTitulo.value ?: "", fileName, bytes)
    }
    add(upload)
  }

  private fun uploadFile(): Pair<MemoryBuffer, Upload> {
    val buffer = MemoryBuffer()
    val upload = Upload(buffer)
    upload.isDropAllowed = false

    val uploadButton = Button("Adicionar")
    uploadButton.icon = VaadinIcon.PLUS.create()
    upload.uploadButton = uploadButton
    upload.isAutoUpload = true
    upload.maxFileSize = 1024 * 1024 * 1024
    upload.addFileRejectedListener { event: FileRejectedEvent ->
      println(event.errorMessage)
    }
    upload.addFailedListener { event ->
      println(event.reason.message)
    }

    return Pair(buffer, upload)
  }
}

package br.com.astrosoft.framework.view.vaadin.helper

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.upload.FileRejectedEvent
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.component.upload.receivers.MemoryBuffer
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer

fun HasComponents.upload(label: String, addAnexo: (fileName: String, dados: ByteArray) -> Unit) {
  uploadFile(label) { buffer, upload ->
    upload.addSucceededListener {
      val fileName = it.fileName ?: ""
      val bytes = buffer.getInputStream().readBytes()

      if (fileName.isNotBlank() && bytes.isNotEmpty()) {
        addAnexo(fileName, bytes)
      }
    }
    add(upload)
  }
}

private fun uploadFile(label: String, block: (buffer: MemoryBuffer, upload: Upload) -> Unit) {
  val buffer = MemoryBuffer()
  val upload = Upload(buffer)
  //upload.isDropAllowed = false
  upload.setAcceptedFileTypes("image/jpeg", "image/png", "application/pdf", "text/plain")
  val uploadButton = Button(label)
  uploadButton.icon = VaadinIcon.PLUS.create()
  upload.uploadButton = uploadButton
  upload.isAutoUpload = true
  upload.maxFileSize = 1024 * 1024 * 1024
  upload.addFileRejectedListener { event: FileRejectedEvent ->
    DialogHelper.showError(event.errorMessage)
  }
  upload.addFailedListener { event ->
    event.reason.message?.let { msg ->
      DialogHelper.showError(msg)
    }
  }

  block(buffer, upload)
}

package br.com.astrosoft.framework.view.vaadin.helper

import com.vaadin.flow.component.HasComponents
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.upload.FileRejectedEvent
import com.vaadin.flow.component.upload.Upload
import com.vaadin.flow.server.streams.UploadHandler
import com.vaadin.flow.server.streams.UploadMetadata

fun HasComponents.upload(label: String, addAnexo: (fileName: String, dados: ByteArray) -> Unit): Upload {
  val upload = uploadFile(label) { metadata, bytes ->
    val fileName = metadata.fileName
    if (fileName.isNotBlank() && bytes.isNotEmpty()) {
      addAnexo(fileName, bytes)
    }
  }
  add(upload)
  return upload
}

//UploadMetadata var1, byte[] var2
private fun uploadFile(label: String, successCallback: (metadata: UploadMetadata, bytes: ByteArray) -> Unit): Upload {
  val buffer = UploadHandler.inMemory(successCallback)
  val upload = Upload(buffer)
  upload.setAcceptedFileTypes(
    "image/jpeg",
    "image/png",
    "application/pdf",
    "text/plain",
    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
    "application/vnd.ms-excel"
  )
  val uploadButton = Button(label)
  uploadButton.icon = VaadinIcon.PLUS.create()
  upload.uploadButton = uploadButton
  upload.isAutoUpload = true
  upload.maxFileSize = 1024 * 1024 * 1024
  upload.addFileRejectedListener { event: FileRejectedEvent ->
    DialogHelper.showError(event.errorMessage)
  }
  
  return upload
}

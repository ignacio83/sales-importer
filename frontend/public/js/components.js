class ImportForm {
  constructor (form, salesFileImportPort) {
    this.form = form
    this.salesFileImportPort = salesFileImportPort
    this.form.importButton.onclick = _ => this.execute()
  }

  execute () {
    const file = this.form.file.files[0]
    this.salesFileImportPort.execute(file)
  }
}

export default ImportForm

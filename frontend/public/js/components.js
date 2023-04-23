class ImportForm {
  constructor (form, messageDiv, salesFileImportPort) {
    this.form = form
    this.messageDiv = messageDiv
    this.salesFileImportPort = salesFileImportPort
    this.importButton = form.elements.namedItem('importButton')
    this.importButton.onclick = _ => this.execute()
  }

  async execute () {
    this.importButton.disabled = true
    const fileInput = this.form.elements.namedItem('file')
    const file = fileInput.files[0]
    if (!file) {
      this.messageDiv.innerHTML = 'Favor selecionar um arquivo.'
    } else {
      await this.salesFileImportPort.execute(file)
        .then(response => {
          fileInput.value = ''
          this.messageDiv.innerHTML = `${response.transactionsCount} transações importadas.`
        }).catch(error => {
          console.error(error)
          fileInput.value = ''
          this.messageDiv.innerHTML = 'Ocorreu um erro inesperado'
        })
    }
    this.importButton.disabled = false
  }
}

export default ImportForm

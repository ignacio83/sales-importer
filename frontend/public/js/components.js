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
          this.messageDiv.innerHTML = 'Ocorreu um erro inesperado.'
        })
    }
    this.importButton.disabled = false
  }
}

class TransactionsSearch {
  constructor (table, searchButton, messageDiv, findAllTransactionsPort) {
    this.table = table
    this.searchButton = searchButton
    this.messageDiv = messageDiv
    this.findAllTransactionsPort = findAllTransactionsPort
    this.searchButton.onclick = _ => this.search()
  }

  search () {
    this.findAllTransactionsPort.findAll().then(transactions => {
      const tbody = this.table.getElementsByTagName('tbody')[0]
      tbody.innerHTML = ''
      transactions.forEach(transaction => {
        const newRow = tbody.insertRow()
        this.insertCell(newRow, this.toTypeDescription(transaction.type))
        this.insertCell(newRow, transaction.productDescription)
        this.insertCell(newRow, transaction.salesPersonName)
        this.insertCell(newRow, new Intl.NumberFormat('pt-BR', { style: 'decimal', minimumFractionDigits: 2 }).format(transaction.value))
        this.insertCell(newRow, transaction.date.toLocaleString('pt-BR'))
      })
    }).catch(error => {
      console.error(error)
      this.messageDiv.innerHTML = 'Ocorreu um erro inesperado.'
    })
  }

  toTypeDescription (type) {
    const descriptions = {
      CommissionPayed: 'Comissão paga',
      CommissionReceived: 'Comissão recebida',
      ProducerSale: 'Venda do produtor',
      AffiliateSale: 'Venda do afiliado'
    }
    return descriptions[type]
  }

  insertCell (row, textContent) {
    const typeCell = row.insertCell()
    typeCell.innerHTML = textContent
  }
}

class BalanceValue {
  constructor (valueOutput, messageDiv, findBalancePort) {
    this.valueOutput = valueOutput
    this.messageDiv = messageDiv
    this.findBalancePort = findBalancePort
  }

  autoRefresh (id, timeout) {
    setInterval(() => this.getBalance(id), timeout)
  }

  getBalance (id) {
    this.findBalancePort.findBalance(id).then(value => {
      if (value == null) {
        value = 0
      }
      this.valueOutput.innerHTML = new Intl.NumberFormat('pt-BR', { style: 'decimal', minimumFractionDigits: 2 }).format(value)
    }).catch(error => {
      console.error(error)
      this.messageDiv.innerHTML = 'Ocorreu um erro inesperado.'
    })
  }
}

export { TransactionsSearch, ImportForm, BalanceValue }

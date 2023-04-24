class SalesFileUploadAdapter {
  constructor (basePath) {
    this.basePath = basePath
  }

  execute (file) {
    const formData = new FormData()
    formData.append('file', file)

    return fetch(`${this.basePath}/api/v1/sales/upload`, {
      method: 'POST',
      body: formData,
      headers: {
        Accept: 'application/json'
      }
    })
      .then(res => this.handleResponse(res))
  }

  handleResponse (res) {
    if (res.status === 200) {
      return res.json()
    } else if (res.status >= 400 && res.status < 500) {
      return res.json().then(json => {
        throw new Error(json.detail)
      })
    } else {
      console.error('Unable to call upload service:', res.status)
      throw new Error('Unable to upload file')
    }
  }
}

class TransactionSearchAdapter {
  constructor (basePath) {
    this.basePath = basePath
  }

  findAll () {
    return fetch(`${this.basePath}/api/v1/sales/transactions`, {
      method: 'GET',
      headers: {
        Accept: 'application/json'
      }
    })
      .then(res => this.handleResponse(res))
  }

  handleResponse (res) {
    if (res.status === 200) {
      return res.json().then(transactions => {
        return transactions.map(transaction => {
          const date = new Date(transaction.date)
          transaction.date = date
          return transaction
        })
      })
    } else if (res.status >= 400 && res.status < 500) {
      return res.json().then(json => {
        throw new Error(json.detail)
      })
    } else {
      console.error('Unable to find all transactions:', res.status)
      throw new Error('Unable to find all transactions')
    }
  }
}

class ProducerBalanceAdapter {
  constructor (basePath) {
    this.basePath = basePath
  }

  findBalance (id) {
    return fetch(`${this.basePath}/api/v1/producers/${id}/balance`, {
      method: 'GET',
      headers: {
        Accept: 'application/json'
      }
    })
      .then(res => this.handleResponse(res))
  }

  handleResponse (res) {
    if (res.status === 200) {
      return res.json().then(json => {
        return json.value
      })
    } else if (res.status === 404) {
      return null
    } else {
      console.error('Unable to find producer balance:', res.status)
      throw new Error('Unable to find producer balance')
    }
  }
}

class AffiliateBalanceAdapter {
  constructor (basePath) {
    this.basePath = basePath
  }

  findBalance (id) {
    return fetch(`${this.basePath}/api/v1/affiliates/${id}/balance`, {
      method: 'GET',
      headers: {
        Accept: 'application/json'
      }
    })
      .then(res => this.handleResponse(res))
  }

  handleResponse (res) {
    if (res.status === 200) {
      return res.json().then(json => {
        return json.value
      })
    } else if (res.status === 404) {
      return null
    } else {
      console.error('Unable to find affiliate balance:', res.status)
      throw new Error('Unable to find affiliate balance')
    }
  }
}

export { SalesFileUploadAdapter, TransactionSearchAdapter, ProducerBalanceAdapter, AffiliateBalanceAdapter }

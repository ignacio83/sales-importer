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
      throw new Error('Unable to call upload service')
    }
  }
}

export default SalesFileUploadAdapter

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
      .then(res => res.json())
      .catch(x => console.error(x))
  }
}

export default SalesFileUploadAdapter

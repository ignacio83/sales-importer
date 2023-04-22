class ImportButton {
    constructor(element, salesFileImportPort) {
        this.element = element;
        this.salesFileImportPort = salesFileImportPort
        this.element.onclick = _ => this.execute()
    }

    execute() {
        this.salesFileImportPort.execute()
    }
}

export default ImportButton
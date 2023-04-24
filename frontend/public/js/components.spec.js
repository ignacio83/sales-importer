/**
 * @jest-environment jsdom
 */

import { ImportForm, BalanceValue, TransactionsSearch } from './components'
import fs from 'fs'
import { JSDOM } from 'jsdom'
import path from 'path'
import userEvent from '@testing-library/user-event'
import { getByText } from '@testing-library/dom'

class SalesFileImportAdapterMock {
  called = false

  constructor (expectedResponse, expectedError) {
    this.expectedResponse = expectedResponse
    this.expectedError = expectedError
  }

  async execute (file) {
    this.called = true

    return new Promise((resolve, reject) => {
      if (this.expectedError) {
        reject(this.expectedError)
      } else {
        resolve(this.expectedResponse)
      }
    })
  }
}

describe('ImportForm.execute()', () => {
  const tests = [
    {
      name: 'should call backend when file is provided and clicks at import button',
      adapter: new SalesFileImportAdapterMock({ transactionsCount: 20 }),
      userInteraction: (page) => {
        const fileInput = page.window.document.getElementById('fileImporterForm').elements.namedItem('file')
        const button = getByText(page.window.document, 'Importar')

        const user = userEvent.setup()
        const file = new File(['content'], 'sales.txt')

        return user.upload(fileInput, file)
          .then(() => user.click(button))
      },
      wantPage: (page) => {
        const fileInput = page.window.document.getElementById('fileImporterForm').elements.namedItem('file')
        expect(fileInput.value).toBe('')

        const messages = page.window.document.getElementById('messages')
        expect(messages.innerHTML).toBe('20 transações importadas.')
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show message when file is not provided',
      adapter: new SalesFileImportAdapterMock(),
      userInteraction: (page) => {
        const button = getByText(page.window.document, 'Importar')
        const user = userEvent.setup()
        return user.click(button)
      },
      wantAdapterCalled: false,
      wantPage: (page) => {
        const messages = page.window.document.getElementById('messages')
        expect(messages.innerHTML).toBe('Favor selecionar um arquivo.')
      }
    },
    {
      name: 'should show error message when error occurs',
      adapter: new SalesFileImportAdapterMock(null, new Error('error')),
      userInteraction: (page) => {
        const fileInput = page.window.document.getElementById('fileImporterForm').elements.namedItem('file')
        const button = getByText(page.window.document, 'Importar')

        const user = userEvent.setup()
        const file = new File(['content'], 'sales.txt')

        return user.upload(fileInput, file)
          .then(() => user.click(button))
      },
      wantAdapterCalled: true,
      wantPage: (page) => {
        const messages = page.window.document.getElementById('messages')
        expect(messages.innerHTML).toBe('Ocorreu um erro inesperado.')
      }
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const html = fs.readFileSync(path.resolve(__dirname, '../index.html'))
      const page = new JSDOM(html)
      const form = page.window.document.getElementById('fileImporterForm')
      const messages = page.window.document.getElementById('messages')
      const importForm = new ImportForm(form, messages, test.adapter)

      expect(importForm.form).toBe(form)
      expect(importForm.messageDiv).toBe(messages)

      await test.userInteraction(page)

      expect(test.adapter.called).toBe(test.wantAdapterCalled)

      if (test.wantPage) {
        test.wantPage(page)
      }
    })
  })
})

class FindAllTransactionsAdapterMock {
  called = false

  constructor (expectedResponse, expectedError) {
    this.expectedResponse = expectedResponse
    this.expectedError = expectedError
  }

  async findAll () {
    this.called = true

    return new Promise((resolve, reject) => {
      if (this.expectedError) {
        reject(this.expectedError)
      } else {
        resolve(this.expectedResponse)
      }
    })
  }
}

describe('TransactionsSearch.search()', () => {
  const tests = [
    {
      name: 'should show transactions when click in search button',
      adapter: new FindAllTransactionsAdapterMock([{
        type: 'CommissionPayed',
        productDescription: 'Pencil',
        value: 10.00,
        salesPersonName: 'Walter White',
        date: new Date('2023-03-10T13:30:02Z')
      }]),
      userInteraction: (page) => {
        const button = getByText(page.window.document, 'Consultar')
        const user = userEvent.setup()
        return user.click(button)
      },
      wantPage: (page) => {
        const tableBody = page.window.document.getElementById('transactionsTable').tBodies[0]
        expect(tableBody.rows.length).toBe(1)
        expect(tableBody.rows.item(0).cells.item(0).innerHTML).toBe('Comissão paga')
        expect(tableBody.rows.item(0).cells.item(1).innerHTML).toBe('Pencil')
        expect(tableBody.rows.item(0).cells.item(2).innerHTML).toBe('Walter White')
        expect(tableBody.rows.item(0).cells.item(3).innerHTML).toBe('10,00')
        expect(tableBody.rows.item(0).cells.item(4).innerHTML).not.toBeNull()
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show error message when error occurs',
      adapter: new FindAllTransactionsAdapterMock(null, new Error('error')),
      userInteraction: (page) => {
        const button = getByText(page.window.document, 'Consultar')
        const user = userEvent.setup()
        return user.click(button)
      },
      wantAdapterCalled: true,
      wantPage: (page) => {
        const messages = page.window.document.getElementById('transactionMessages')
        expect(messages.innerHTML).toBe('Ocorreu um erro inesperado.')
      }
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const html = fs.readFileSync(path.resolve(__dirname, '../index.html'))
      const page = new JSDOM(html)
      const transactionsTable = page.window.document.getElementById('transactionsTable')
      const searchButton = page.window.document.getElementById('searchTransactionsButton')
      const messages = page.window.document.getElementById('transactionMessages')
      const transactionsSearch = new TransactionsSearch(transactionsTable, searchButton, messages, test.adapter)

      expect(transactionsSearch.table).toBe(transactionsTable)
      expect(transactionsSearch.searchButton).toBe(searchButton)
      expect(transactionsSearch.messageDiv).toBe(messages)

      await test.userInteraction(page)

      expect(test.adapter.called).toBe(test.wantAdapterCalled)

      if (test.wantPage) {
        test.wantPage(page)
      }
    })
  })
})

class FindBalanceAdapterMock {
  called = false

  constructor (expectedValue, expectedError) {
    this.expectedValue = expectedValue
    this.expectedError = expectedError
  }

  async findBalance () {
    this.called = true

    return new Promise((resolve, reject) => {
      if (this.expectedError) {
        reject(this.expectedError)
      } else {
        resolve(this.expectedValue)
      }
    })
  }
}

describe('BalanceValue.autoRefresh()', () => {
  const tests = [
    {
      name: 'should show producer balance when exists',
      adapter: new FindBalanceAdapterMock(1002.90),
      balanceValueOutputId: 'producerBalance',
      userInteraction: (_) => {
        return new Promise((resolve, reject) => setTimeout(resolve, 1000))
      },
      wantPage: (page) => {
        const span = page.window.document.getElementById('producerBalance')
        expect(span.innerHTML).toBe('1.002,90')
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show 0,00 for producer when balance does exists',
      adapter: new FindBalanceAdapterMock(null),
      balanceValueOutputId: 'producerBalance',
      userInteraction: (_) => {
        return new Promise((resolve, reject) => setTimeout(resolve, 1000))
      },
      wantPage: (page) => {
        const span = page.window.document.getElementById('producerBalance')
        expect(span.innerHTML).toBe('0,00')
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show affiliate balance when exists',
      adapter: new FindBalanceAdapterMock(2002.90),
      balanceValueOutputId: 'affiliateBalance',
      userInteraction: (_) => {
        return new Promise((resolve, reject) => setTimeout(resolve, 1000))
      },
      wantPage: (page) => {
        const span = page.window.document.getElementById('affiliateBalance')
        expect(span.innerHTML).toBe('2.002,90')
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show 0,00 for affiliate when balance does exists',
      adapter: new FindBalanceAdapterMock(null),
      balanceValueOutputId: 'affiliateBalance',
      userInteraction: (_) => {
        return new Promise((resolve, reject) => setTimeout(resolve, 1000))
      },
      wantPage: (page) => {
        const span = page.window.document.getElementById('affiliateBalance')
        expect(span.innerHTML).toBe('0,00')
      },
      wantAdapterCalled: true
    },
    {
      name: 'should show error message when error occurs',
      adapter: new FindBalanceAdapterMock(null, new Error('error')),
      balanceValueOutputId: 'producerBalance',
      userInteraction: (_) => {
        return new Promise((resolve, reject) => setTimeout(resolve, 1000))
      },
      wantAdapterCalled: true,
      wantPage: (page) => {
        const messages = page.window.document.getElementById('balanceMessages')
        expect(messages.innerHTML).toBe('Ocorreu um erro inesperado.')
      }
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const html = fs.readFileSync(path.resolve(__dirname, '../index.html'))
      const page = new JSDOM(html)
      const producerBalance = page.window.document.getElementById(test.balanceValueOutputId)
      const messages = page.window.document.getElementById('balanceMessages')
      const producerBalanceValue = new BalanceValue(producerBalance, messages, test.adapter)
      producerBalanceValue.autoRefresh(1, 500)

      expect(producerBalanceValue.valueOutput).toBe(producerBalance)
      expect(producerBalanceValue.messageDiv).toBe(messages)

      await test.userInteraction(page)

      expect(test.adapter.called).toBe(test.wantAdapterCalled)

      if (test.wantPage) {
        test.wantPage(page)
      }
    })
  })
})

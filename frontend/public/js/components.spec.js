/**
 * @jest-environment jsdom
 */

import ImportForm from './components'
import fs from 'fs'
import { JSDOM } from 'jsdom'
import path from 'path'
import userEvent from '@testing-library/user-event'
import { getByText } from '@testing-library/dom'

class MockAdapter {
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

describe('execute()', () => {
  const tests = [
    {
      name: 'should call adapter when file is provided',
      adapter: new MockAdapter({ transactionsCount: 20 }),
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
      adapter: new MockAdapter(),
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
      adapter: new MockAdapter(null, new Error('error')),
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
        expect(messages.innerHTML).toBe('Ocorreu um erro inesperado')
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

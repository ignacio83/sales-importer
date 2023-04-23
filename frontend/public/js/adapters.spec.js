import SalesFileUploadAdapter from './adapters'
import { compose, rest } from 'msw'
import { setupServer } from 'msw/node'

const basePath = 'http://localhost:8080'
describe('execute()', () => {
  const tests = [
    {
      name: 'should return transaction count when backend responds 200',
      setupMockServer: () => {
        const handler = rest.post(`${basePath}/api/v1/sales/upload`, async (req, res, ctx) => {
          return res(ctx.json({
            transactionsCount: 10
          }))
        })
        return setupServer(handler)
      },
      file: new Blob(['a mocked file'], { type: 'text/plain' }),
      expectedJson: {
        transactionsCount: 10
      }
    },
    {
      name: 'should return error with json detail when backend responds 400',
      setupMockServer: () => {
        const handler = rest.post(`${basePath}/api/v1/sales/upload`, (req, res, ctx) => {
          return res(ctx.status(400), ctx.json({
            detail: "Required part 'file' is not present."
          }))
        })
        return setupServer(handler)
      },
      file: new Blob(['a mocked file'], { type: 'text/plain' }),
      expectedError: new Error('Required part \'file\' is not present.')
    },
    {
      name: 'should return error with json detail when backend responds 422',
      setupMockServer: () => {
        const handler = rest.post(`${basePath}/api/v1/sales/upload`, (req, res, ctx) => {
          return res(compose(ctx.status(422), ctx.json({
            detail: 'File sales_empty.txt is empty.'
          })))
        })
        return setupServer(handler)
      },
      file: new Blob(['a mocked file'], { type: 'text/plain' }),
      expectedError: new Error('File sales_empty.txt is empty.')
    },
    {
      name: 'should return error when backend responds 500',
      setupMockServer: () => {
        const handler = rest.post(`${basePath}/api/v1/sales/upload`, (req, res, ctx) => {
          return res(compose(ctx.status(500), ctx.body('Internal server error')))
        })
        return setupServer(handler)
      },
      file: new Blob(['a mocked file'], { type: 'text/plain' }),
      expectedError: new Error('Unable to call upload service')
    },
    {
      name: 'should return error when backend responds 503',
      setupMockServer: () => {
        const handler = rest.post(`${basePath}/api/v1/sales/upload`, (req, res, ctx) => {
          return res(compose(ctx.status(503), ctx.body('Service unavailable')))
        })
        return setupServer(handler)
      },
      file: new Blob(['a mocked file'], { type: 'text/plain' }),
      expectedError: new Error('Unable to call upload service')
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      const adapter = new SalesFileUploadAdapter(basePath, test.timeout)
      if (test.expectedError) {
        await expect(adapter.execute(test.file)).rejects.toEqual(test.expectedError)
      } else {
        await expect(adapter.execute(test.file)).resolves.toEqual(test.expectedJson)
      }
      server.close()
      server.resetHandlers()
    })
  })
})

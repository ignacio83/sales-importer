import {
  AffiliateBalanceAdapter,
  ProducerBalanceAdapter,
  SalesFileUploadAdapter,
  TransactionSearchAdapter
} from './adapters'
import { compose, rest } from 'msw'
import { setupServer } from 'msw/node'

const basePath = 'http://localhost:8080'
describe('SalesFileUploadAdapter.execute()', () => {
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
      expectedError: new Error('Unable to upload file')
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
      expectedError: new Error('Unable to upload file')
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      try {
        const adapter = new SalesFileUploadAdapter(basePath)
        if (test.expectedError) {
          await expect(adapter.execute(test.file)).rejects.toEqual(test.expectedError)
        } else {
          await expect(adapter.execute(test.file)).resolves.toEqual(test.expectedJson)
        }
      } finally {
        server.close()
        server.resetHandlers()
      }
    })
  })
})

describe('TransactionSearchAdapter.findAll()', () => {
  const tests = [
    {
      name: 'should return transactions when backend responds 200',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/sales/transactions`, async (req, res, ctx) => {
          return res(ctx.json([{
            type: 'CommissionPayed',
            productDescription: 'Pencil',
            value: 10.00,
            salesPersonName: 'Walter White',
            date: new Date('2023-03-10T13:30:02Z')
          }]))
        })
        return setupServer(handler)
      },
      expectedJson: [{
        type: 'CommissionPayed',
        productDescription: 'Pencil',
        value: 10.00,
        salesPersonName: 'Walter White',
        date: new Date('2023-03-10T13:30:02Z')
      }]
    },
    {
      name: 'should return error when backend responds 500',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/sales/transactions`, (req, res, ctx) => {
          return res(compose(ctx.status(500), ctx.body('Internal server error')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find all transactions')
    },
    {
      name: 'should return error when backend responds 503',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/sales/transactions`, (req, res, ctx) => {
          return res(compose(ctx.status(503), ctx.body('Service unavailable')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find all transactions')
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      try {
        const adapter = new TransactionSearchAdapter(basePath)
        if (test.expectedError) {
          await expect(adapter.findAll()).rejects.toEqual(test.expectedError)
        } else {
          await expect(adapter.findAll()).resolves.toEqual(test.expectedJson)
        }
      } finally {
        server.close()
        server.resetHandlers()
      }
    })
  })
})

describe('ProducerBalanceAdapter.findBalance()', () => {
  const tests = [
    {
      name: 'should return balance when backend responds 200',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/producers/1/balance`, async (req, res, ctx) => {
          return res(ctx.json({
            value: 12.32
          }))
        })
        return setupServer(handler)
      },
      expected: 12.32
    },
    {
      name: 'should return null when backend responds 404',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/producers/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(404)))
        })
        return setupServer(handler)
      },
      expected: null
    },
    {
      name: 'should return error when backend responds 500',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/producers/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(500), ctx.body('Internal server error')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find producer balance')
    },
    {
      name: 'should return error when backend responds 503',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/producers/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(503), ctx.body('Service unavailable')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find producer balance')
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      try {
        const adapter = new ProducerBalanceAdapter(basePath)
        if (test.expectedError) {
          await expect(adapter.findBalance(1)).rejects.toEqual(test.expectedError)
        } else {
          await expect(adapter.findBalance(1)).resolves.toEqual(test.expected)
        }
      } finally {
        server.close()
        server.resetHandlers()
      }
    })
  })
})

describe('AffiliateBalanceAdapter.findBalance()', () => {
  const tests = [
    {
      name: 'should return balance when backend responds 200',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/affiliates/1/balance`, async (req, res, ctx) => {
          return res(ctx.json({
            value: 12.32
          }))
        })
        return setupServer(handler)
      },
      expected: 12.32
    },
    {
      name: 'should return null when backend responds 404',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/affiliates/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(404)))
        })
        return setupServer(handler)
      },
      expected: null
    },
    {
      name: 'should return error when backend responds 500',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/affiliates/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(500), ctx.body('Internal server error')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find affiliate balance')
    },
    {
      name: 'should return error when backend responds 503',
      setupMockServer: () => {
        const handler = rest.get(`${basePath}/api/v1/affiliates/1/balance`, (req, res, ctx) => {
          return res(compose(ctx.status(503), ctx.body('Service unavailable')))
        })
        return setupServer(handler)
      },
      expectedError: new Error('Unable to find affiliate balance')
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      try {
        const adapter = new AffiliateBalanceAdapter(basePath)
        if (test.expectedError) {
          await expect(adapter.findBalance(1)).rejects.toEqual(test.expectedError)
        } else {
          await expect(adapter.findBalance(1)).resolves.toEqual(test.expected)
        }
      } finally {
        server.close()
        server.resetHandlers()
      }
    })
  })
})

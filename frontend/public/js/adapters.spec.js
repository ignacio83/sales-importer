import SalesFileUploadAdapter from './adapters'
import { rest } from 'msw'
import { setupServer } from 'msw/node'

const basePath = 'http://localhost:8080'
describe('execute()', () => {
  const tests = [
    {
      name: 'should upload sales file when file is provided',
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
    }
  ]

  tests.forEach(test => {
    it(test.name, async () => {
      const server = test.setupMockServer()
      server.listen()

      const adapter = new SalesFileUploadAdapter(basePath)
      const json = await adapter.execute(test.file)

      expect(json).toEqual(test.expectedJson)

      server.close()
    })
  })
})

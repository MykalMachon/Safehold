const request = require('supertest');
const app = require('../app.js');

describe('Misc Tests', () => {
  it('true should equal true', () => {
      expect(true).toEqual(true);
  })
})

describe('GET Endpoints', () => {
  it('should show the test route', async () => {
    const res = await request(app)
      .get('/')
      .send({});
      console.log(res.body);
    expect(res.body.message).toEqual('this is the test route');
  })
})

describe('POST Endpoints', () => {
  it('should echo what I said', async () => {
    const res = await request(app)
      .post('/echo')
      .send({
        message: 'This is my message'
      })
      console.log(res.body);
    expect(res.body.message).toEqual('This is my message')
  })
})
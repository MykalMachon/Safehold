const request = require('supertest');
const app = require('../app.js');

describe('Misc Tests', () => {
  it('true should equal true', () => {
    expect(true).toEqual(true);
  });
});

describe('GET Endpoints : ', () => {
  // ADD IN TESTS FOR GET ENDPOINTS
});

describe('POST Endpoints : ', () => {
  it('should echo what I said', async () => {
    const res = await request(app)
      .post('/echo')
      .send({
        message: 'This is my message',
      });
    console.log(res.body);
    expect(res.body.message).toEqual('This is my message');
  });

  it('should error out for no message', async () => {
    const res = await request(app)
      .post('/echo')
      .send({});
    console.log(res.body);
    expect(res.status).toEqual(500);
  });
});

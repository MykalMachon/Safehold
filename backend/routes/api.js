const express = require('express');
const router = express.Router();

const mapController = require('../controllers/mapController');

/* GET test route. */
router.get('/', mapController.testRoute);

/* POST echo route */
router.post('/echo', mapController.echoRoute)

module.exports = router;

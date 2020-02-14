const express = require('express');
const router = express.Router();

const mapController = require('../controllers/MapController');

/* GET test route. */
router.get('/', mapController.testRoute);

module.exports = router;

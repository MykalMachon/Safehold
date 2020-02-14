const express = require('express');
const router = express.Router();

const mapController = require('../controllers/mapController.js');

// * GET ROUTES

// * POST ROUTES
router.post('/echo', mapController.echoRoute);

module.exports = router;

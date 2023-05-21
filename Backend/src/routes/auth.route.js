const express = require('express');
const router = express.Router();
const { login, register, details } = require('../controllers/auth.controller');
const verifyToken = require('../middlewares/jwt.middlewares');

router.get('/login', login);

router.post('/register', register);

router.get('/details', verifyToken, details);

module.exports = router;
const service = require('../services/auth.service');

async function login(req, res) {
    try {
        await service.login(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function register(req, res) {
    try {
        await service.register(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function details(req, res) {
    try {
        await service.details(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

module.exports = {
    login,
    register,
    details,
}
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

async function createGroup(req, res) {
    try {
        await service.createGroup(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function joinGroup(req, res) {
    try {
        await service.joinGroup(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function getGroupByUserId(req, res) {
    try {
        await service.getGroupByUserId(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function getGroupByGroupId(req, res) {
    try {
        await service.getGroupByGroupId(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function updateGroup(req, res) {
    try {
        await service.updateGroup(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function deleteMember(req, res) {
    try {
        await service.deleteMember(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

async function deleteGroup(req, res) {
    try {
        await service.deleteGroup(req, res);
    } catch (error) {
        res.status(500).json({message: error.message});
    }
}

module.exports = {
    login,
    register,
    details,
    createGroup,
    joinGroup,
    getGroupByUserId,
    getGroupByGroupId,
    updateGroup,
    deleteMember,
    deleteGroup
}

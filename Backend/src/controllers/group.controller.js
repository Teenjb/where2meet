const group = require("../services/group.service");

async function createGroup(req, res) {
  try {
    await group.createGroup(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function joinGroup(req, res) {
  try {
    await group.joinGroup(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function getGroupByUserId(req, res) {
  try {
    await group.getGroupByUserId(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function getGroupByGroupId(req, res) {
  try {
    await group.getGroupByGroupId(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function updateGroup(req, res) {
  try {
    await group.updateGroup(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function deleteMember(req, res) {
  try {
    await group.deleteMember(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function deleteGroup(req, res) {
  try {
    await group.deleteGroup(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  updateGroup,
  deleteMember,
  deleteGroup,
};

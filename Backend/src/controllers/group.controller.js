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

async function getGroupByCode(req, res) {
  try {
    await group.getGroupByCode(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function searchGroup(req, res) {
  try {
    await group.searchGroup(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function filterGroup(req, res) {
  try {
    await group.filterGroup(req, res);
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

async function updateLocation(req, res) {
  try {
    await group.updateLocation(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  getGroupByCode,
  searchGroup,
  filterGroup,
  updateGroup,
  deleteMember,
  deleteGroup,
  updateLocation,
};

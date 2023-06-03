const mood = require("../services/mood.service.js");

async function createMood(req, res) {
  try {
    await mood.createMood(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function getMoods(req, res) {
  try {
    await mood.getMoods(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

async function updateMoods(req, res) {
  try {
    await mood.updateMoods(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
  createMood,
  getMoods,
  updateMoods,
};

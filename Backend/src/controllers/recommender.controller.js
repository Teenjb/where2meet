const recommend = require("../services/recommender.service.js");

async function getRecommendation(req, res) {
  try {
    await recommend.getRecommendation(req, res);
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
    getRecommendation,
};
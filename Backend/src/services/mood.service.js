const { User, Group, UserGroup, Mood } = require("../models/models.js");

async function createMood(req, res) {
  try {
    const { name, displayName } = req.body;

    const newMood = await Mood.create({
      name: name,
      displayName: displayName,
    });

    return res
      .status(201)
      .json({ message: "Mood created", data: { mood: newMood } });
  } catch (error) {
    console.error("Error creating mood:", error);
    return res.status(500).json({ error: "Failed to create mood" });
  }
}

async function getMoods(req, res) {
    try {
        const moods = await Mood.findAll({
            attributes: ["id", "name", "displayName"],
        });
        return res.status(200).json({ message: "Moods found", data: moods });
    } catch (error) {
        console.error("Error getting moods:", error);
        return res.status(500).json({ error: "Failed to get moods" });
    }
}

async function updateMoods(req, res) {
    try {
        const { id, name, displayName } = req.body;
    } catch (error) {
        console.error("Error updating mood:", error);
        return res.status(500).json({ error: "Failed to update mood" });
    }
}

module.exports = {
    createMood,
    getMoods,
};
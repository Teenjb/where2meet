const { User, Group, UserGroup, Mood, UserGroupMood } = require("../models/models.js");

async function createMood(req, res) {
  try {
    const { name, displayText } = req.body;

    const newMood = await Mood.create({
      name: name,
      displayText: displayText,
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
      attributes: ["id", "name", "displayText"],
    });
    return res.status(200).json({ message: "Moods found", data: moods });
  } catch (error) {
    console.error("Error getting moods:", error);
    return res.status(500).json({ error: "Failed to get moods" });
  }
}

async function updateMoods(req, res) {
  try {
    const { moods } = req.body;
    const userId = req.user;
    const { groupId } = req.params;
    var arrMoods = JSON.parse(moods);

    arrMoods.forEach((element) => {
      Mood.findOne({ where: { id: element } }).then(function (mood) {
        UserGroup.findOne({ where: { UserId: userId, GroupId: groupId } }).then(
          function (userGroup) {
            UserGroupMood.create({
              UserGroupId: userGroup.id,
              MoodId: mood.id,
          })}
        );
      });
    });

    res.status(200).json({ message: "Moods updated" });
  } catch (error) {
    console.error("Error updating mood:", error);
    return res.status(500).json({ error: "Failed to update mood" });
  }
}

module.exports = {
  createMood,
  getMoods,
  updateMoods,
};

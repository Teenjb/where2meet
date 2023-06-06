const {
  User,
  Group,
  UserGroup,
  Mood,
  UserGroupMood,
} = require("../models/models.js");
const { centerPoint } = require("../utils/recommender.util.js");
const axios = require("axios");

async function getRecommendation(req, res) {
  const { groupId } = req.params;
  try {
    const group = await Group.findOne({
      where: {
        id: groupId,
      },
      include: [
        {
          model: UserGroup,
          as: "users",
          attributes: ["long", "lat"],
          include: [
            {
              model: User,
              attributes: ["id", "username"],
            },
            {
              model: Mood,
              as: "moods",
              through: {
                attributes: [],
              },
              attributes: ["id", "name", "displayText"],
            },
          ],
        },
      ],
    });
    if (!group) {
      return res.status(404).json({ message: "Group not found" });
    }
    var locations = [];
    var moods = [];
    var mappedRepsonse = [];
    var counter = 1;

    const user = group.users;

    user.forEach((element) => {
      if (element.long == null && element.lat == null) {
        return res.status(404).json({ message: "Location not completed" });
      } else if (element.moods.length == 0) {
        return res.status(404).json({ message: "Mood not completed" });
      } else {
        locations.push({
          x: parseFloat(element.lat),
          y: parseFloat(element.long),
        });
        element.moods.forEach((mood) => {
          moods.push(mood.name);
        });
      }
    });

    const center = centerPoint(locations);

    console.log(locations);
    console.log(centerPoint(locations));
    console.log(moods);

    const response = await axios.post('https://tes1-wtlln4sbra-uc.a.run.app', {
        "latitude": center.x,
        "longitude": center.y,
        "keywords": moods
    });

    console.log(response.data);

    const mapping = await response.data.forEach(async (element) => {
        mappedRepsonse.push({
            rank: counter++,
            locations: {
                id: element.id,
                name: element.Nama,
                imageLink: null,
                long: element.Lang,
                lat: element.Lat,
            }
        })
    })

    res.status(200).json({ message: "Group recommendation generated", data: mappedRepsonse });

  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
  getRecommendation,
};

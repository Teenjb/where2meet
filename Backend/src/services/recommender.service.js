const {
  User,
  Group,
  UserGroup,
  Mood,
  UserGroupMood,
} = require("../models/models.js");
const { centerPoint } = require("../utils/recommender.util.js");
const axios = require("axios");

async function getGroupDetail(id) {
  var result = await Group.findOne({
    where: {
      id: id,
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
  const adminId = await result.adminId;
  const users = await result.users;
  const mappedUsers = [];

  return Promise.all(
    users.map(async (user) => {
      if (user.User.id == adminId) {
        mappedUsers.unshift(user);
      } else {
        mappedUsers.push(user);
      }
    })
  ).then(() => {
    const modifiedResult = {
      id: result.id,
      adminId: result.adminId,
      name: result.name,
      code: result.code,
      status: result.status,
      result: result.result,
      generatedAt: result.generatedAt,
      createdAt: result.createdAt,
      updatedAt: result.updatedAt,
      users: mappedUsers,
    };
    return modifiedResult;
  });
}

async function getRecommendation(req, res) {
  const { groupId } = req.params;
  try {
    const group = await getGroupDetail(groupId);
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

    const center = await centerPoint(locations);

    const response = await axios.post("https://tes1-wtlln4sbra-uc.a.run.app", {
      latitude: center.x,
      longitude: center.y,
      keywords: moods,
    });

    const mapping = await response.data.forEach(async (element) => {
      mappedRepsonse.push({
        rank: counter++,
        locations: {
          id: 1,
          name: element.Nama,
          imageLink: null,
          long: element.Lang,
          lat: element.Lat,
        },
      });
    });

    const updateGroup = await Group.update(
      {
        status: "Done",
        generatedAt: new Date(),
        result: mappedRepsonse,
      },
      {
        where: {
          id: groupId,
        },
        returning: true,
      }
    );

    const result = await getGroupDetail(groupId);

    res
      .status(200)
      .json({ message: "Group recommendation generated", data: result });
  } catch (error) {
    res.status(500).json({ message: error.message });
  }
}

module.exports = {
  getRecommendation,
};

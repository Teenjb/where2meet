const { User, Group, UserGroup, Mood } = require("../models/models.js");
const { generateCode } = require("../utils/group.util.js");
const { Op, Association } = require("sequelize");

async function getGroupDetail(id) {
  return await Group.findOne({
    where: {
      id: id,
    },
    include: [{
      model: UserGroup,
      as: "users",
      attributes: ["lang", "lat"],
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
      ]
    }],
  });
}

async function createGroup(req, res) {
  try {
    const userId = req.user;
    const code = await generateCode();

    const existingGroup = await Group.findOne({
      where: {
        name: code,
      },
    });

    if (existingGroup) {
      return res.status(409).json({ message: "Group already exists" });
    }

    const group = await Group.create({
      name: code,
      code: code,
      adminId: userId,
    });

    const userGroup = await UserGroup.create({
      UserId: userId,
      GroupId: group.id,
    });

    const getGroup = await getGroupDetail(group.id);

    return res.status(201).json({ message: "Group created", data: { group: getGroup } });
  } catch (error) {
    console.error("Error creating group:", error);
    return res.status(500).json({ error: "Failed to create group" });
  }
}

// confirmation
// async function confirmation(req, res){

// }

async function joinGroup(req, res) {
  try {
    const { code } = req.body;
    const userId = req.user;

    const group = await Group.findOne({
      where: {
        code: code,
      },
    });

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      const existingUserGroup = await UserGroup.findOne({
        where: {
          UserId: userId,
          GroupId: group.id,
        },
      });

      if (existingUserGroup) {
        return res.status(409).json({ message: "Already joined group" });
      } else {
        const newUserGroup = await UserGroup.create({
          UserId: userId,
          GroupId: group.id,
        });

        const getGroup = await getGroupDetail(group.id);

        return res
          .status(200)
          .json({ message: "Group found", data: getGroup });
      }
    }
  } catch (error) {
    console.error("Error joining group:", error);
    return res.status(500).json({ error: "Failed to join group" });
  }
}

async function getGroupByUserId(req, res) {
  const { pageSize, pageNumber } = req.query;
  try {
    const userId = req.user;
    const user = await User.findOne({
      where: {
        id: userId,
      },
    });

    const countGroup = await user.countGroups();

    const groups = await user.getGroups({
      include: [{
        model: User,
        attributes: ["id", "username"],
        through: {attributes: []}
      }],
      attributes: ["id", "name", "status"],
      limit: pageSize,
      offset: (pageNumber - 1) * pageSize,
    });

    // remove the UserGroup attribute
    groups.filter((group) => {
      delete group.dataValues.UserGroup;
    });

    if (groups === null || !groups) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({
        message: "Group found",
        data: {
          totalPage: Math.ceil(countGroup / pageSize),
          pageNumber: pageNumber,
          pageSize: pageSize,
          Groups: groups,
        },
      });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function getGroupByGroupId(req, res) {
  try {
    const { groupId } = req.params;
    const group = await getGroupDetail(groupId)

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", data: { group } });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function getGroupByCode(req, res) {
  const { code } = req.query;
  try {
    const group = await Group.findOne({
      where: {
        code: code,
      },
    });

    if (group === null || !group || group === []) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", data: { group } });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function searchGroup(req, res) {
  try {
    const { name } = req.query;
    const group = await Group.findAll({
      where: {
        name: {
          [Op.iLike]: `%${name}%`,
        },
      },
    });

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", data: { group } });
    }
  } catch (error) {
    console.error("Error searching group:", error);
    return res.status(500).json({ error: "Failed to search group" });
  }
}

async function filterGroup(req, res) {
  try {
    const { status, userId } = req.query;
    let group = null;
    // Filter based on user id available or not
    if (userId === null || !userId) {
      // Get all group
      group = await Group.findAll({
        where: {
          status: status,
        },
      });
    } else {
      // Get group based on user id
      group = await Group.findAll({
        include: [
          {
            model: User,
            where: {
              id: userId,
            },
            attributes: {
              exclude: ["password", "createdAt", "updatedAt", "UserGroup"],
            },
          },
        ],
        where: {
          status: status,
        },
      });
    }

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", data: { group } });
    }
  } catch (error) {
    console.error("Error filtering group:", error);
    return res.status(500).json({ error: "Failed to filter group" });
  }
}

async function updateGroup(req, res) {
  try {
    const { groupId, name, status, result } = req.body;
    const group = await Group.update(
      {
        name: name,
        status: status ? status : "Pending",
        result: result ? result : null,
      },
      {
        where: {
          id: groupId,
        },
        returning: true,
        plain: true,
      }
    );
    const data = group[1].dataValues;
    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res
        .status(200)
        .json({ message: "Group updated", data });
    }
  } catch (error) {
    console.error("Error updating group:", error);
    return res.status(500).json({ error: "Failed to update group" });
  }
}

async function deleteMember(req, res) {
  try {
    const { groupId, userId } = req.body;

    const userGroup = await UserGroup.destroy({
      where: {
        GroupId: groupId,
        UserId: userId,
        isAdmin: false,
      },
    });

    if (userGroup === null || !userGroup) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Member deleted", userGroup });
    }
  } catch (error) {
    console.error("Error deleting member:", error);
    return res.status(500).json({ error: "Failed to delete member" });
  }
}

async function deleteGroup(req, res) {
  try {
    const { groupId } = req.body;
    const userId = req.user;

    const isAdmin = await UserGroup.findOne({
      where: {
        GroupId: groupId,
        UserId: userId,
        isAdmin: true,
      },
    });

    if (isAdmin === null || !isAdmin) {
      return res.status(401).json({ message: "Unauthorized" });
    } else {
      const userGroup = await UserGroup.destroy({
        where: {
          GroupId: groupId,
        },
      });

      const group = await Group.destroy({
        where: {
          id: groupId,
        },
      });

      if (userGroup === null || !userGroup) {
        return res.status(404).json({ message: "Group not found" });
      } else {
        return res
          .status(200)
          .json({ message: "Group deleted", group, userGroup });
      }
    }
  } catch (error) {
    console.error("Error deleting group:", error);
    return res.status(500).json({ error: "Failed to delete group" });
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
};

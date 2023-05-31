const { User, Group, UserGroup } = require("../models/models.js");
const { generateCode } = require("../utils/group.util.js");
const { Op } = require("sequelize");

async function createGroup(req, res) {
  try {
    const { groupName } = req.body;
    const userId = req.user;

    const existingGroup = await Group.findOne({
      where: {
        groupName: groupName,
      },
    });

    if (existingGroup) {
      return res.status(409).json({ message: "Group already exists" });
    }

    const code = await generateCode();

    const group = await Group.create({
      groupName: groupName,
    });

    const userGroup = await UserGroup.create({
      UserId: userId,
      GroupId: group.id,
      code: code,
      isAdmin: true,
    });

    return res
      .status(201)
      .json({ message: "Group created", data: { group, userGroup } });
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

    const userGroup = await UserGroup.findOne({
      where: {
        code: code,
      },
    });

    if (userGroup === null || !userGroup) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      const group = await Group.findOne({
        where: {
          id: userGroup.GroupId,
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
            code: code,
          });

          return res
            .status(200)
            .json({ message: "Group found", data: { group, newUserGroup } });
        }
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

    const groups = await user.getGroups({limit: pageSize, offset: (pageNumber - 1) * pageSize})

    if (groups === null || !groups) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", data: {totalPage : Math.ceil(countGroup/pageSize), pageNumber: pageNumber, pageSize: pageSize, Groups: groups} });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function getGroupByGroupId(req, res) {
  try {
    const { groupId } = req.query;
    const group = await Group.findOne({
      include: [{
        model: User,
        through: {attributes: []},
        attributes: {
          exclude: [
            "password",
            "createdAt",
            "updatedAt",
            "UserGroup",
          ],
        },
      }],
      where: {
        id: groupId,
      },
    });

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

async function searchGroup(req, res) {
  try {
    const { groupName } = req.query;
    const group = await Group.findAll({
      where: {
        groupName: {
          [Op.iLike]: `%${groupName}%`,
        },
      },
    });

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    }
    else {
      return res.status(200).json({ message: "Group found", data: { group } });
    }

  } catch (error) {
    console.error("Error searching group:", error);
    return res.status(500).json({ error: "Failed to search group" });
  }
}

async function updateGroup(req, res) {
  try {
    const { groupId, groupName, status, result } = req.body;
    const group = await Group.update(
      {
        groupName: groupName,
        status: status ? status : "Pending",
        result: result ? result : null,
      },
      {
        where: {
          id: groupId,
        },
      }
    );

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group updated" });
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
      },
    });

    if (userGroup === null || !userGroup) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group updated", userGroup });
    }
  } catch (error) {
    console.error("Error deleting member:", error);
    return res.status(500).json({ error: "Failed to delete member" });
  }
}

async function deleteGroup(req, res) {
  try {
    const { groupId } = req.body;
    const group = await Group.destroy({
      where: {
        id: groupId,
      },
    });

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group deleted", group });
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
  searchGroup,
  updateGroup,
  deleteMember,
  deleteGroup,
};

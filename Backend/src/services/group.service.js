const { User, Group, UserGroup } = require("../models/models.js");

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

    const group = await Group.create({
      groupName: groupName,
    });

    const userGroup = await UserGroup.create({
      UserId: userId,
      GroupId: group.id,
    });

    return res
      .status(201)
      .json({ message: "Group created", data: { group, userGroup } });
  } catch (error) {
    console.error("Error creating group:", error);
    return res.status(500).json({ error: "Failed to create group" });
  }
}

async function joinGroup(req, res) {
  try {
    // Temporary solution
    const {} = req.body;
  } catch (error) {
    console.error("Error joining group:", error);
    return res.status(500).json({ error: "Failed to join group" });
  }
}

async function getGroupByUserId(req, res) {
  try {
    const userId = req.user;
    const userGroup = await UserGroup.findAll({
      where: {
        UserId: userId,
      },
    });

    if (userGroup === null || !userGroup) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res
        .status(200)
        .json({ message: "Group found", data: { userGroup } });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function getGroupByGroupId(req, res) {
  try {
    const { groupId } = req.query;
    const group = await Group.findAll({
      where: {
        id: groupId,
      },
    });

    if (group === null || !group) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res
        .status(200)
        .json({ message: "Group found", data: { group } });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
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
  updateGroup,
  deleteMember,
  deleteGroup,
};

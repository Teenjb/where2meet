const { User, Group, UserGroup } = require("../models/models.js");
const { checkRegex, hashPassword, comparePassword } = require("../utils/auth.util.js");
const jwt = require("jsonwebtoken");

async function login(req, res) {
  const { username, password } = req.body;
  // Check if username and password are valid
  try {
    await checkRegex(username, null, password, "login");
    // If valid, check if user exists
    const user = await User.findOne({
      where: {
        username: username,
      },
    });
    // If user exists, check if password matches
    if (!user) {
      throw new Error("User does not exist");
    }
    if (await comparePassword(password, user.password)) {
      const token = jwt.sign({ id: user.id }, process.env.JWT_SECRET);
      res.status(200).json({ message: "Login successful", token: token });
    } else {
      res.status(401).json({ message: "Login failed" });
    }
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
}

async function register(req, res) {
  const { username, password, email } = req.body;
  // Check if username, email, and password are valid
  try {
    await checkRegex(username, email, password, "register");

    const existingUser = await User.findOne({
      where: {
        username: username,
      },
    });

    if (existingUser) {
      res.status(409).json({ message: "Username already exists" });
    } else {
      const hashedPassword = await hashPassword(password);

      const newUser = await User.create({
        username: username,
        password: hashedPassword,
        email: email,
      });

      res.status(201).json({ message: "User created" });
    }
  } catch (error) {
    res.status(400).json({ message: error.message });
  }
}

async function details(req, res) {
  const id = req.user;
  const user = await User.findOne({
    where: {
      id: id,
    },
  });
  if (!user) {
    res.status(404).json({ message: "User not found" });
  } else {
    res.status(200).json({ message: "User found", user: user });
  }
}

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

    return res.status(201).json({ message: "Group created", group, userGroup });
  } catch (error) {
    console.error("Error creating group:", error);
    return res.status(500).json({ error: "Failed to create group" });
  }
}

async function joinGroup(req, res) {
  try {
    // Temporary solution
    const {  } = req.body;

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
      return res.status(200).json({ message: "Group found", userGroup });
    }
  } catch (error) {
    console.error("Error getting group:", error);
    return res.status(500).json({ error: "Failed to get group" });
  }
}

async function getGroupByGroupId(req, res) {
  try {
    const { groupId }= req.body;
    const userGroup = await UserGroup.findAll({
      where: {
        GroupId: groupId,
      },
    });

    if (userGroup === null || !userGroup) {
      return res.status(404).json({ message: "Group not found" });
    } else {
      return res.status(200).json({ message: "Group found", userGroup });
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
      return res.status(200).json({ message: "Group updated", group });
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
  login,
  register,
  details,
  createGroup,
  joinGroup,
  getGroupByUserId,
  getGroupByGroupId,
  updateGroup,
  deleteMember,
  deleteGroup,
}
